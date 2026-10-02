package id.molca.pipeline.silver.parse;

import id.molca.pipeline.silver.model.MasterChange;
import id.molca.pipeline.silver.model.RawReading;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test void telegrafMessage() {
        RawReading r = TelegrafParser.parse("{\"name\":\"machine_metrics\",\"tags\":{\"equipment_code\":\"ANRITSU57-1\","
                + "\"line_code\":\"FI2-L1\",\"source_tag\":\"PLC/FI2-L1/ANRITSU57-1/ng_count\"},"
                + "\"fields\":{\"payload\":\"PLC/FI2-L1/ANRITSU57-1/ng_count=206\"},\"timestamp\":\"2026-09-30T03:05:55Z\"}");
        assertEquals("PLC/FI2-L1/ANRITSU57-1/ng_count", r.sourceTag);
        assertEquals("206", r.valueText);
        assertEquals(1790737555000L, r.eventTimeMs);
    }
    @Test void epochSecondsAndDecimal() {
        RawReading r = TelegrafParser.parse("{\"tags\":{\"source_tag\":\"a/good_weight\"},"
                + "\"fields\":{\"payload\":\"a/good_weight=10223.0\"},\"timestamp\":1790737555}");
        assertEquals("10223.0", r.valueText); assertEquals(1790737555000L, r.eventTimeMs);
    }
    @Test void badPayloadThrows() {
        assertThrows(IllegalArgumentException.class, () -> TelegrafParser.parse("{\"fields\":{\"payload\":\"novalue\"},\"timestamp\":1}"));
    }
    @Test void cdcCreateAndDelete() {
        MasterChange c = CdcParser.parse("{\"before\":null,\"after\":{\"tag_id\":101,\"node\":\"PLC/x\"},"
                + "\"source\":{\"schema\":\"silver\",\"table\":\"asset_tags\"},\"op\":\"r\"}");
        assertEquals("asset_tags|101", c.key()); assertFalse(c.delete);
        MasterChange d = CdcParser.parse("{\"before\":{\"tag_id\":101},\"after\":null,"
                + "\"source\":{\"table\":\"asset_tags\"},\"op\":\"d\"}");
        assertTrue(d.delete); assertNull(d.rowJson);
        assertNull(CdcParser.parse("{\"after\":{\"x\":1},\"source\":{\"table\":\"not_master\"},\"op\":\"c\"}"));
    }
}
