/* SparkStreamingApp.scala */
import org.apache.spark
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro.functions.*
import org.apache.spark.sql.functions.col

import java.nio.file.{Files, Paths}

// `from_avro` requires Avro schema in JSON string format.
val jsonFormatSchema = new String(Files.readAllBytes(Paths.get("./schema.avsc")))

class SparkStreamingApp:
  def main(args: Array[String]): Unit =
    println(args.mkString("Array(", ", ", ")"))
    val spark = SparkSession.builder.appName("Spark Streaming App").getOrCreate()
    import spark.implicits.*
    // https://spark.apache.org/docs/latest/sql-data-sources-avro.html
    // https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html
    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "sensors-avro")
      .load()
    // 1. Decode the Avro data into a struct;
    // 2. Filter valid samples
    // 3. Encode the column `payload` in Avro format.
    val output = df
      .select(from_avro($"value", jsonFormatSchema).as("sample"))
      .where("sample.is_valid")
      .select(to_avro($"sample.payload").as("value"))
    // DataFrames can be saved as Parquet files, maintaining the schema information
    // https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#output-sinks
    val query = output.writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "sensors-normalized-avro")
      .start()
    println(query)
    val parquet_query =
      output.writeStream.format("parquet").option("path", "./data/samples").start()
    print(parquet_query)
    output.writeStream
      .format("console")
      .start()
