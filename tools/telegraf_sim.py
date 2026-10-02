#!/usr/bin/env python3
"""
Telegraf-shaped machine messages for testing the silver job (matches db/seed/dev_master.sql).

Scenario "worked_example" = Documents/04_gold/gold_model.md §6 on business day 2026-09-30, shift 1
(07:00-15:00 WIB = 00:00-08:00 UTC), one message per tag every 10 s:
  product A 00:00-05:00 UTC (18,000 packs, 300 underweight rejects = 6.0 kg), small stop 03:00:00-03:04:30
  stall 05:00-05:30 (a break: Flink records it unplanned; the supervisor relabels it 'Break' -> planned)
  product B 05:30-08:00 (6,000 packs, 180 overweight rejects = 3.6 kg), stop 06:40-07:02 with
  'Material jam' turning on 06:41 and 'Film out' 06:45
  checkweigher CHK01 counts what the packer passed (23,520 total, 23,400 good)
Expected (after the Break relabel): A 95.111 · P 91.121 · Q 98.000 · OEE 84.933 for WU 7.

Output: JSON lines "key<TAB>message" (key = source_tag, so one tag stays in one partition), e.g.
  python3 tools/telegraf_sim.py > /tmp/sim.tsv
  docker compose exec -T kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server kafka:9092 \
      --topic machine_metrics --property parse.key=true --property key.separator=$'\t' < /tmp/sim.tsv
"""
import argparse
import json
from datetime import datetime, timedelta, timezone

DAY = datetime(2026, 9, 30, tzinfo=timezone.utc)
STEP = 10  # seconds


def ts(h, m=0, s=0):
    return DAY + timedelta(hours=h, minutes=m, seconds=s)


def increments(windows):
    """All message instants (every STEP s) at which the counter increases."""
    out = []
    for a, b in windows:
        t = a
        while t <= b:
            out.append(t)
            t += timedelta(seconds=STEP)
    return out


def spread(total, n, decimals=0):
    """Split `total` over n increments (Bresenham), as cumulative values after each increment."""
    scale = 10 ** decimals
    units = round(total * scale)
    cum, acc = [], 0
    for i in range(1, n + 1):
        acc = units * i // n
        cum.append(acc / scale if decimals else acc)
    return cum


def msg(node, value, t, equipment):
    return {
        "name": "machine_metrics",
        "tags": {"equipment_code": equipment, "line_code": "L1", "source_tag": node},
        "fields": {"payload": f"{node}={value}"},
        "timestamp": t.strftime("%Y-%m-%dT%H:%M:%SZ"),
    }


def worked_example():
    a_inc = increments([(ts(0, 0, 10), ts(3)), (ts(3, 4, 30), ts(5))])
    b_inc = increments([(ts(5, 30), ts(6, 40)), (ts(7, 2), ts(7, 59, 50))])
    series = {}   # node -> {instant: cumulative value}

    def cumulative(node, incs, values, start=0):
        d = series.setdefault(node, {})
        for t, v in zip(incs, values):
            d[t] = v + start

    tot_a, tot_b = spread(18000, len(a_inc)), spread(6000, len(b_inc))
    cumulative("PLC/L1/PCK01/total_count", a_inc, tot_a)
    cumulative("PLC/L1/PCK01/total_count", b_inc, tot_b, start=18000)
    cumulative("PLC/L1/PCK01/good_count", a_inc, spread(17700, len(a_inc)))
    cumulative("PLC/L1/PCK01/good_count", b_inc, spread(5820, len(b_inc)), start=17700)
    cumulative("PLC/L1/PCK01/ng_under_weight", a_inc, spread(6.0, len(a_inc), 3))
    cumulative("PLC/L1/PCK01/ng_over_weight", b_inc, spread(3.6, len(b_inc), 3))
    all_inc = a_inc + b_inc
    cumulative("PLC/L1/CHK01/total_count", all_inc, spread(23520, len(all_inc)))
    cumulative("PLC/L1/CHK01/good_count", all_inc, spread(23400, len(all_inc)))

    equipment = {"PCK01": "PCK01", "CHK01": "CHK01"}
    lines = []
    t, end = ts(0), ts(8)
    last = {n: 0 for n in series}
    while t < end:
        for node, d in series.items():
            if t in d:
                last[node] = d[t]
            lines.append(msg(node, last[node], t, node.split("/")[2]))
        product = "A01" if t < ts(5) else "B01"
        lines.append(msg("PLC/L1/PCK01/product_code", product, t, "PCK01"))
        jam = 1 if ts(6, 41) <= t < ts(7) else 0
        film = 1 if ts(6, 45) <= t < ts(7) else 0
        lines.append(msg("PLC/L1/PCK01/jam", jam, t, "PCK01"))
        lines.append(msg("PLC/L1/PCK01/film_out", film, t, "PCK01"))
        t += timedelta(seconds=STEP)
    return lines


def edge_cases():
    """Unknown tag, broken payload: these must land in the DLQ."""
    t = ts(1)
    return [
        msg("PLC/L1/UNKNOWN/total_count", 5, t, "UNKNOWN"),
        {"name": "machine_metrics", "tags": {"source_tag": "PLC/L1/PCK01/total_count"}, "fields": {"payload": "no-equals"},
         "timestamp": t.strftime("%Y-%m-%dT%H:%M:%SZ")},
    ]


if __name__ == "__main__":
    ap = argparse.ArgumentParser()
    ap.add_argument("--scenario", default="worked_example", choices=["worked_example", "edge_cases", "tail"])
    a = ap.parse_args()
    if a.scenario == "tail":
        # one message well after the shift, so the watermark passes 08:00 and the last minute of the day is flushed
        # the line keeps running into shift 2 for 5 minutes (so shift 1 ends without an artificial stall), then one
        # message at 08:07 moves the watermark past 08:05
        msgs, t, n = [], ts(8), 0
        while t <= ts(8, 5):
            n += 1
            for node, base in (("PLC/L1/PCK01/total_count", 24000), ("PLC/L1/PCK01/good_count", 23520),
                               ("PLC/L1/CHK01/total_count", 23520), ("PLC/L1/CHK01/good_count", 23400)):
                msgs.append(msg(node, base + 4 * n, t, node.split("/")[2]))
            msgs.append(msg("PLC/L1/PCK01/product_code", "B01", t, "PCK01"))
            t += timedelta(seconds=STEP)
        msgs.append(msg("PLC/L1/CHK01/good_count", 23400 + 4 * n, ts(8, 7), "CHK01"))
    else:
        msgs = worked_example() if a.scenario == "worked_example" else edge_cases()
    for m in msgs:
        print(m["tags"].get("source_tag", "") + "\t" + json.dumps(m, separators=(",", ":")))
