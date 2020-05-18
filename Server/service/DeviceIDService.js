'use strict';

// Mongo
var mongodb = require('mongodb');
var mongoClient = mongodb.MongoClient;
var mongoURL = "mongodb://localhost:27017/";

// DB and Collection creation
/*
mongoClient.connect(mongoURL, function(err, db) {
    if (err) throw err;
    var dbase = db.db("tfg");
    dbase.createCollection("device", function(err, res){
        if (err) throw err;
    });
}); 
*/
/**
 * Delete the device ID data.
 * This deletes the current device ID.
 *
 * deviceID Integer Devide ID
 * returns String
 **/
module.exports.deleteDeviceID = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var query = { _id : new mongodb.ObjectID(req.deviceID.value) };
        var dbase = db.db("tfg");
        dbase.collection("device").deleteMany(query, function(err, obj) {
            if (err) throw err;
            console.log("Device with ID " + req.deviceID.value + " succesfully deleted");
            res.send({
                message: "Device with ID " + req.deviceID.value + " succesfully deleted"
            });
        }) 
    });
};


/**
 * Return all the devices ID
 * Return all the devices ID
 *
 * returns String
 **/
module.exports.getDeviceID = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
        dbase.collection("device").aggregate(
                [
                    { $project : // Include the _id field
                        {  
                            "_id": 1
                        }
                    }
                ]).toArray(function(err, result) {
            if (err) throw err;
            console.log("Devices ID: " + JSON.stringify(result));
            res.send({
                results: result
            });
        }); 
    });
};


/**
 * Add a new device ID.
 * Addition of a new device ID
 *
 * Device ID
 * returns String
 **/
module.exports.postDeviceID = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
        var data = {}
        dbase.collection("device").insertOne(data, function(err, response) {
            if (err) throw err;
            res.send({
                _id: response.insertedId
            });
            console.log("Device with ID " + response.insertedId + " succesfully added");
        }); 
    });
};





