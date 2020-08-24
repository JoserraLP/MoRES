from flask import Blueprint, render_template, request, flash, redirect
from flask_login import current_user
from flask_user import roles_required
from sqlalchemy import desc
from .static.constants import SERVER_API_URL
from . import geolocator, mqtt

from .models import Observation

import requests
import ast
import json

# Statistics blueprint
statistics = Blueprint('statistics', __name__)

# -------------- Statistics -------------- #
@statistics.route('/statistics')
@roles_required(['admin', 'politician_country', 'politician_admin_area', 'politician_locality'])
def show_statistics():
    ''' Retrieve basic statistics as number of devices in some time intervals

        Returns:
            Redirect to the statistics page 
    '''
    try:

        # Get current location
        location = geolocator.geocode(current_user.location, language='en')

        # Retrieve the area
        area = location.raw['boundingbox']
        
        # Initialize the statistics dict
        data = {}

        # Statistics -> 5 minutes
        data['5 Minutes'] = retrieve_num_devices(area, 5)

        # Statistics -> 60 minutes = 1 hour
        data['1 Hour'] = retrieve_num_devices(area, 60)
        
        # Statistics -> 720 minutes = 12 hours
        data['12 Hours'] = retrieve_num_devices(area, 720)

        # Statistics -> 1440 minutes = 24 hours
        data['1 Day'] = retrieve_num_devices(area, 1440)

        # Statistics -> 10080 minutes = 7 days = 1 week
        data['1 Week'] = retrieve_num_devices(area, 10080)

        # Statistics -> 282240 minutes = 28 days = 4 week = 1 month
        data['1 Month'] = retrieve_num_devices(area, 282240)

        # Statistics -> Total number
        data['Total'] = retrieve_num_devices(area, -1)

        json_data = json.dumps(data)

        # Search observations in the db
        observations = Observation.query.filter_by(location=current_user.location).order_by(desc(Observation.timestamp))

        json_obs = { "data" : []}
        for obs in observations:
            json_obs["data"].append({"name": obs.name, "description": obs.description, "timestamp": obs.timestamp})
            
        return render_template('statistics.html', data=json_data, obs=json_obs["data"])

    except Exception as e:
        print(e)
        # Exception -> show message    
        flash('Error, the statistics could not be loaded')
        return render_template('statistics.html')
    
def retrieve_num_devices(area, minutes):
    ''' Parse selected allowed places types to a list of objects that fits the API

        Parameters:
            area (list[float]): 
                List with the area coordinates
            minutes (int): 
                Number of minutes


        Returns:
            Number of devices that have been connected in the last "minutes"  
    '''
    
    # Initialize the params
    params = {
        # API receives str representing the list
        "area": str(area)
    }

    # Set the mins parameter
    if (minutes != -1):
        params['mins'] = minutes

    # Make the request
    r_server_api = requests.get(SERVER_API_URL + '/area_devices', params=params)

    # Return the response
    return r_server_api.json()['num_devices']
