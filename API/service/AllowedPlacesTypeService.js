'use strict';

// Mongo
var mongoClient = require('mongodb').MongoClient;
var mongoURL = "mongodb://localhost:27017/"

// MQTT
var mqtt = require('mqtt')
var mqttClient = mqtt.connect('mqtt://192.168.1.83')

mqttClient.on("connect", function(){
    console.log("Connected to MQTT Broker");
});

mqttClient.on("error",function(error){
    console.log("Can't connect" + error);
    process.exit(1);
});


/**
 * Delete the allowed place type.
 * This deletes the current allowed places type.
 *
 * idAllowedPlace Integer Allowed Place Type ID
 * returns String
 **/
module.exports.deleteAllowedPlacesType = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var query = { type : req.type.value };
        var dbase = db.db("tfg");
        dbase.collection("allowed_places_types").deleteOne(query, function(err, obj) {
            if (err) throw err;
            console.log("Allowed place with type " + req.type.value + " succesfully deleted");
        }) 
    });
    res.send({
        message: "Allowed place with type " + req.type.value + " succesfully deleted"
    });
};


/**
 * Return all the allowed places types
 * Return all the allowed places types
 *
 * returns String
 **/
module.exports.getAllowedPlacesType = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
        dbase.collection("allowed_places_types").aggregate(
                [
                    { $project : // Exclude the _id field
                        {  
                            "_id": 0, 
                            "type": 1,
                            "title": 1,
                            "icon": 1
                        }
                    }
                ]).toArray(function(err, result) {
            if (err) throw err;
            console.log("Allowed places types: " + JSON.stringify(result));
            res.send({
                results: result
            });
        }); 
    });
};


/**
 * Add a new allowed place.
 * Addition of a new allowed place
 *
 * allowedPlaces AllowedPlaces 
 * returns String
 **/
module.exports.postAllowedPlaceType = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
        var data = req.undefined.value

        dbase.collection("allowed_places_types").deleteMany({});

        dbase.collection("allowed_places_types").insertMany(data, function(err, response) {
            if (err) throw err;

            res.send({
                message: "Allowed places types updated"
            });

            if (mqttClient.connected){
                mqttClient.publish('AllowedPlacesTypes', "1");
                console.log("Allowed places types updated");
            }
        }); 
    });
};





