'use strict';

var fs = require('fs'),
http = require('http'),
path = require('path'),
https = require('https')

// Mongo
var mongodb = require('mongodb');
var mongoClient = mongodb.MongoClient;
var mongoURL = "mongodb://localhost:27017/";

// DB and Collection creation
mongoClient.connect(mongoURL, function(err, db) {
    if (err) throw err;
    var dbase = db.db("tfg");

    dbase.createCollection("allowed_places_types", function(err, res){
        if (err) throw err;
    });
    dbase.collection("allowed_places_types").createIndex({"type": 1}, {unique : true}); 

    dbase.createCollection("device", function(err, res){
        if (err) throw err;
    });
    dbase.collection("device").createIndex({"createdAt" : 1}, {expireAfterSeconds: 86400}); // 24 hours to delete the document

    dbase.createCollection("allowed_places", function(err, res){
        if (err) throw err;
    });
    dbase.collection("allowed_places").createIndex({"location" : "2dsphere"});

}); 


// MQTT
var mqtt = require('mqtt'); 
var client = mqtt.connect("mqtt://192.168.1.83");

client.on("connect", function(){
    console.log("Connected to MQTT Broker");
})

client.on("error",function(error){
    console.log("Can't connect" + error);
    process.exit(1);
});

client.subscribe('Location');

client.on('message', function(topic, message, packet){
	console.log("message is "+ message);
    console.log("topic is "+ topic);
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
        message = JSON.parse(message);
        var data = {
            $set : {
                createdAt: new Date(),
                location: {
                    type: "Point",
                    coordinates: [message.geo_lat, message.geo_long]
                }
            }
        }
        dbase.collection("device").updateOne({_id: mongodb.ObjectID(message._id)}, data, function(err) {
            if (err) throw err;
            console.log("Device location updated to " + message.geo_lat + " - " + message.geo_long);
        }); 
    });
});

var express = require("express");
var app = express();
var bodyParser = require('body-parser');
app.use(bodyParser.json({
    strict: false
}));
var oasTools = require('oas-tools');
var jsyaml = require('js-yaml');
var serverPort = 8080;

var spec = fs.readFileSync(path.join(__dirname, '/api/openapi.yaml'), 'utf8');
var oasDoc = jsyaml.safeLoad(spec);

var options_object = {
    controllers: path.join(__dirname, './controllers'),
    loglevel: 'info',
    strict: false,
    router: true,
    validator: true
};

var lat = 38.8779
var lng = -6.9680
var radius = 100000
var key = 'AIzaSyDlR3UBqr8GuUTJuYsS3bJ6xozqc4jfnhw'

// Function to load the allowed places from a place, for example, Extremadura
https.get('https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=' + str(lat) + ',' + str(lng) + '&radius=' + str(radius) + '&key=' + key, (resp) => {
  let data = '';

  // A chunk of data has been recieved.
  resp.on('data', (chunk) => {
    data += chunk;
  });

  // The whole response has been received. Print out the result.
  resp.on('end', () => {
    console.log(JSON.parse(data));
    
    // Save the data in mongo
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");

        var results = JSON.parse(date)['results'];

        results.forEach(function(value) {
            data = {
                id : value.id,
                location: { 
                    type: "Point", 
                    coordinates: [ value.geometry.location.lat, value.geometry.location.lng ] 
                },
                name : value.name,
                types : value.types
            }
            dbase.collection("allowed_places").insertOne(data, function(err, res) { // TODO make this more efficient with insertMany
                if (err) throw err;
                console.log("Inserted allowed place with name: " + value.name)
            }); 
        });

    }); 
  });

}).on("error", (err) => {
  console.log("Error: " + err.message);
});

oasTools.configure(options_object);

oasTools.initialize(oasDoc, app, function() {
    http.createServer(app).listen(serverPort, function() {
        console.log("App running at http://localhost:" + serverPort);
        console.log("________________________________________________________________");
        if (options_object.docs !== false) {
            console.log('API docs (Swagger UI) available on http://localhost:' + serverPort + '/docs');
            console.log("________________________________________________________________");
        }
    });
});