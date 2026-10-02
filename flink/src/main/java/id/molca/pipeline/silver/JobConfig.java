package id.molca.pipeline.silver;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/** All settings come from environment variables (see .env.example). Nothing environment-specific is hard-coded. */
public class JobConfig implements Serializable {
    // Kafka (machine data)
    public String kafkaBootstrap, kafkaTopic, kafkaGroupId, kafkaDlqTopic, kafkaStartingOffsets;
    public Properties kafkaSecurity = new Properties();
    // Postgres (silver writes)
    public String pgHost, pgDatabase, pgUser, pgPassword;
    public int pgPort;
    // Postgres CDC (master data)
    public String cdcUser, cdcPassword, cdcSlot, cdcPublication, cdcSchema;
    // behaviour
    public long latenessMs, unresolvedRetryMs, checkpointIntervalMs;
    public int parallelism, sinkBatchSize;
    public long sinkFlushMs;

    public static JobConfig fromEnv() { return from(System.getenv()); }

    public static JobConfig from(Map<String, String> env) {
        JobConfig c = new JobConfig();
        c.kafkaBootstrap = req(env, "KAFKA_BOOTSTRAP_SERVERS");
        c.kafkaTopic = req(env, "KAFKA_TOPIC");
        c.kafkaGroupId = get(env, "KAFKA_GROUP_ID", "flink-silver");
        c.kafkaDlqTopic = get(env, "KAFKA_DLQ_TOPIC", c.kafkaTopic + "_dlq");
        c.kafkaStartingOffsets = get(env, "KAFKA_STARTING_OFFSETS", "earliest");   // earliest | latest (first deploy only)
        // optional security: any KAFKA_PROP_<name> becomes a Kafka client property (dots as underscores, lower case)
        for (Map.Entry<String, String> e : env.entrySet()) {
            if (e.getKey().startsWith("KAFKA_PROP_")) {
                c.kafkaSecurity.setProperty(e.getKey().substring("KAFKA_PROP_".length()).toLowerCase().replace('_', '.'), e.getValue());
            }
        }
        c.pgHost = req(env, "PG_HOST");
        c.pgPort = Integer.parseInt(get(env, "PG_PORT", "5432"));
        c.pgDatabase = req(env, "PG_DATABASE");
        c.pgUser = req(env, "FLINK_PG_USER");
        c.pgPassword = req(env, "FLINK_PG_PASSWORD");
        c.cdcUser = get(env, "FLINK_CDC_USER", "flink_cdc");
        c.cdcPassword = req(env, "FLINK_CDC_PASSWORD");
        c.cdcSlot = get(env, "FLINK_CDC_SLOT", "flink_master_slot");
        c.cdcPublication = get(env, "FLINK_CDC_PUBLICATION", "flink_master_pub");
        c.cdcSchema = get(env, "FLINK_CDC_SCHEMA", "silver");
        c.latenessMs = 1000L * Long.parseLong(get(env, "WATERMARK_LATENESS_SECONDS", "60"));
        c.unresolvedRetryMs = 1000L * Long.parseLong(get(env, "UNRESOLVED_RETRY_SECONDS", "300"));
        c.checkpointIntervalMs = 1000L * Long.parseLong(get(env, "CHECKPOINT_INTERVAL_SECONDS", "60"));
        c.parallelism = Integer.parseInt(get(env, "FLINK_PARALLELISM", "2"));
        c.sinkBatchSize = Integer.parseInt(get(env, "SINK_BATCH_SIZE", "500"));
        c.sinkFlushMs = Long.parseLong(get(env, "SINK_FLUSH_MS", "1000"));
        return c;
    }

    public String jdbcUrl() { return "jdbc:postgresql://" + pgHost + ":" + pgPort + "/" + pgDatabase; }

    private static String req(Map<String, String> env, String k) {
        String v = env.get(k);
        if (v == null || v.isBlank()) throw new IllegalArgumentException("missing environment variable " + k);
        return v;
    }

    private static String get(Map<String, String> env, String k, String def) {
        String v = env.get(k);
        return (v == null || v.isBlank()) ? def : v;
    }
}
