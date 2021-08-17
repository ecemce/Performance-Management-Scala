$jar = "sparkdeneme2_2.12-0.1.jar"
$source = "C:\Users\atakan.colak\IdeaProjects\sparkdeneme2\target\scala-2.12\$jar"
$log4jproperties = "C:\Users\atakan.colak\IdeaProjects\sparkdeneme2\src\main\log4j.properties"
$remote = "ttg@192.168.8.43"
$remotejardir = "/home/ttg/jars"

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

#scp "C:\Users\atakan.colak\IdeaProjects\sparkdeneme2\target\scala-2.12\kafkadeneme.jar" "ttg@192.168.8.43:/home/ttg/jars"
#ssh ttg@192.168.8.43 'spark-submit /home/ttg/jars/kafkadeneme.jar'