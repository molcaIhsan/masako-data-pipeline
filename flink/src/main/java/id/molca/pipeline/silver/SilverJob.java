package id.molca.pipeline.silver;

import id.molca.pipeline.silver.dlq.DlqSerializer;
import id.molca.pipeline.silver.lane.LaneProcessor;
import id.molca.pipeline.silver.master.MasterBroadcast;
import id.molca.pipeline.silver.master.MasterTables;
import id.molca.pipeline.silver.model.DlqRecord;
import id.molca.pipeline.silver.model.FactRow;
import id.molca.pipeline.silver.model.MasterChange;
import id.molca.pipeline.silver.model.RawReading;
import id.molca.pipeline.silver.model.Reading;
import id.molca.pipeline.silver.model.StallInput;
import id.molca.pipeline.silver.model.StopRow;
import id.molca.pipeline.silver.parse.MasterChangeDeserializer;
import id.molca.pipeline.silver.parse.TelegrafParser;
import id.molca.pipeline.silver.resolve.ResolveFunction;
import id.molca.pipeline.silver.sink.PgUpsertSink;
import id.molca.pipeline.silver.sink.SilverStatements;
import id.molca.pipeline.silver.stall.StallDetector;
import id.molca.pipeline.silver.util.Json;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.cdc.connectors.base.options.StartupOptions;
import org.apache.flink.cdc.connectors.postgres.source.PostgresSourceBuilder;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

/**
 * Kafka (Telegraf machine_metrics) -> silver.production_events / reject_events / downtime_events.
 * Master data arrives live through Postgres CDC (publication flink_master_pub) as broadcast state.
 * Checkpoints hold Kafka offsets, the CDC WAL position and all operator state, so a restart resumes exactly where
 * it stopped. Unusable messages go to the DLQ topic with a reason. See Documents/05_contracts/pipeline_contract.md.
 *
 * Cluster-level settings (state backend rocksdb, incremental checkpoints, checkpoint dir, retention on cancel)
 * live in the Flink configuration (docker/flink/config), not in code.
 */
public class SilverJob {

    public static void main(String[] args) throws Exception {
        JobConfig cfg = JobConfig.fromEnv();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(cfg.parallelism);
        env.enableCheckpointing(cfg.checkpointIntervalMs);

        // ---------- master data: Postgres CDC -> broadcast ----------
        Properties dbz = new Properties();
        dbz.setProperty("publication.name", cfg.cdcPublication);
        dbz.setProperty("publication.autocreate.mode", "disabled");   // created by db/cdc/enable_cdc.sh
        dbz.setProperty("decimal.handling.mode", "string");
        dbz.setProperty("time.precision.mode", "connect");
        var cdcSource = PostgresSourceBuilder.PostgresIncrementalSource.<MasterChange>builder()
                .hostname(cfg.pgHost).port(cfg.pgPort).database(cfg.pgDatabase)
                .schemaList(cfg.cdcSchema).tableList(MasterTables.qualifiedTableList(cfg.cdcSchema))
                .username(cfg.cdcUser).password(cfg.cdcPassword)
                .slotName(cfg.cdcSlot).decodingPluginName("pgoutput")
                .deserializer(new MasterChangeDeserializer())
                .startupOptions(StartupOptions.initial())
                .debeziumProperties(dbz)
                .build();
        DataStream<MasterChange> master = env
                .fromSource(cdcSource, idleWatermarks(), "master-cdc").setParallelism(1);
        BroadcastStream<MasterChange> masterBc = master.broadcast(MasterBroadcast.DESCRIPTOR);

        // ---------- machine data: Kafka -> parse ----------
        var kafkaBuilder = KafkaSource.<String>builder()
                .setBootstrapServers(cfg.kafkaBootstrap)
                .setTopics(cfg.kafkaTopic)
                .setGroupId(cfg.kafkaGroupId)
                .setStartingOffsets(OffsetsInitializer.committedOffsets(
                        "latest".equalsIgnoreCase(cfg.kafkaStartingOffsets) ? OffsetResetStrategy.LATEST : OffsetResetStrategy.EARLIEST))
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setProperties(cfg.kafkaSecurity);
        // event time is assigned IN the source: Kafka keeps one watermark per partition, so a fast partition can't
        // push the data of a slow one behind the watermark
        DataStream<String> rawJson = env.fromSource(kafkaBuilder.build(),
                WatermarkStrategy.<String>forBoundedOutOfOrderness(Duration.ofMillis(cfg.latenessMs))
                        .withTimestampAssigner((msg, kafkaTs) -> TelegrafParser.eventTimeOrFallback(msg, kafkaTs))
                        .withIdleness(Duration.ofMinutes(1)),
                "kafka-machine-metrics");

        SingleOutputStreamOperator<RawReading> parsed = rawJson.process(new ProcessFunction<String, RawReading>() {
            @Override
            public void processElement(String msg, Context ctx, Collector<RawReading> out) {
                try { out.collect(TelegrafParser.parse(msg)); }
                catch (Exception e) { ctx.output(Tags.DLQ, DlqRecord.of("parse_error", e.getMessage(), null, msg, null)); }
            }
        }).name("parse");

        // ---------- resolve (keyed by source_tag) ----------
        SingleOutputStreamOperator<Reading> resolved = parsed
                .keyBy(r -> r.sourceTag, Types.STRING)
                .connect(masterBc)
                .process(new ResolveFunction(cfg.unresolvedRetryMs)).name("resolve");

        // ---------- lanes: counters + product (keyed by asset|work unit) ----------
        SingleOutputStreamOperator<FactRow> facts = resolved
                .keyBy(Reading::laneKey, Types.STRING)
                .connect(masterBc)
                .process(new LaneProcessor()).name("lane");

        // ---------- stops (keyed by work unit) ----------
        SingleOutputStreamOperator<StopRow> stops = facts.getSideOutput(Tags.STALL)
                .keyBy(s -> s.workUnitId, Types.LONG)
                .connect(masterBc)
                .process(new StallDetector()).name("stall");

        // ---------- sinks ----------
        facts.sinkTo(new PgUpsertSink<>(cfg.jdbcUrl(), cfg.pgUser, cfg.pgPassword, SilverStatements.factSql(),
                SilverStatements.FACT_BINDER, f -> f.tagId + "|" + f.eventTimeMs, cfg.sinkBatchSize, cfg.sinkFlushMs)).name("silver-facts");
        stops.sinkTo(new PgUpsertSink<>(cfg.jdbcUrl(), cfg.pgUser, cfg.pgPassword, SilverStatements.stopSql(),
                SilverStatements.STOP_BINDER, r -> r.sourceEventId, 50, 200)).name("silver-downtime");

        DataStream<DlqRecord> dlq = parsed.getSideOutput(Tags.DLQ)
                .union(resolved.getSideOutput(Tags.DLQ), facts.getSideOutput(Tags.DLQ));
        dlq.sinkTo(KafkaSink.<DlqRecord>builder()
                .setBootstrapServers(cfg.kafkaBootstrap)
                .setKafkaProducerConfig(cfg.kafkaSecurity)
                .setRecordSerializer(KafkaRecordSerializationSchema.<DlqRecord>builder()
                        .setTopic(cfg.kafkaDlqTopic)
                        .setValueSerializationSchema(new DlqSerializer())
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build()).name("dlq");

        env.execute("molcadx-silver");
    }

    /**
     * The master stream carries no event time and must never move the event-time clock. It declares itself IDLE, so
     * connected operators take their watermark from Kafka alone; when Kafka is idle too, event time stands still
     * (no data = no stop decisions). Emitting Long.MAX_VALUE here would fast-forward every timer when Kafka idles.
     */
    private static <T> WatermarkStrategy<T> idleWatermarks() {
        return ctx -> new WatermarkGenerator<T>() {
            @Override public void onEvent(T e, long ts, WatermarkOutput out) { }
            @Override public void onPeriodicEmit(WatermarkOutput out) { out.markIdle(); }
        };
    }
}
