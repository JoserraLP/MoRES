




'use strict';

var AllowedPlaces = require('../service/AllowedPlacesService');

module.exports.getAllowedPlaces = function getAllowedPlaces (req, res, next) {

    AllowedPlaces.getAllowedPlaces(req.swagger.params, res, next);

};
