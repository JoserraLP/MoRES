import paho.mqtt.client as mqtt

import json

client = mqtt.Client("News_Publisher")

client.connect("90.169.70.108", port=1883)

payload = json.dumps({ 
  "title": "News_Test_Extremadura",
  "date": "07/05/2020",
  "author": "JoserraLP",
  "description": "This is the first description",
  "location": "Extremadura",
  "expansion": "AdminArea",
  "image": "https://www.cancer.org/es/noticias-recientes/preguntas-comunes-acerca-del-brote-del-nuevo-coronavirus/_jcr_content/par/image.img.jpg/1586550990157.jpg",
  "relevance" : 0
})

client.publish("News", payload)