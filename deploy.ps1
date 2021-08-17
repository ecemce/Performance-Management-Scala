$jar = "sparkdeneme2_2.12-0.1.jar"
$source = "************"
$log4jproperties = "***************"
$remote = "**************"
$remotejardir = "**********"

$repositories = (
    "https://packages.confluent.io/maven/"
) -join ','

$packages = (
    "org.apache.spark:spark-core_2.12:3.0.0",
    "org.apache.spark:spark-sql_2.12:3.0.0",
    "org.apache.spark:spark-avro_2.12:3.0.0",
    "org.apache.spark:spark-streaming_2.12:3.0.0",
    "org.apache.spark:spark-tags_2.12:3.0.0",
    "org.apache.spark:spark-streaming-kafka-0-10_2.12:3.0.0",
    "org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.0",
    "org.apache.spark:spark-mllib_2.12:3.0.0",
    "org.apache.spark:spark-token-provider-kafka-0-10_2.12:3.0.0",

    "org.apache.kafka:kafka_2.12:2.6.0",
    "org.apache.kafka:kafka-clients:2.5.0",

    "org.apache.avro:avro:1.9.2",

    "org.scalanlp:breeze_2.12:1.0",

    "io.confluent:kafka-avro-serializer:5.3.1"

) -join ','

scp $source "${remote}:${remotejardir}"
scp $log4jproperties "${remote}:${remotejardir}"

$sparksubmit = "/usr/local/spark/bin/spark-submit"
ssh $remote "cd ${remotejardir}; ${sparksubmit} --repositories ${repositories} --packages ${packages} ${jar}"

#ssh $remote "cd ${remotejardir}; ${sparksubmit} --repositories ${repositories} --packages ${packages} --driver-java-options `"-Dlog4j.configuration=file:${remotejardir}/log4j.properties`" --conf `"spark.executor.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties`" --files `"${remotejardir}/log4j.properties`" ${jar}"


#$libjars = $libjars -join ','

#scp "******************" "**********"
#ssh *************'spark-submit /home/ttg/jars/kafkadeneme.jar'
