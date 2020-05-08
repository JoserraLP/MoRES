import paho.mqtt.client as mqtt

import json

client = mqtt.Client("Publisher_test")

client.connect("192.168.1.83", port=1883)

# topic = input("Insert the topic: \n")

# payload = input("Insert the payload: \n")

payload = json.dumps({
  "title": "Patata-Sp",
  "date": "07/05/2020",
  "description": "This is the first description",
  "location": "Spain",
  "image": "https://miro.medium.com/max/1200/1*mk1-6aYaf_Bes1E3Imhc0A.jpeg",
  "relevance" : "Relevant"
})

client.publish("News", payload)