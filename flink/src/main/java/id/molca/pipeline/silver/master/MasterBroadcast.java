package id.molca.pipeline.silver.master;

import id.molca.pipeline.silver.model.MasterChange;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.Types;

/**
 * Master data kept in Flink broadcast state ("table|pk" -> row JSON), fed by CDC and checkpointed with the job.
 * Each operator instance keeps a transient indexed snapshot, rebuilt lazily after a change.
 */
public final class MasterBroadcast implements java.io.Serializable {
    public static final MapStateDescriptor<String, String> DESCRIPTOR =
            new MapStateDescriptor<>("master-rows", Types.STRING, Types.STRING);

    /** A CDC burst (initial snapshot) arrives row by row: rebuild at most this often while it is dirty. */
    private static final long MIN_REBUILD_INTERVAL_MS = 1000;

    private transient MasterSnapshot snapshot;
    private transient boolean dirty = true;
    private transient long lastBuildMs;

    public void apply(MasterChange c, BroadcastState<String, String> state) throws Exception {
        if (c.delete) state.remove(c.key()); else state.put(c.key(), c.rowJson);
        dirty = true;
    }

    public MasterSnapshot get(ReadOnlyBroadcastState<String, String> state) throws Exception {
        long now = System.currentTimeMillis();
        if (snapshot == null || (dirty && now - lastBuildMs >= MIN_REBUILD_INTERVAL_MS)) {
            snapshot = new MasterSnapshot(state.immutableEntries());
            dirty = false;
            lastBuildMs = now;
        }
        return snapshot;
    }
}
