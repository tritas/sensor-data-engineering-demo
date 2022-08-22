# Sensor Data Engineering Demo

Installing the stack: the MQTT broker is part of the docker-compose stack.

## Running the producer

First install the requirements in a virtual env:

```pip install requirements.txt```

To run the producer, execute the python script:

```python event_producer.py```

## Running the consumer

To use the [Apache Bahir Streaming MQTT
connector](https://github.com/apache/bahir/tree/master/streaming-mqtt), we need to
compile it for the latest Spark version we're using. See https://stackoverflow.com/questions/64254852/scala-2-11-compilation-for-the-apache-bahir-library

to run this example locally:

```bash
    python mqtt_pyspark_consumer.py
```
