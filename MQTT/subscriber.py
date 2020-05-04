import paho.mqtt.client as mqtt

def on_message(client, userdata, message):
    print("message received=" ,str(message.payload.decode("utf-8")))
    print("message topic=",message.topic)

client = mqtt.Client("Subscriber_test")
client.connect("192.168.1.83", port=1883)
client.subscribe("Location")
client.subscribe("Hello")

client.on_message = on_message
client.loop_forever()
