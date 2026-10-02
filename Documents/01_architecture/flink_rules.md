Key Rules for Apache Flink
• Match Parallelism to Slots: Set your total parallelism so that your Task Managers multiplied by their task slots equals or slightly exceeds your highest operator parallelism level.
• Prefer Event Time: Use event time instead of processing time to handle out-of-order data and ensure consistent, predictable results.
• Configure Watermarks Carefully: Define watermark lateness thresholds matching your peak data delay to balance latency and completeness.
• Choose the Right State Backend: Use JVM Heap for small, low-overhead states and RocksDB for large, disk-scaled states that exceed memory limits.
• Keep JAR Footprints Lean: Strip out non-runtime dependencies, test datasets, and static bootstrapping files to keep your deployment JAR files small.
• Enable Checkpointing: Turn on incremental checkpoints with reliable local storage rocksdb? to guarantee exactly-once semantics during failures.


use this Flink ver:
# syntax=docker/dockerfile:1
# Event Run Checker job image: the official Flink 1.20 image plus the job jar (Flink application mode).
# Flink, RocksDB, flink-connector-base and datagen come from the image's flink-dist; the job jar bundles the
# Kafka/JDBC connectors, the PostgreSQL driver and Jackson.

ARG FLINK_IMAGE=flink:1.20.5-java17

FROM eclipse-temurin:17-jdk AS build
WORKDIR /src
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q package -DskipTests

FROM ${FLINK_IMAGE}
COPY --from=build --chown=flink:flink /src/target/event-run-checker-*-SNAPSHOT.jar /opt/flink/usrlib/event-run-checker.jar
COPY --chmod=755 deploy/flink/start-jobmanager.sh /opt/event-run-checker/start-jobmanager.sh
# Pre-create the state directory owned by `flink`, so a fresh named volume mounted here inherits the ownership.
RUN mkdir -p /flink-data/checkpoints /flink-data/savepoints /flink-data/dumps && chown -R flink:flink /flink-data

The job must of flink must ran if flink is deployed/build   
---

**Related:** [[silver_model]] · [[downtime_detector]] · [[objective]]
