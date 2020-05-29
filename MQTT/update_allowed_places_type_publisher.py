import paho.mqtt.client as mqtt

import json

client = mqtt.Client("Allowed_Places_Types_Publisher")

client.connect("192.168.1.83", port=1883)

payload = 1

client.publish("AllowedPlacesTypes", payload)