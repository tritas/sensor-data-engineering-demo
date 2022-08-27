/* SparkStreamingApp.scala */
import org.apache.spark
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro.functions._

class SparkStreamingApp {
  def main(args: Array[String]): Unit = {
    println(args.mkString("Array(", ", ", ")"))
    val spark = SparkSession.builder.appName("Spark Streaming App").getOrCreate()
    // https://spark.apache.org/docs/latest/sql-data-sources-avro.html
    // https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html
      val df = spark
          .readStream
          .format("kafka")
          .option("kafka.bootstrap.servers", "localhost:9092")
          .option("subscribe", "topic1")
          .load()
      df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)").as[(String, String)]

  }
}
