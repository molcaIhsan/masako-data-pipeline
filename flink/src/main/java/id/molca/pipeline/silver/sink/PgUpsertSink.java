package id.molca.pipeline.silver.sink;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.api.connector.sink2.WriterInitContext;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Batched upsert into Postgres (SinkV2). Flushes at batchSize, after flushIntervalMs, and on every checkpoint
 * (flush() is called before the checkpoint completes) => at-least-once; the SQL is an idempotent upsert, so a replay
 * after a failure rewrites the same rows.
 */
public class PgUpsertSink<T> implements Sink<T> {

    /** Turns one element into one statement execution. A binder may choose between several statements. */
    public interface Binder<T> extends Serializable {
        /** @return index of the statement (in the sink's SQL list) to use for this element */
        int bind(T element, List<PreparedStatement> statements, Connection conn) throws SQLException;
    }

    private final String jdbcUrl, user, password;
    private final List<String> sql;
    private final Binder<T> binder;
    private final KeyFn<T> key;
    private final int batchSize;
    private final long flushIntervalMs;

    /** Natural key of an element: within one batch only the LAST element per key is written (an upsert may not
     *  touch the same row twice in one statement, and the last version is the one that counts). */
    public interface KeyFn<T> extends Serializable { String key(T element); }

    public PgUpsertSink(String jdbcUrl, String user, String password, List<String> sql, Binder<T> binder,
                        KeyFn<T> key, int batchSize, long flushIntervalMs) {
        this.jdbcUrl = jdbcUrl; this.user = user; this.password = password; this.sql = sql; this.binder = binder;
        this.key = key;
        this.batchSize = batchSize; this.flushIntervalMs = flushIntervalMs;
    }

    @Override
    public SinkWriter<T> createWriter(WriterInitContext context) {
        return new Writer();
    }

    private final class Writer implements SinkWriter<T> {
        private Connection conn;
        private List<PreparedStatement> statements;
        private final List<T> batch = new ArrayList<>();
        private long lastFlush = System.currentTimeMillis();

        private void connect() throws SQLException {
            Properties p = new Properties();
            p.setProperty("user", user);
            p.setProperty("password", password);
            p.setProperty("ApplicationName", "flink-silver");
            p.setProperty("reWriteBatchedInserts", "true");
            conn = DriverManager.getConnection(jdbcUrl, p);
            conn.setAutoCommit(false);
            statements = new ArrayList<>();
            for (String s : sql) statements.add(conn.prepareStatement(s));
        }

        @Override
        public void write(T element, Context context) throws java.io.IOException {
            batch.add(element);
            if (batch.size() >= batchSize || System.currentTimeMillis() - lastFlush >= flushIntervalMs) flush(false);
        }

        @Override
        public void flush(boolean endOfInput) throws java.io.IOException {
            if (batch.isEmpty()) { lastFlush = System.currentTimeMillis(); return; }
            SQLException last = null;
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    if (conn == null || conn.isClosed()) connect();
                    java.util.LinkedHashMap<String, T> latest = new java.util.LinkedHashMap<>();
                    for (T e : batch) { String k = key.key(e); latest.remove(k); latest.put(k, e); }
                    boolean[] used = new boolean[statements.size()];
                    for (T e : latest.values()) { int i = binder.bind(e, statements, conn); statements.get(i).addBatch(); used[i] = true; }
                    for (int i = 0; i < statements.size(); i++) if (used[i]) statements.get(i).executeBatch();
                    conn.commit();
                    batch.clear();
                    lastFlush = System.currentTimeMillis();
                    return;
                } catch (SQLException e) {
                    last = e;
                    try { if (conn != null) { conn.rollback(); conn.close(); } } catch (SQLException ignore) { }
                    conn = null;
                    try { Thread.sleep(1000L * attempt); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }
            throw new java.io.IOException("upsert failed after 3 attempts", last);
        }

        @Override
        public void close() throws Exception {
            if (conn != null) conn.close();
        }
    }
}
