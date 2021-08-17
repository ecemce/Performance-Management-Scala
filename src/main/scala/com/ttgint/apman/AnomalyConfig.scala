**********

import org.apache.spark.sql.DataFrame

case class HostDescribe(name: String, sigma: Double)

case class AnomalyConfig(
                          topic: String,
                          metric: String,
                          up_threshold: Double,
                          down_threshold: Double,
                          host: Array[HostDescribe],
                          dfs: DataFrame)
