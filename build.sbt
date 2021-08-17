name := "APMANAnomalyDetector"

version := "0.1"

scalaVersion := "2.12.10"

resolvers += "confluent" at "https://packages.confluent.io/maven/"

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.0" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.0" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-avro" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-tags" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-token-provider-kafka-0-10" % "3.0.0"

libraryDependencies += "org.apache.kafka" %% "kafka" % "2.6.0"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.5.0"

libraryDependencies += "org.apache.avro"  %  "avro"  %  "1.9.2"

libraryDependencies += "org.scalanlp" %% "breeze" % "1.0"

libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "5.3.1"

libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "4.0.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test

libraryDependencies += "net.sourceforge.f2j" % "arpack_combined_all" % "0.1"  //add
libraryDependencies +="org.skife.com.typesafe.config" % "typesafe-config" % "0.3.0" //add2
libraryDependencies += "io.github.cdimascio" % "dotenv-java" % "2.2.0"


mainClass in (Compile, run) := Some("com.ttgint.apman.AnomalyDetector")

showSuccess := false

//assemblyJarName in assembly := "kafkadeneme.jar"
//
//assemblyMergeStrategy in assembly := {
//  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
//  case x => MergeStrategy.first
//}
