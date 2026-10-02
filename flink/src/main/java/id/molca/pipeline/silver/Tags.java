package id.molca.pipeline.silver;

import id.molca.pipeline.silver.model.DlqRecord;
import id.molca.pipeline.silver.model.StallInput;
import org.apache.flink.util.OutputTag;

/** Side outputs. */
public final class Tags {
    private Tags() {}
    public static final OutputTag<DlqRecord> DLQ = new OutputTag<>("dlq") {};
    public static final OutputTag<StallInput> STALL = new OutputTag<>("stall") {};
}
