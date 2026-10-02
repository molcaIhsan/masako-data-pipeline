package id.molca.pipeline.silver.parse;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import id.molca.pipeline.silver.master.MasterTables;
import id.molca.pipeline.silver.model.MasterChange;
import id.molca.pipeline.silver.util.Json;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Debezium SourceRecord -> MasterChange, without Kafka Connect's JsonConverter (that converter targets kafka-clients
 * 3.x and breaks next to the Kafka connector's 4.x). Row values: dates as epoch days (int), timestamptz as ISO text
 * (ZonedTimestamp), numeric as text (decimal.handling.mode=string).
 */
public class MasterChangeDeserializer implements DebeziumDeserializationSchema<MasterChange> {

    @Override
    public void deserialize(SourceRecord record, Collector<MasterChange> out) {
        if (!(record.value() instanceof Struct env)) return;              // tombstones / heartbeats
        if (env.schema().field("op") == null) return;
        String op = env.getString("op");
        Struct source = env.getStruct("source");
        String table = source == null ? null : source.getString("table");
        if (table == null || !MasterTables.PK.containsKey(table)) return;
        boolean delete = "d".equals(op);
        Struct row = delete ? env.getStruct("before") : env.getStruct("after");
        if (row == null) return;
        ObjectNode json = toJson(row);
        String pk = json.path(MasterTables.PK.get(table)).asText(null);
        if (pk == null) return;
        out.collect(new MasterChange(table, pk, delete, delete ? null : json.toString()));
    }

    static ObjectNode toJson(Struct s) {
        ObjectNode o = Json.MAPPER.createObjectNode();
        for (Field f : s.schema().fields()) put(o, f.name(), s.get(f), f.schema().name());
        return o;
    }

    private static void put(ObjectNode o, String name, Object v, String logical) {
        if (v == null) o.putNull(name);
        else if (v instanceof java.util.Date d) {
            // Kafka Connect logical types (time.precision.mode=connect): Date -> epoch day, Timestamp/Time -> epoch ms
            if ("org.apache.kafka.connect.data.Date".equals(logical)) o.put(name, Math.floorDiv(d.getTime(), 86_400_000L));
            else o.put(name, d.getTime());
        }
        else if (v instanceof Integer i) o.put(name, i);
        else if (v instanceof Long l) o.put(name, l);
        else if (v instanceof Short sh) o.put(name, sh);
        else if (v instanceof Double d) o.put(name, d);
        else if (v instanceof Float fl) o.put(name, fl);
        else if (v instanceof Boolean b) o.put(name, b);
        else if (v instanceof BigDecimal bd) o.put(name, bd);
        else if (v instanceof Struct st) o.set(name, toJson(st));
        else if (v instanceof Collection<?> c) {
            ArrayNode a = o.putArray(name);
            for (Object x : c) a.add(x == null ? null : x.toString());
        } else if (v instanceof ByteBuffer || v instanceof byte[]) o.putNull(name);   // not used by the job
        else o.put(name, v.toString());
    }

    @Override
    public TypeInformation<MasterChange> getProducedType() { return TypeInformation.of(MasterChange.class); }
}
