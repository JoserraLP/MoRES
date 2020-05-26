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
 * Return the  position of the nearby devices
 * Return the nearby devices
 *
 * returns String
 **/
module.exports.getNearbyDevices = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");

        var query = {	
            "location" : {	
                $near : {	
                    $geometry : {	
                        type: "Point",	
                        coordinates: [req.lat.value , req.lng.value]	
                    },	
                    $maxDistance : req.rad.value,	
                    $minDistance : 0	
                }	
            }	
        }	

        dbase.collection("device").find(query).toArray(function(err, result) {
            if (err) throw err;
            console.log("Nearby devices: " + JSON.stringify(result));	
                res.send({	
                    results: result	
            });
        });
    });
};
