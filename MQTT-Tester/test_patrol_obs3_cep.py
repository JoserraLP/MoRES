import paho.mqtt.client as mqtt

import json

client = mqtt.Client("Patrol_Publisher")

client.connect("90.169.70.108", port=1883)

payload = json.dumps({ 
  "lat": 38.878435,
  "lng": -6.9513433
})

# Send 6 patrols to test observation o3

for i in range(6):
  print("Publish " + str(i))
  client.publish("Patrol", payload)