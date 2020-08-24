import paho.mqtt.client as mqtt

import json

client = mqtt.Client("Allowed_Places_Types_Publisher")

client.connect("90.169.70.108", port=1883)

payload = 1

client.publish("AllowedPlacesTypes", payload)