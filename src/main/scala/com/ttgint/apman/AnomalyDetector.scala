package ************

import java.util.Properties

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.github.cdimascio.dotenv.Dotenv
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, explode}

object AnomalyDetector {
  private val DEV_ENV: Map[String, String] = Map(
    "URL_MASTER" -> "spark://spark:7077",
    "APP_NAME" -> "dev_app",
    "URL_SCHEMA_REGISTRY" -> "***************",
    "ANOMALY_TOPIC" -> "***************",
    "SPARK_EXECUTOR_MEMORY" -> "2g",
    "SPARK_CORES_MAX" -> "2"
  )
  private val ECEM_ENV: Map[String, String] = Map(
    "URL_MASTER" -> "***************",
    "APP_NAME" -> "**************",
    "URL_SCHEMA_REGISTRY" -> "***************",
    "ANOMALY_TOPIC" -> "***********",
    "SPARK_EXECUTOR_MEMORY" -> "2g",
    "SPARK_CORES_MAX" -> "2"
  )

  private val PROD_ENV: Map[String,String] = Map(
    "URL_MASTER" -> "spark://spark:7077",
    "APP_NAME" -> "**************",
    "URL_SCHEMA_REGISTRY" -> "*****************",
    "ANOMALY_TOPIC" -> "************",
    "SPARK_EXECUTOR_MEMORY" -> "2g",
    "SPARK_CORES_MAX" -> "2"
  )

  val ENV: Map[String, String] = ECEM_ENV

  val KAFKA_PROPS: Properties = Kafka.devEnvKafkaProperties()

  val ANOMALY_SUBJECT: String = ENV("ANOMALY_TOPIC") + "-value"

  def schemaRegistryClient = new CachedSchemaRegistryClient(ENV("URL_SCHEMA_REGISTRY"), 128)

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setMaster(ENV("URL_MASTER"))
      .setAppName(ENV("APP_NAME"))
      .set("spark.sql.codegen.wholeStage", "false")
      .set("spark.debug.maxToStringFields", "1000")
      .set("spark.executor.memory", ENV("SPARK_EXECUTOR_MEMORY"))
      .set("spark.cores.max", ENV("SPARK_CORES_MAX"))

    val spark = SparkSession.builder().config(conf).getOrCreate()

    val anomalyConfig = spark.read.json("config.json")
    val prepareStruct = anomalyConfig.select(
      col("topic"),
      col("metric"),
      col("up_threshold"),
      col("down_threshold")
    )

    val distinctDF = prepareStruct.select("topic", "metric").distinct().collect()

    val totalFrame = distinctDF.map(row => (
      Kafka.kafkaReadStream(spark, KAFKA_PROPS, row.getAs[String]("topic"), ENV("URL_SCHEMA_REGISTRY")),
      row.getAs[String]("topic"),
      row.getAs[String]("metric")
    ))
    val anomaly = Models.ecemTestModelWithUnion(spark, prepareStruct, totalFrame)

    val writeStream = Kafka.kafkaWriteStream(spark, anomaly, KAFKA_PROPS, ENV("ANOMALY_TOPIC"), ENV("URL_SCHEMA_REGISTRY"))

    writeStream.awaitTermination()
  }
}
