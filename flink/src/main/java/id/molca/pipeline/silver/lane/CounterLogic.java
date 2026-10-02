package id.molca.pipeline.silver.lane;

import java.math.BigDecimal;

/**
 * Counter delta rules (pipeline_contract F4):
 *  delta_counter      -> the value itself
 *  cumulative_counter -> value - previous; first value = baseline (0); value < previous = reset (delta = value)
 * Never negative.
 */
public final class CounterLogic {
    private CounterLogic() {}

    public static final class Result {
        public final BigDecimal delta; public final boolean reset;
        Result(BigDecimal d, boolean r) { delta = d; reset = r; }
    }

    public static Result delta(String kind, BigDecimal previous, BigDecimal value) {
        if ("delta_counter".equals(kind)) {
            return new Result(value.signum() < 0 ? BigDecimal.ZERO : value, false);
        }
        // cumulative_counter (and anything counted the same way)
        if (previous == null) return new Result(BigDecimal.ZERO, false);
        int cmp = value.compareTo(previous);
        if (cmp >= 0) return new Result(value.subtract(previous), false);
        return new Result(value.signum() < 0 ? BigDecimal.ZERO : value, true);   // reset: counted from 0
    }

    public static boolean isCounterRole(String role) {
        return "total".equals(role) || "good".equals(role) || "reject".equals(role);
    }
}
