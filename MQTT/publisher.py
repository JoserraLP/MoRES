import paho.mqtt.client as mqtt

import json

client = mqtt.Client("News_Publisher")

client.connect("192.168.1.83", port=1883)

# topic = input("Insert the topic: \n")

# payload = input("Insert the payload: \n")

payload = json.dumps({ 
  "title": "Patata-Sp",
  "date": "07/05/2020",
  "author": "JoserraLP",
  "description": "This is the first description",
  "location": "Extremadura",
  "expansion": "AdminArea",
  "image": "https://miro.medium.com/max/1200/1*mk1-6aYaf_Bes1E3Imhc0A.jpeg",
  "relevance" : 0
})

client.publish("News", payload)