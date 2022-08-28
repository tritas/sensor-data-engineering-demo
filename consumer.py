#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""
 This example will consume temperature data (or any other numerical values, really)
 from an MQTT broker, and consolidate/graph this data on a 15-second sliding window.

 This work is based on the original mqtt_wordcount.py sample from the Apache Spark codebase

 Running the example:
    `$ bin/spark-submit --jars \
      external/mqtt-assembly/target/spark-streaming-mqtt-assembly_*.jar \
      mqtt_spark_streaming.py`

To run the example, run pyspark with this command::

  pyspark --packages org.apache.bahir:spark-streaming-mqtt_2.11:2.4.0

When the console is ready I can run the code example changing
from pyspark.streaming.mqtt import MQTTUtils to from mqtt import MQTTUtils

Also I had to change also the MQTT connection to:
mqttStream = MQTTUtils.createStream(ssc, brokerUrl, topic, username=None, password=None)

"""
import operator
import sys

from pyspark import SparkContext
from pyspark.sql import SparkSession
from pyspark.streaming import StreamingContext
from pyspark.streaming.mqtt import MQTTUtils


def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False


def printHistogram(time, rdd):
    c = rdd.collect()
    print("-------------------------------------------")
    print("Time: %s" % time)
    print("-------------------------------------------")
    for record in c:
        # "draw" our lil' ASCII-based histogram
        print(str(record[0]) + ': ' + '#' * record[1])
    print("")


def build_context(master: str = "local[*]"):
    spark = (
        SparkSession.builder.appName("mqtt-consumer")
        .master(master)
        .config("spark.jars.packages", "org.apache.bahir:spark-streaming-mqtt_2.12:3.3.0")
        .getOrCreate()
    )
    sc = SparkContext(appName="mqtt-consumer")
    ssc = StreamingContext(sc, 1)
    ssc.checkpoint("checkpoint")
    return sc, ssc, spark


if __name__ == "__main__":
    # broker URI
    brokerUrl = "tcp://localhost:1883"
    # topic or topic pattern where temperature data is being sent
    topic = "+/+/sensors/temperature"
    sc, ssc, spark = build_context()
    mqttStream = MQTTUtils.createStream(ssc, brokerUrl, topic)

    counts = mqttStream \
        .filter(lambda message: is_number(message)) \
        .map(lambda message: (round(float(message) * 2, 0) / 2, 1)) \
        .reduceByKeyAndWindow(operator.add, operator.sub, 15, 1) \
        .transform(lambda rdd: rdd.sortByKey())

    counts.foreachRDD(printHistogram)

    ssc.start()
    ssc.awaitTermination()
