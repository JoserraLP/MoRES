'use strict';

// Mongo
var mongodb = require('mongodb');
var mongoClient = mongodb.MongoClient;
var mongoURL = "mongodb://localhost:27017/";

/**
 * Return the number of connected devices within the specified time
 * Return the number of connected devices within the specified time
 *
 * returns String
 **/
module.exports.getLastConnectedDevices = function(req, res, next) {
    //Parameters
    console.log("Request: " + JSON.stringify(req));
    mongoClient.connect(mongoURL, function(err, db) {
        if (err) throw err;
        var dbase = db.db("tfg");
        var minutes = req.mins.value;
        console.log(minutes);
        console.log(new Date())
        
        dbase.collection("device").aggregate(
                [   
                    {  $match: 
                        {
                            "lastUpdate": {$gte: new Date(Date.now() - 1000 * 60 * minutes) }  
                        }
                    },
                    { $project : // Include the _id field
                        {  
                            "_id": 1,
                            "location": 1,
                            "lastUpdate": 1
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
