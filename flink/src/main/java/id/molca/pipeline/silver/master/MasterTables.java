package id.molca.pipeline.silver.master;

import java.util.LinkedHashMap;
import java.util.Map;

/** The master tables Flink keeps live via CDC (publication flink_master_pub) and their primary-key columns. */
public final class MasterTables {
    private MasterTables() {}

    public static final Map<String, String> PK = new LinkedHashMap<>();
    static {
        PK.put("asset_tags", "tag_id");
        PK.put("asset_placement", "asset_placement_id");
        PK.put("asset", "asset_id");
        PK.put("work_unit", "work_unit_id");
        PK.put("work_center", "work_center_id");
        PK.put("area", "area_id");
        PK.put("site", "site_id");
        PK.put("site_shift", "site_shift_id");
        PK.put("shift_instance", "shift_instance_id");
        PK.put("work_unit_kpi_binding", "binding_id");
        PK.put("asset_product_codes", "asset_product_code_id");
        PK.put("downtime_reason", "downtime_reason_id");
        PK.put("reject_reason", "reject_reason_id");
        PK.put("product", "product_id");
    }

    public static String[] qualifiedTableList(String schema) {
        return PK.keySet().stream().map(t -> schema + "." + t).toArray(String[]::new);
    }
}
