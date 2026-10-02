package id.molca.pipeline.silver.lane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTrackerTest {
    @Test void firstCodeAcceptedImmediately() {
        ProductTracker p = new ProductTracker();
        p.onCode("A", 0);
        assertEquals("A", p.confirmed);
    }
    @Test void newCodeNeeds30Seconds() {
        ProductTracker p = new ProductTracker();
        p.onCode("A", 0);
        p.onCode("B", 100_000);
        p.settle(110_000);
        assertEquals("A", p.confirmed);              // only 10 s
        p.settle(130_000);
        assertEquals("B", p.confirmed);              // held 30 s
    }
    @Test void noiseIsIgnored() {
        ProductTracker p = new ProductTracker();
        p.onCode("A", 0);
        p.onCode("B", 100_000);
        p.onCode("A", 105_000);                      // blip back to A
        p.settle(200_000);
        assertEquals("A", p.confirmed);
        assertNull(p.candidate);
    }
}
