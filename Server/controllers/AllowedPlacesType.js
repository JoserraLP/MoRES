




'use strict';

var AllowedPlacesType = require('../service/AllowedPlacesTypeService');

module.exports.deleteAllowedPlacesType = function deleteAllowedPlacesType (req, res, next) {

    AllowedPlacesType.deleteAllowedPlacesType(req.swagger.params, res, next);

};

module.exports.getAllowedPlacesType = function getAllowedPlacesType (req, res, next) {

    AllowedPlacesType.getAllowedPlacesType(req.swagger.params, res, next);

};

module.exports.postAllowedPlaceType = function postAllowedPlaceType (req, res, next) {

    AllowedPlacesType.postAllowedPlaceType(req.swagger.params, res, next);

};
