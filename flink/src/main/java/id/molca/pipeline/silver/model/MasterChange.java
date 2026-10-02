package id.molca.pipeline.silver.model;

/** One master-data row change from Postgres CDC (Debezium JSON), reduced to table / pk / row. */
public class MasterChange {
    public String table;   // e.g. "asset_tags"
    public String pk;      // primary key value as text
    public boolean delete; // true = row removed
    public String rowJson; // the row after the change (null on delete)

    public MasterChange() {}

    public MasterChange(String table, String pk, boolean delete, String rowJson) {
        this.table = table; this.pk = pk; this.delete = delete; this.rowJson = rowJson;
    }

    public String key() { return table + "|" + pk; }
}
