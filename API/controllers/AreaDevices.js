




'use strict';

var AreaDevices = require('../service/AreaDevicesService');

module.exports.getAreaDevices = function getAreaDevices (req, res, next) {

    AreaDevices.getAreaDevices(req.swagger.params, res, next);

};
