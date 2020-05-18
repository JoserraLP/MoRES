'use strict';

// Mongo
var mongoClient = require('mongodb').MongoClient;
var mongoURL = "mongodb://localhost:27017/"

/**
 * Return all the allowed places given a radius and two coordinates
 * Return all the allowed places given a radius and two coordinates
 *
 * returns String
 **/
module.exports.getAllowedPlaces = function(req, res, next) {
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
                        coordinates: [req.lat.value , req.long.value]
                    },
                    $maxDistance : req.radius.value,
                    $minDistance : 0
                }
            }
        }
        dbase.collection("allowed_places").findMany(query, function(err, result){
            if (err) throw err;
            console.log("Allowed places: " + JSON.stringify(result));
            res.send({
                results: result
            });
        });
    });
};