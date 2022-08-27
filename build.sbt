val organization  = "com.tritas"
val description   = "Sensor Data Engineering"
// Must match the docker-compose version
val sparkVersion  = "3.2.0"
val scala3Version = "3.1.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "sensor-data-eng-demo",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      // https://mvnrepository.com/artifact/org.apache.spark/spark-avro
      ("org.apache.spark" %% "spark-avro" % sparkVersion).cross(CrossVersion.for3Use2_13),
      ("org.apache.spark" %% "spark-core" % sparkVersion).cross(CrossVersion.for3Use2_13),
      ("org.apache.spark" %% "spark-streaming" % sparkVersion).cross(CrossVersion.for3Use2_13),
      // https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kafka-0-10
      ("org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion).cross(CrossVersion.for3Use2_13),

      ("org.apache.spark" %% "spark-sql" % sparkVersion % "provided").cross(CrossVersion.for3Use2_13),
      // https://mvnrepository.com/artifact/org.apache.spark/spark-sql-kafka-0-10
      ("org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion).cross(CrossVersion.for3Use2_13),
      ("org.apache.spark" %% "spark-repl" % sparkVersion).cross(CrossVersion.for3Use2_13),
      // Not compiled
      // "org.apache.bahir" %% "spark-streaming-mqtt" % sparkVersion,
      ("io.github.vincenzobaz" %% "spark-scala3" % "0.1.3"),
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )


initialCommands += """
  import org.apache.spark.sql.SparkSession
  import org.apache.spark.SparkContext
  val spark = SparkSession.builder.
    master("local[*]").
    appName("Console").
    config("spark.app.id", "Console").   // To silence Metrics warning.
    getOrCreate()
  val sc = spark.sparkContext
  val sqlContext = spark.sqlContext
  import sqlContext.implicits._
  import org.apache.spark.sql.functions._    // for min, max, etc.
  """

cleanupCommands += """
  println("Closing the SparkSession:")
  spark.stop()
  """

// include the 'provided' Spark dependency on the classpath for `sbt run`
Compile / run := Defaults.runTask(Compile / fullClasspath, Compile / run / mainClass, Compile / run / runner).evaluated
