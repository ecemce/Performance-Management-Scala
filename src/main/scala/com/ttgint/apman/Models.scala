package com.ttgint.apman

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{asc, avg, col, count, desc, expr, from_utc_timestamp, last, lit, stddev, sum, unix_timestamp, window}
import org.apache.spark.sql.types.TimestampType

object Models {
  def atakanTestModel(input: DataFrame, topicName: String, fieldName: String, threshold: Int): DataFrame = {
    input.filter(row => row.getAs[Double](fieldName) > threshold)
      .select(
        col("host"),
        col(fieldName).alias("value")
      )
      .withColumn("topic", lit(topicName))
      .withColumn("column", lit(fieldName))
      .withColumn("model", lit("ATAKANTESTMODEL"))
      .withColumn("threshold", lit(threshold))
  }

  def ecemTestModelWithUnion(spark: SparkSession, JsonData: DataFrame, allData: Array[(DataFrame, String, String)]): DataFrame = {
    import spark.sqlContext.implicits._

    val df = Process.dfProcessForUnion(allData(0)._1, spark, allData(0)._2, allData(0)._3)

    val joinDF = df.join(JsonData, expr("topicName=topic and fieldName=metric "), "inner")
      .select(
        'time,
        'value,
        'host,
        'metric,
        'topic,
        'down_threshold,
        'up_threshold
      )

    joinDF.createOrReplaceTempView("sortedDF")

    val sqlQuery = spark.sql(
      "select metric, topic, anomaly_time, down_threshold, up_threshold, window_start_time, window_end_time, host, (100-value) as value, " +
        "case when  (100-value) >= up_threshold then 'up' " +
        " else 'not anomaly' end as anomaly from " +
        "(select sorteddf.time as window_start_time, sorteddf.time as window_end_time ,sorteddf.down_threshold, sorteddf.up_threshold, sorteddf.time as anomaly_time , host, metric, topic,  value " +
        "from sortedDF ) as data"
    )


    val readyToWrite = sqlQuery
      .filter(row =>
        (row.getAs[String]("anomaly") == "up" || row.getAs[String]("anomaly") == "down")
          && !row.getAs[Double]("up_threshold").isNaN
          && !row.getAs[Double]("down_threshold").isNaN
          && row.getAs("window_start_time") != null
          && row.getAs("window_end_time") != null
          && row.getAs("anomaly_time") != null
      )
      .select(
        'host,
        'anomaly,
        'value,
        'up_threshold,
        'down_threshold,
        (unix_timestamp('window_start_time) * 1000).as("window_start_time"),
        (unix_timestamp('window_end_time) * 1000).as("window_end_time"),
        (unix_timestamp('anomaly_time) * 1000).as("anomaly_time"),
        lit("ECEMTESTMODEL").as("model"),
        lit("usedpercent").as("metric"),
        'topic
      )
 //unix_timestamp saat çağrısı +3 -3 şekilde ayarlı onu kontrol et

    readyToWrite
  }
}
