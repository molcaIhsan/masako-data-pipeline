package id.molca.pipeline.silver.lane;

/**
 * Current product code of one lane with a debounce: a new code is accepted only after it has been seen
 * for at least DEBOUNCE_MS (pipeline_contract F6). The very first code is accepted immediately.
 * Plain POJO so Flink can keep it in ValueState.
 */
public class ProductTracker {
    public static final long DEBOUNCE_MS = 30_000L;

    public String confirmed;       // raw code in force
    public String candidate;       // raw code waiting for the debounce
    public long candidateSinceMs;

    public ProductTracker() {}

    /** A product_code reading. */
    public void onCode(String raw, long tsMs) {
        if (raw == null || raw.isEmpty()) return;
        if (confirmed == null) { confirmed = raw; candidate = null; return; }
        if (raw.equals(confirmed)) { candidate = null; return; }
        if (!raw.equals(candidate)) { candidate = raw; candidateSinceMs = tsMs; }
        settle(tsMs);
    }

    /** Called before using the product at an instant: promotes a candidate that has held long enough. */
    public void settle(long tsMs) {
        if (candidate != null && tsMs - candidateSinceMs >= DEBOUNCE_MS) { confirmed = candidate; candidate = null; }
    }
}
