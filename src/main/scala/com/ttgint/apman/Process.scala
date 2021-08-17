package ************

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, expr, lit}

object Process {
  def dfProcess(df: DataFrame, spark: SparkSession, topicName: String, fieldName: String): DataFrame = {

    import spark.sqlContext.implicits._

    val processedDF = df
      .select(
        ('time.cast("long")/1000).cast("timestamp").as(topicName + "_time"),
        'host.as(topicName + "_host"),
        col(fieldName).as(topicName + "_" + fieldName)
      )
      .withWatermark(topicName + "_time", "20000 milliseconds")

    return processedDF
  }

  def joinProcess(df: DataFrame, df2: DataFrame, topicNames: Array[String], fieldNames: Array[String]): DataFrame ={

    val joinDF = df.join(df2, expr("" + topicNames.apply(0) + "_host=" + topicNames.apply(1) + "_host and " + topicNames.apply(0) + "_time=" +  topicNames.apply(1) + "_time"), "inner")
      .select(
        topicNames.apply(0) + "_time",
        topicNames.apply(0) + "_host",
        topicNames.apply(0) + "_" + fieldNames.apply(0),
        topicNames.apply(1) + "_" + fieldNames.apply(1)
      )

    return joinDF
  }

  def dfProcessForUnion(df: DataFrame, spark: SparkSession, topicName: String, fieldName: String): DataFrame = {

    import spark.sqlContext.implicits._

    val processedDF = df
      .select(
        ('time.cast("long")/1000).cast("timestamp").as("time"),
        'host,
        col(fieldName).as("value")
      )
      .withColumn("fieldName", lit(fieldName))
      .withColumn("topicName", lit(topicName))
      .withWatermark("time", "20000 milliseconds")

    return processedDF
  }
}
