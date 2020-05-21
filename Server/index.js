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