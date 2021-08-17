#!/bin/bash
JAR="apmananomalydetector_2.12-0.1.jar"
SOURCE="$(pwd)/target/scala-2.12/${JAR}"
LOG4JFILE="$(pwd)/src/main/log4j.properties"
REMOTE="ttg@192.168.8.43"
REMOTEJARDIR="/home/ttg/jars"
SPARKSUBMIT="/usr/local/spark/bin/spark-submit"
jsonfile="$(pwd)/resources/config.json"


REPOSITORIES="https://packages.confluent.io/maven/"


PACKAGES="org.apache.spark:spark-core_2.12:3.0.0"
PACKAGES=${PACKAGES},"org.apache.spark:spark-sql_2.12:3.0.0"
PACKAGES=${PACKAGES},"org.apache.spark:spark-avro_2.12:3.0.0"
PACKAGES=${PACKAGES},"org.apache.spark:spark-streaming_2.12:3.0.0"
PACKAGES=${PACKAGES},"org.apache.spark:spark-tags_2.12:3.0.0"
PACKAGES=${PACKAGES},"org.apache.spark:spark-streaming-kafka-0-10_2.12:3.0.0"
PACKAGES=${PACKAGES},"org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.0"
PACKAGES=${PACKAGES},"org.apache.spark:spark-mllib_2.12:3.0.0"
PACKAGES=${PACKAGES},"org.apache.spark:spark-token-provider-kafka-0-10_2.12:3.0.0"
PACKAGES=${PACKAGES},"org.apache.kafka:kafka_2.12:2.6.0"
PACKAGES=${PACKAGES},"org.apache.kafka:kafka-clients:2.6.0"
PACKAGES=${PACKAGES},"net.sourceforge.f2j:arpack_combined_all:0.1"

PACKAGES=${PACKAGES},"org.apache.avro:avro:1.9.2"
PACKAGES=${PACKAGES},"org.skife.com.typesafe.config:typesafe-config:0.3.0"

PACKAGES=${PACKAGES},"org.scalanlp:breeze_2.12:1.0"

PACKAGES=${PACKAGES},"io.confluent:kafka-avro-serializer:5.3.1"
PACKAGES=${PACKAGES},"io.github.cdimascio:java-dotenv:5.2.2"

REMOTECOMMAND="${REMOTECOMMAND} --files \`\"${REMOTEJARDIR}/config.json\"\`"

REMOTECOMMAND="cd ${REMOTEJARDIR};"
REMOTECOMMAND="${REMOTECOMMAND} ${SPARKSUBMIT}"
REMOTECOMMAND="${REMOTECOMMAND} --repositories ${REPOSITORIES}"
REMOTECOMMAND="${REMOTECOMMAND} --packages ${PACKAGES}"
## REMOTECOMMAND="${REMOTECOMMAND} --driver-java-options \`\"-Dlog4j.configuration=file:${REMOTEJARDIR}/log4j.properties\"\`"
## REMOTECOMMAND="${REMOTECOMMAND} --conf \`\"spark.executor.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties\"\`"
## REMOTECOMMAND="${REMOTECOMMAND} --files \`\"${REMOTEJARDIR}/log4j.properties\"\`"
REMOTECOMMAND="${REMOTECOMMAND} ${JAR}"
scp $jsonfile "${REMOTE}:${REMOTEJARDIR}"
scp $SOURCE     "${REMOTE}:${REMOTEJARDIR}"
# scp $LOG4JFILE  "${REMOTE}:${REMOTEJARDIR}"

ssh ${REMOTE} "${REMOTECOMMAND}"
ssh ${REMOTE} "${REMOTECOMMAND}"
