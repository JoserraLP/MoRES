'use strict';

var fs = require('fs'),
http = require('http'),
path = require('path'),
https = require('https')

// Mongo
var mongodb = require('mongodb');
var mongoClient = mongodb.MongoClient;
var mongoURL = "mongodb://localhost:27017/";
var mongoOptions = { useUnifiedTopology: true };

// DB and Collection creation
mongoClient.connect(mongoURL, mongoOptions, function(err, db) {
    if (err) throw err;
    var dbase = db.db("tfg");

    dbase.createCollection("allowed_places_types", function(err, res){
        if (err) throw err;
    });
    dbase.collection("allowed_places_types").createIndex({"type": 1}, {unique : true}); 

    dbase.createCollection("device", function(err, res){
        if (err) throw err;
    });
    dbase.collection("device").createIndex({"location" : "2dsphere"});
    //dbase.collection("device").createIndex({"createdAt" : 1}, {expireAfterSeconds: 86400}); // 24 hours to delete the document

}); 


// MQTT
var mqtt = require('mqtt'); 
var client = mqtt.connect("mqtt://192.168.1.83");

client.on("connect", function(){
    console.log("Connected to MQTT Broker");
});

client.on("error",function(error){
    console.log("Can't connect" + error);
    process.exit(1);
});

client.subscribe('Location');

client.on('message', function(topic, message, packet){
	console.log("message is "+ message);
    console.log("topic is "+ topic);
    mongoClient.connect(mongoURL, mongoOptions, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
        message = JSON.parse(message);
        var data = {
            $set : {
                lastUpdate: new Date(),
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
const { parse } = require('path');
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

oasTools.configure(options_object);

// Load all allowed places types from the external API
mongoClient.connect(mongoURL, mongoOptions, function(err, db) {
    if (err) throw err;
    var allowed_places_types_api = 'https://places.demo.api.here.com/places/v1/categories/places/?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg#'
    var dbase = db.db("tfg");
    
    dbase.collection("allowed_places_types").countDocuments(function (err, count){
        if (err) throw err;
        if (count === 0){
            var results = []
            https.get(allowed_places_types_api, (resp) => {
                let data = '';

                // A chunk of data has been recieved.
                resp.on('data', (chunk) => {
                    data += chunk;
                });

                // The whole response has been received. Print out the result.
                resp.on('end', () => {
                    JSON.parse(data)['items'].forEach(element => {
                        let item = {
                            "type": element.id,
                            "title": element.title,
                            "icon": element.icon,
                            "country": ['Spain', 'France', 'Portugal', 'England'], // Some values for testing
                            "admin_area": ['Andalucia', 'Extremadura', 'Madrid'],
                            "locality": ['Caceres', 'Badajoz', 'Merida', 'Don Benito']
                        };
                        dbase.collection("allowed_places_types").insertOne(item, function(err, res) {
                            if (err) throw err;
                        });
                    });
                    db.close();
                });

                }).on("error", (err) => {
                    console.log("Error: " + err.message);
            });
        }
    });
});

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