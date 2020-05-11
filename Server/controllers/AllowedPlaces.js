




'use strict';

var AllowedPlaces = require('../service/AllowedPlacesService');

module.exports.deleteAllowedPlaces = function deleteAllowedPlaces (req, res, next) {

    AllowedPlaces.deleteAllowedPlaces(req.swagger.params, res, next);

};

module.exports.getAllowedPlace = function getAllowedPlace (req, res, next) {

    AllowedPlaces.getAllowedPlace(req.swagger.params, res, next);

};

module.exports.postAllowedPlace = function postAllowedPlace (req, res, next) {

    AllowedPlaces.postAllowedPlace(req.swagger.params, res, next);

};

module.exports.putAllowedPlace = function putAllowedPlace (req, res, next) {

    AllowedPlaces.putAllowedPlace(req.swagger.params, res, next);

};
