'use strict';

// Mongo
var mongoClient = require('mongodb').MongoClient;
var mongoURL = "mongodb://localhost:27017/"

// DB and Collection creation
/*
mongoClient.connect(mongoURL, function(err, db) {
    if (err) throw err;
    var dbase = db.db("tfg");
    dbase.createCollection("allowed_places_types", function(err, res){
        if (err) throw err;
    });
    dbase.collection("allowed_places_types").createIndex({"type": 1}, {unique : true});
}); 
*/

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
        var data = {
            type: req.undefined.value.type,
            title: req.undefined.value.title,
            icon: req.undefined.value.icon
        }
        dbase.collection("allowed_places_types").insertOne(data, function(err, response) {
            if (err) {
                res.statusCode = 409 
                res.send({
                    message: "The allowed place with type " + req.undefined.value.type + " already exists"
                })
                throw err;
            }
            res.send({
                message: "Allowed place with type " + req.undefined.value.type + " succesfully added"
            });
            console.log("Allowed place with type" + req.undefined.value.type + " succesfully added");
        }); 
    });
};





