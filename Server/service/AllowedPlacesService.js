'use strict';

// Mongo
var mongoClient = require('mongodb').MongoClient;
var mongoURL = "mongodb://localhost:27017/"

// DB and Collection creation
mongoClient.connect(mongoURL, function(err, db) {
    if (err) throw err;
    console.log("Database created!");
    var dbase = db.db("tfg");
    dbase.createCollection("allowed_places", function(err, res){
        if (err) throw err;
        console.log("Collection created!");
    });
    dbase.collection("allowed_places").createIndex({"type": 1}, {unique : true});
    db.close();
}); 


/**
 * Delete the allowed place data.
 * This deletes the current allowed places.
 *
 * idAllowedPlace Integer Allowed Place ID
 * returns String
 **/
module.exports.deleteAllowedPlaces = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var query = { type : req.type.value };
        var dbase = db.db("tfg");
        dbase.collection("allowed_places").deleteOne(query, function(err, obj) {
            if (err) throw err;
            console.log("Allowed place with type " + req.type.value + " succesfully deleted");
            db.close();
        }) 
    });
    res.send({
        message: "Allowed place with type " + req.type.value + " succesfully deleted"
    });
};


/**
 * Return all the allowed places
 * Return all the allowed places
 *
 * returns String
 **/
module.exports.getAllowedPlace = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
        dbase.collection("allowed_places").aggregate(
                [
                    { $project : // Exclude the _id field
                        {  
                            "_id": 0, 
                            "type": 1
                        }
                    }
                ]).toArray(function(err, result) {
            if (err) throw err;
            console.log("Allowed places: " + JSON.stringify(result));
            res.send({
                message: result
            });
            db.close();
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
module.exports.postAllowedPlace = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
        var data = {
            type: req.undefined.value.type
        }
        dbase.collection("allowed_places").insertOne(data, function(err, response) {
            if (err) {
                res.statusCode = 409 
                res.send({
                    message: "The allowed place " + req.undefined.value.type + " already exists"
                })
                throw err;
            }
            res.send({
                message: "Allowed place " + req.undefined.value.type + " succesfully added"
            });
            console.log("Allowed place " + req.undefined.value.type + " succesfully added");
            db.close();
        }); 
    });
};





