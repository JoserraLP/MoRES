'use strict';

// Mongo
var mongodb = require('mongodb');
var mongoClient = mongodb.MongoClient;
var mongoURL = "mongodb://localhost:27017/";

/**
 * Return the position of the devices within an area
 * Return the area devices
 *
 * returns String
 **/
module.exports.getAreaDevices = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
    
        var coordinates = req.area.value.replace('[', '').replace(']', '').split(', ')
        
        for (var i = 0; i < coordinates.length; i++) {
            coordinates[i] = parseFloat(coordinates[i].replace('\'', ''));
        }

        var query = {	
            "location" : {	
                $geoWithin : {	
                    $box : [
                        [parseFloat(coordinates[0]), parseFloat(coordinates[3])],
                        [parseFloat(coordinates[1]), parseFloat(coordinates[2])]
                    ]
                }	
            }
        }	

        var lastConnectTime = req.mins.value
        if (lastConnectTime){
            query.lastUpdate = { $gt : new Date(Date.now() - 1000 * 60 * lastConnectTime) };// lastConnectedTime Minutes
        }

        dbase.collection("device").countDocuments(query, function(err, result) {
            if (err) throw err;
            console.log("Area devices num: " + JSON.stringify(result));	
            // To avoid frontend CORS error
            res.setHeader('Access-Control-Allow-Origin', 'http://192.168.1.61:5000');
            res.setHeader('Access-Control-Allow-Origin', 'http://127.0.0.1:5000');
            
            res.send({	
                num_devices: result
            });
        });
    });
};
