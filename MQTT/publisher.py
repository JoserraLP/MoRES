import paho.mqtt.client as mqtt

client = mqtt.Client("Publisher_test")

client.connect("192.168.1.83", port=1883)

topic = input("Insert the topic: \n")

payload = input("Insert the payload: \n")

client.publish(topic, payload)