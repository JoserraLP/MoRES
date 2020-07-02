




'use strict';

var LastConnectedDevices = require('../service/LastConnectedDevicesService');

module.exports.getLastConnectedDevices = function getLastConnectedDevices (req, res, next) {

    LastConnectedDevices.getLastConnectedDevices(req.swagger.params, res, next);

};