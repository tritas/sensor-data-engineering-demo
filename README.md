# Sensor Data Engineering demo

Installing the stack: the MQTT broker is part of the `docker-compose` stack.

## Running the producer

First install the requirements in a virtual env:

`pip install requirements.txt`

To run the producer, execute the python script:

`python event_producer.py`

## Running the consumer

To use the
[Apache Bahir Streaming MQTT connector](https://github.com/apache/bahir/tree/master/streaming-mqtt),
we need to compile it for the latest Spark version we're using. See
https://stackoverflow.com/questions/64254852/scala-2-11-compilation-for-the-apache-bahir-library

to run this example locally:

```bash
    python mqtt_pyspark_consumer.py
```

## Scala packages

### sbt

The scala code can be compiled with `sbt compile`, run with `sbt run`. `sbt console` will start a
Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).

### Development with Scala 3

See for example:

- https://github.com/47deg/spark-scala3-example
- https://github.com/vincenzobaz/spark-scala3

## Author

Copyright (c) 2022 Aris Tritas

## License

The project is licensed under the Apache 2.0 License, see the [LICENSE](./LICENSE) file.
