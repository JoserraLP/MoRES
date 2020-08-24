import paho.mqtt.client as mqtt

import json

client = mqtt.Client("Location_Publisher")

client.connect("90.169.70.108", port=1883)

payload = json.dumps({ 
  "lat": 38.878435,
  "lng": -6.9513433
})

# Send 21 locations to test observation o1

for i in range(21):
  print("Publish " + str(i))
  client.publish("Location", payload)