




'use strict';

var NearbyDevices = require('../service/NearbyDevicesService');

module.exports.getNearbyDevices = function getNearbyDevices (req, res, next) {

    NearbyDevices.getNearbyDevices(req.swagger.params, res, next);

};
