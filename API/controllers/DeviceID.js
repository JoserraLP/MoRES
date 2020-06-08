




'use strict';

var DeviceID = require('../service/DeviceIDService');

module.exports.deleteDeviceID = function deleteDeviceID (req, res, next) {

    DeviceID.deleteDeviceID(req.swagger.params, res, next);

};

module.exports.getDeviceID = function getDeviceID (req, res, next) {

    DeviceID.getDeviceID(req.swagger.params, res, next);

};

module.exports.postDeviceID = function postDeviceID (req, res, next) {

    DeviceID.postDeviceID(req.swagger.params, res, next);

};
