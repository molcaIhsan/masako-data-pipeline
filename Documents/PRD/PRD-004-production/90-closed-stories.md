# PRD-004 · Retired PRODUCTION story IDs

> Part of [PRD-004 PRODUCTION](README.md). **Do not build anything from this page.** It lists the 18 PRODUCTION
> IDs from frozen [PRD-001](../PRD-001-molcadx-core-q3-en.md) and where their content went. The IDs are retired
> with the old module codes `SHF` / `OEE` / `JOB` (2026-09-24) and are never reused.

| Old ID | Old title | Now | Note |
|--------|-----------|-----|------|
| `US-PROD-SHF-001` | Start the shift & see scheduled jobs | Partly [`PLN-005`](01-planning.md#us-prod-pln-005) (the queue) | Starting a work session is not in the Figma. Not carried |
| `US-PROD-SHF-002` | Record production output | **Not carried** | Output comes from the sensor path; manual entry is open (M-01) |
| `US-PROD-SHF-003` | Open downtime in one tap | **Not carried** | Needs `ASSET_STATE_LOG` (B5); not in the Figma |
| `US-PROD-SHF-004` | Close downtime with a reason code | Partly [`MON-005`](02-monitoring.md#us-prod-mon-005) | The downtime part waits on B5 |
| `US-PROD-SHF-005` | Record defects with their disposition | [`MON-005`](02-monitoring.md#us-prod-mon-005), [`MON-006`](02-monitoring.md#us-prod-mon-006) | Rows come from the sensor; manual rows are overrides. Disposition is on the row (T-6) |
| `US-PROD-SHF-006` | Raise an andon | **Not carried** | Not in the Figma. Needs a PO decision on where it belongs |
| `US-PROD-SHF-007` | Close the shift & lock its numbers | **Not carried** | Not in the Figma. Shift close and "provisional → final" still matter; open for the PO |
| `US-PROD-SHF-008` | Correct entries as flagged adjustments | Partly [`MON-005`](02-monitoring.md#us-prod-mon-005) | Corrections of loss reasons, with the log |
| `US-PROD-OEE-001` | See line OEE for the running shift | [`PRF-001`](03-performance.md#us-prod-prf-001) | Numbers now read from `KPI_RESULT` |
| `US-PROD-OEE-002` | Compare every line in one view | [`MON-001`](02-monitoring.md#us-prod-mon-001), [`PRF-003`](03-performance.md#us-prod-prf-003) | |
| `US-PROD-OEE-003` | Trace loss causes to the source entry | Partly [`MON-004`](02-monitoring.md#us-prod-mon-004) + `US-FND-KPI-008` (breakdown) | |
| `US-PROD-OEE-004` | See the OEE trend across periods | [`PRF-002`](03-performance.md#us-prod-prf-002) | |
| `US-PROD-OEE-005` | Flag anomalies & data completeness | Rule on every PRF screen (`KPI_RESULT.status`, `is_anomaly`) | No separate story |
| `US-PROD-JOB-001` | Allocate jobs to lines & shifts | Partly [`PLN-001`](01-planning.md#us-prod-pln-001) (workload per operation) | The capacity board used `scheduled_minutes`, which F-02 removed. Not carried |
| `US-PROD-JOB-002` | Release jobs so operators can see them | [`PLN-003`](01-planning.md#us-prod-pln-003) | |
| `US-PROD-JOB-003` | Surface carry-over in the next shift | **Not carried** | Carry-over is not in the current spec or the Figma |
| `US-PROD-JOB-004` | Reschedule with a recorded reason | [`PLN-006`](01-planning.md#us-prod-pln-006) | |
| `US-PROD-JOB-005` | Create & release a work order | [`PLN-001`](01-planning.md#us-prod-pln-001) + [`PLN-003`](01-planning.md#us-prod-pln-003) | Recipe lock at release (B3) |

**Not carried, needs a PO answer:** andon (`SHF-006`), shift close (`SHF-007`), carry-over (`JOB-003`), and operator
output / downtime entry (`SHF-002`, `SHF-003`, tied to M-01 and B5).
