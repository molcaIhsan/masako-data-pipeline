package id.molca.pipeline.silver.dlq;

import id.molca.pipeline.silver.model.DlqRecord;
import id.molca.pipeline.silver.util.Json;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;

/** DLQ record -> JSON bytes (a named class: the Kafka sink's lineage needs concrete types, not a lambda). */
public class DlqSerializer implements SerializationSchema<DlqRecord> {
    @Override
    public byte[] serialize(DlqRecord r) { return Json.write(r).getBytes(StandardCharsets.UTF_8); }
}
