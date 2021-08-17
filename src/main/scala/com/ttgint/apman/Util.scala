package ***********

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord, IndexedRecord}
import org.apache.spark.sql
import org.apache.spark.sql.Row

object Util {
  private def schemaParser = new Schema.Parser()
  def deserialize(deserializer: KafkaAvroDeserializer, row: Row): String = {
    val value = row.getAs[Array[Byte]]("value")
    val res = deserializer.deserialize("", value)
    res match {
      case str: String =>
        str
      case _ =>
        val genericRecord = res.asInstanceOf[GenericRecord]
        genericRecord.toString
    }
  }

  def schemaFromSchemaRegistry(subject: String, client: CachedSchemaRegistryClient): Schema = {
    schemaParser.parse(client.getLatestSchemaMetadata(subject).getSchema)
  }

  def rowToRecord(row: sql.Row, schema: Schema): IndexedRecord = {
    val record = new GenericData.Record(schema)
    schema.getFields.forEach(field => {
      val fieldname = field.name()
      val index = row.fieldIndex(fieldname)
      record.put(fieldname, row.get(index))
    })
    record
  }

}
