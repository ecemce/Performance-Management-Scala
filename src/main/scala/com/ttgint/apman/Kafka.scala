package ***********

import java.util.Properties

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroSerializer}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.avro.SchemaConverters
import org.apache.spark.sql.functions.from_json
import org.apache.spark.sql.streaming.StreamingQuery

object Kafka {
  def devEnvKafkaProperties(): Properties = {
    val properties = new Properties()
    properties.setProperty("kafka.bootstrap.servers", "****************")
    properties.setProperty("kafka.security.protocol", "SASL_PLAINTEXT")
    properties.setProperty("kafka.sasl.mechanism", "PLAIN")
    properties.setProperty("kafka.sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required\n username=\"client\"\n password=\"*********\";")
    properties
  }

  def prodEnvKafkaProperties(): Properties = {
    val properties = new Properties()
    properties.setProperty("kafka.bootstrap.servers", "*****************")
    properties
  }

  def kafkaReadStream(spark: SparkSession, properties: Properties, topic: String, schemaRegistryURL: String): DataFrame = {
    val subject = topic + "-value"
    var reader = spark
      .readStream
      .format("kafka")
      .option("startingOffsets", "latest")
      .option("subscribe", topic)
    //      .option("failOnDataLoss", "false")

    properties.stringPropertyNames().forEach(name => {
      reader = reader.option(name, properties.getProperty(name))
    })

    import spark.sqlContext.implicits._
    val df = reader.load()
    val jsonDF = df.mapPartitions(partition => {
      val schemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistryURL, 128)
      val deserializer = new KafkaAvroDeserializer(schemaRegistryClient)
      val newPartition = partition.map(row => {
        Util.deserialize(deserializer, row)
      })
      newPartition.toList.iterator
    })
    val schemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistryURL, 128)
    val sqlType = SchemaConverters.toSqlType(Util.schemaFromSchemaRegistry(subject, schemaRegistryClient))
    jsonDF.select(from_json('value, sqlType.dataType).alias("metric")).select($"metric.*")
  }

  def kafkaWriteStream(spark: SparkSession, df: DataFrame, properties: Properties, topic: String, schemaRegistryURL: String): StreamingQuery = {
    import spark.implicits._
    val subject = topic + "-value"
    val serialized = df.mapPartitions(partition => {
      val schemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistryURL, 128)
      val serializer = new KafkaAvroSerializer(schemaRegistryClient)
      val schema = Util.schemaFromSchemaRegistry(subject, schemaRegistryClient)
      val newPartition = partition.map(row => {
        val record = Util.rowToRecord(row, schema)
        serializer.serialize(topic, record)
      })
      newPartition.toList.iterator
    })

    var writer = serialized
      .select('value)
      .writeStream
      .format("kafka")
      .option("topic", topic)
      .option("checkpointLocation", "/************")

    properties.stringPropertyNames().forEach(name => {
      writer = writer.option(name, properties.getProperty(name))
    })

    writer.start()
  }
}
