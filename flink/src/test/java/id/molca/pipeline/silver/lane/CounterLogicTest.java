package id.molca.pipeline.silver.lane;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CounterLogicTest {
    static BigDecimal d(String s) { return new BigDecimal(s); }

    @Test void cumulativeFirstValueIsBaseline() {
        var r = CounterLogic.delta("cumulative_counter", null, d("10000"));
        assertEquals(0, r.delta.compareTo(BigDecimal.ZERO)); assertFalse(r.reset);
    }
    @Test void cumulativeDifference() {
        assertEquals(0, CounterLogic.delta("cumulative_counter", d("10000"), d("10060")).delta.compareTo(d("60")));
    }
    @Test void cumulativeSameValueIsZero() {
        assertEquals(0, CounterLogic.delta("cumulative_counter", d("206"), d("206")).delta.signum());
    }
    @Test void cumulativeResetCountsFromZero() {
        var r = CounterLogic.delta("cumulative_counter", d("10060"), d("25"));
        assertEquals(0, r.delta.compareTo(d("25"))); assertTrue(r.reset);
    }
    @Test void cumulativeDecimalWeight() {
        assertEquals(0, CounterLogic.delta("cumulative_counter", d("10223.0"), d("10225.5")).delta.compareTo(d("2.5")));
    }
    @Test void deltaCounterIsValueItself() {
        var r = CounterLogic.delta("delta_counter", d("5"), d("7"));
        assertEquals(0, r.delta.compareTo(d("7"))); assertFalse(r.reset);
        assertEquals(0, CounterLogic.delta("delta_counter", null, d("0")).delta.signum());
    }
    @Test void neverNegative() {
        assertEquals(0, CounterLogic.delta("delta_counter", null, d("-3")).delta.signum());
    }
}
