import paho.mqtt.client as mqtt

def on_message(client, userdata, message):
    print("message received=" ,str(message.payload.decode("utf-8")))
    print("message topic=",message.topic)

client = mqtt.Client("Subscriber_test")

client.connect("90.169.70.108", port=1883)

client.subscribe("Location")
client.subscribe("News")
client.subscribe("Patrol")
client.subscribe("AllowedPlacesTypes")

client.on_message = on_message
client.loop_forever()
