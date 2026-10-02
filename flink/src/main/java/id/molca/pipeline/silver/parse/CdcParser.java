package id.molca.pipeline.silver.parse;

import com.fasterxml.jackson.databind.JsonNode;
import id.molca.pipeline.silver.master.MasterTables;
import id.molca.pipeline.silver.model.MasterChange;
import id.molca.pipeline.silver.util.Json;

/** Debezium JSON change event (from Flink CDC) -> MasterChange. Unknown tables return null. */
public final class CdcParser {
    private CdcParser() {}

    public static MasterChange parse(String json) {
        JsonNode root = Json.parse(json);
        String table = root.path("source").path("table").asText(null);
        if (table == null || !MasterTables.PK.containsKey(table)) return null;
        String op = root.path("op").asText("");
        JsonNode after = root.get("after");
        JsonNode before = root.get("before");
        boolean delete = "d".equals(op);
        JsonNode row = delete ? before : after;
        if (row == null || row.isNull()) return null;
        String pk = row.path(MasterTables.PK.get(table)).asText(null);
        if (pk == null) return null;
        return new MasterChange(table, pk, delete, delete ? null : after.toString());
    }
}
