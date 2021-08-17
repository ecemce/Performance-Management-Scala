import com.sksamuel.avro4s.AvroSchema
import com.ttgint.apman.anomaly
import com.ttgint.apman.AnomalyDetector
import org.scalatest.FunSuite

class TestRegisterSchema extends FunSuite {
  val subject = "*************"
  test("PrintSchema") {
    val schema = AvroSchema[anomaly]
    println(schema.toString(true))
  }

  test("RegisterSchema") {
    val schema = AvroSchema[anomaly]
    AnomalyDetector.schemaRegistryClient.register(subject, schema)
  }

  test("GetSchema") {
    val schema = AnomalyDetector.schemaRegistryClient.getLatestSchemaMetadata(subject)
    println(schema.getSchema)
  }
}
