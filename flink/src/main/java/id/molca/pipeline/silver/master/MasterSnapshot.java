package id.molca.pipeline.silver.master;

import com.fasterxml.jackson.databind.JsonNode;
import id.molca.pipeline.silver.util.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable, indexed view of the master rows (built from Flink broadcast state, which CDC keeps current).
 * Every lookup is AS-OF: dates (epoch day) for date-typed validity, instants (ms) for timestamptz validity.
 * Validity is half-open: valid_from <= x AND (valid_to IS NULL OR valid_to > x).
 */
public final class MasterSnapshot {

    // ---- row records (only the columns the job needs) ----
    public static final class Tag {
        public long tagId, assetId; public Long workUnitId; public String node, role, kind, countBasis;
        public Long uomId, rejectReasonId, downtimeReasonId; public Integer validFrom, validTo;
    }
    public static final class Placement {
        public long assetId, workUnitId; public String role; public Integer validFrom, validTo;
    }
    public static final class Shift {
        public long shiftInstanceId, siteId, startMs, endMs; public int businessDate;
    }
    public static final class Binding {
        public long bindingId, workUnitId, tagId; public String slot, transform; public Integer validFrom, validTo;
    }
    public static final class ProductCode {
        public long tagId, productId; public String rawValue; public Long validFromMs, validToMs;
    }

    private final Map<String, List<Tag>> tagsByNode = new HashMap<>();
    private final Map<Long, Tag> tagById = new HashMap<>();
    private final Map<Long, List<Placement>> placementsByAsset = new HashMap<>();
    private final Map<Long, Long> wuToWc = new HashMap<>();
    private final Map<Long, Long> wcToArea = new HashMap<>();
    private final Map<Long, Long> areaToSite = new HashMap<>();
    private final Map<Long, String> assetCode = new HashMap<>();
    private final Map<Long, List<Shift>> shiftsBySite = new HashMap<>();
    private final Map<Long, List<Binding>> bindingsByWu = new HashMap<>();
    private final Map<Long, List<ProductCode>> codesByAsset = new HashMap<>();
    private final Map<Long, String> reasonCategory = new HashMap<>();
    private final Map<Long, String> rejectReasonCode = new HashMap<>();
    private final Map<Long, String> productCode = new HashMap<>();
    private final int rowCount;

    /** @param rows key "table|pk" -> row JSON (the broadcast state) */
    public MasterSnapshot(Iterable<Map.Entry<String, String>> rows) {
        Map<Long, Long> siteShiftToSite = new HashMap<>();
        List<JsonNode> shiftRows = new ArrayList<>();
        List<JsonNode> codeRows = new ArrayList<>();
        int n = 0;
        for (Map.Entry<String, String> e : rows) {
            n++;
            String table = e.getKey().substring(0, e.getKey().indexOf('|'));
            JsonNode r = Json.parse(e.getValue());
            switch (table) {
                case "asset_tags" -> {
                    Tag t = new Tag();
                    t.tagId = r.path("tag_id").asLong(); t.assetId = r.path("asset_id").asLong();
                    t.workUnitId = Json.longOrNull(r, "work_unit_id"); t.node = Json.textOrNull(r, "node");
                    t.role = Json.textOrNull(r, "tag_role"); t.kind = Json.textOrNull(r, "kind");
                    t.countBasis = Json.textOrNull(r, "count_basis"); t.uomId = Json.longOrNull(r, "uom_id");
                    t.rejectReasonId = Json.longOrNull(r, "reject_reason_id");
                    t.downtimeReasonId = Json.longOrNull(r, "downtime_reason_id");
                    t.validFrom = Json.epochDayOrNull(r, "valid_from"); t.validTo = Json.epochDayOrNull(r, "valid_to");
                    tagById.put(t.tagId, t);
                    if (t.node != null) tagsByNode.computeIfAbsent(t.node.trim(), k -> new ArrayList<>()).add(t);
                }
                case "asset_placement" -> {
                    Placement p = new Placement();
                    p.assetId = r.path("asset_id").asLong(); p.workUnitId = r.path("work_unit_id").asLong();
                    p.role = Json.textOrNull(r, "placement_role");
                    p.validFrom = Json.epochDayOrNull(r, "valid_from"); p.validTo = Json.epochDayOrNull(r, "valid_to");
                    placementsByAsset.computeIfAbsent(p.assetId, k -> new ArrayList<>()).add(p);
                }
                case "asset" -> assetCode.put(r.path("asset_id").asLong(), Json.textOrNull(r, "asset_tag"));
                case "work_unit" -> wuToWc.put(r.path("work_unit_id").asLong(), r.path("work_center_id").asLong());
                case "work_center" -> wcToArea.put(r.path("work_center_id").asLong(), r.path("area_id").asLong());
                case "area" -> areaToSite.put(r.path("area_id").asLong(), r.path("site_id").asLong());
                case "site_shift" -> siteShiftToSite.put(r.path("site_shift_id").asLong(), r.path("site_id").asLong());
                case "shift_instance" -> shiftRows.add(r);
                case "work_unit_kpi_binding" -> {
                    Binding b = new Binding();
                    b.bindingId = r.path("binding_id").asLong(); b.workUnitId = r.path("work_unit_id").asLong();
                    b.tagId = r.path("asset_tag_id").asLong(); b.slot = Json.textOrNull(r, "slot_id");
                    b.transform = Json.textOrNull(r, "transform");
                    b.validFrom = Json.epochDayOrNull(r, "valid_from"); b.validTo = Json.epochDayOrNull(r, "valid_to");
                    bindingsByWu.computeIfAbsent(b.workUnitId, k -> new ArrayList<>()).add(b);
                }
                case "asset_product_codes" -> codeRows.add(r);
                case "downtime_reason" -> reasonCategory.put(r.path("downtime_reason_id").asLong(), Json.textOrNull(r, "downtime_category"));
                case "reject_reason" -> rejectReasonCode.put(r.path("reject_reason_id").asLong(), Json.textOrNull(r, "code"));
                case "product" -> productCode.put(r.path("product_id").asLong(), Json.textOrNull(r, "code"));
                default -> { }
            }
        }
        for (JsonNode r : shiftRows) {
            Long site = siteShiftToSite.get(r.path("site_shift_id").asLong());
            Long start = Json.instantMsOrNull(r, "start_datetime"), end = Json.instantMsOrNull(r, "end_datetime");
            Integer bd = Json.epochDayOrNull(r, "business_date");
            if (site == null || start == null || end == null || bd == null) continue;
            Shift s = new Shift();
            s.shiftInstanceId = r.path("shift_instance_id").asLong(); s.siteId = site;
            s.startMs = start; s.endMs = end; s.businessDate = bd;
            shiftsBySite.computeIfAbsent(site, k -> new ArrayList<>()).add(s);
        }
        shiftsBySite.values().forEach(l -> l.sort(Comparator.comparingLong(s -> s.startMs)));
        for (JsonNode r : codeRows) {
            ProductCode c = new ProductCode();
            c.tagId = r.path("tag_id").asLong(); c.productId = r.path("product_id").asLong();
            c.rawValue = Json.textOrNull(r, "raw_value");
            c.validFromMs = Json.instantMsOrNull(r, "valid_from"); c.validToMs = Json.instantMsOrNull(r, "valid_to");
            Tag t = tagById.get(c.tagId);
            if (t != null) codesByAsset.computeIfAbsent(t.assetId, k -> new ArrayList<>()).add(c);
        }
        this.rowCount = n;
    }

    public boolean isEmpty() { return rowCount == 0; }

    static boolean validOnDay(Integer from, Integer to, int day) {
        return (from == null || from <= day) && (to == null || to > day);
    }

    // ---- lookups ----

    /** Tag for a node around an instant (loose by +-1 day: the exact business date is checked later). */
    public Tag tagForNode(String node, long tsMs) {
        List<Tag> l = tagsByNode.get(node);
        if (l == null) return null;
        int day = (int) Math.floorDiv(tsMs, 86_400_000L);
        Tag best = null;
        for (Tag t : l) {
            boolean loose = (t.validFrom == null || t.validFrom <= day + 1) && (t.validTo == null || t.validTo > day - 1);
            if (loose && (best == null || (t.validFrom != null && (best.validFrom == null || t.validFrom > best.validFrom)))) best = t;
        }
        return best;
    }

    public Tag tag(long tagId) { return tagById.get(tagId); }

    public boolean tagValidOn(Tag t, int day) { return validOnDay(t.validFrom, t.validTo, day); }

    /** Work units the asset is placed on around an instant (loose by +-1 day). */
    public List<Placement> placementsAround(long assetId, long tsMs) {
        int day = (int) Math.floorDiv(tsMs, 86_400_000L);
        List<Placement> out = new ArrayList<>();
        for (Placement p : placementsByAsset.getOrDefault(assetId, Collections.emptyList())) {
            if ((p.validFrom == null || p.validFrom <= day + 1) && (p.validTo == null || p.validTo > day - 1)) out.add(p);
        }
        return out;
    }

    public boolean placedOn(long assetId, long workUnitId, int day) {
        for (Placement p : placementsByAsset.getOrDefault(assetId, Collections.emptyList())) {
            if (p.workUnitId == workUnitId && validOnDay(p.validFrom, p.validTo, day)) return true;
        }
        return false;
    }

    public Long siteOfWorkUnit(long workUnitId) {
        Long wc = wuToWc.get(workUnitId); if (wc == null) return null;
        Long area = wcToArea.get(wc); if (area == null) return null;
        return areaToSite.get(area);
    }

    public String assetCode(long assetId) { return assetCode.get(assetId); }

    /** The shift instance of the site containing the instant: start <= ts < end. */
    public Shift shiftAt(long siteId, long tsMs) {
        for (Shift s : shiftsBySite.getOrDefault(siteId, Collections.emptyList())) {
            if (s.startMs <= tsMs && tsMs < s.endMs) return s;
        }
        return null;
    }

    /** The first shift of the site starting at or after the instant (for continuing a stop after a gap). */
    public Shift nextShiftFrom(long siteId, long tsMs) {
        for (Shift s : shiftsBySite.getOrDefault(siteId, Collections.emptyList())) {
            if (s.startMs >= tsMs) return s;
        }
        return null;
    }

    /** True if this tag is bound to availability.downtime_reason with transform stall_bucket for the work unit. */
    public boolean isStallWatched(long workUnitId, long tagId, int day) {
        for (Binding b : bindingsByWu.getOrDefault(workUnitId, Collections.emptyList())) {
            if (b.tagId == tagId && "availability.downtime_reason".equals(b.slot) && "stall_bucket".equals(b.transform)
                    && validOnDay(b.validFrom, b.validTo, day)) return true;
        }
        return false;
    }

    /** product_id for a raw PLC code on an asset at an instant (exact string match; codes follow the asset). */
    public Long productForCode(long assetId, String raw, long tsMs) {
        if (raw == null) return null;
        for (ProductCode c : codesByAsset.getOrDefault(assetId, Collections.emptyList())) {
            if (raw.equals(c.rawValue) && (c.validFromMs == null || c.validFromMs <= tsMs)
                    && (c.validToMs == null || c.validToMs > tsMs)) return c.productId;
        }
        return null;
    }

    public String reasonCategory(Long reasonId) { return reasonId == null ? null : reasonCategory.get(reasonId); }
    public String rejectReasonCode(Long id) { return id == null ? null : rejectReasonCode.get(id); }
    public String productCode(Long productId) { return productId == null ? null : productCode.get(productId); }
}
