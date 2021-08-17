package com.ttgint.apman

import java.sql.Timestamp

case class anomaly(
                    topic: String = null,
                    metric: String = null,
                    host: String = null,
                    model: String = null,
                    anomaly: String = null,
                    up_threshold: Double = 0.0,
                    down_threshold: Double = 0.0,
                    value: Double = 0.0,
                    window_start_time: Timestamp = null,
                    window_end_time: Timestamp = null,
                    anomaly_time: Timestamp = null
                  )
