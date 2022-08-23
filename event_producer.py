"""
MQTT Smart temperature Sensor
"""

import time

from paho.mqtt.client import Client
from faker import Faker

if __name__ == "__main__":
    # let's connect to the MQTT broker
    MQTT_BROKER_URL = "localhost"
    MQTT_PUBLISH_TOPIC = "temperature"

    mqttc = Client()
    mqttc.connect(MQTT_BROKER_URL)

    # Init faker our fake data provider
    fake = Faker()

    # Infinite loop of fake data sent to the Broker
    while True:
        temperature = fake.random_int(min=0, max=30)
        mqttc.publish(MQTT_PUBLISH_TOPIC, temperature)
        print(f"Published new temperature measurement: {temperature}")
        time.sleep(1)
