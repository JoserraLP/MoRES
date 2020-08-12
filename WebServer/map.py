from flask import Blueprint, render_template, request, url_for, redirect
from flask_login import login_required, current_user
from flask_user import roles_required
from .static.constants import LOCATION_API_URL, RADIUS_ROLE
from . import mqtt, geolocator

import json
import requests

# Map blueprint
map = Blueprint('map', __name__)

# -------------- Map -------------- #

def get_cur_location(ip_address):
    ''' Retrieve the current user location by its IP address.

        Parameters:
            ip_address (str): Device IP address 

        Returns:
            dict: User's device location 

    '''
    try:
        # Make the request to the location API
        response = requests.get(LOCATION_API_URL + "{}".format(ip_address))
        
        # Retrieve response body
        location = response.json()

        return location
    except Exception:
        return "Unknown"

@map.route('/map')
@login_required
def show_map():
    ''' Map page related with the user's device location. 
    
        Returns:
            Map page with the current device location
    '''
    try:
        # Get current user roles
        current_user_roles = [role.name for role in current_user.roles]

        # Parse to JSON
        json_roles = json.dumps(current_user_roles)

        if ("police" in current_user_roles):
            # Get user's device IP
            ip_address = request.remote_addr

            # Get current device location
            cur_location = get_cur_location(ip_address)

            if cur_location['status'] != 'fail':
                # Remote IP loaded

                # Set the params to make the request
                params = {'lat': cur_location['lat'], 'lng': cur_location['lon'], 'rad': 1000}
                
                return render_template('map.html', data=params, cur_location=cur_location['city'], user_roles=json_roles, zoom=12)
                
            else:
                # Remote IP could not be loaded

                # Params are defined to appear in a static place
                params = {'lat': -6.9706100, 'lng': 38.8778900, 'rad': 12000}

                return render_template('map.html', data=params, cur_location="Badajoz", user_roles=json_roles)
        else:
            # Get current location
            cur_location = geolocator.geocode(current_user.location, language='en')

            # Set the params to make the request
            params = {'lat': cur_location.raw['lat'], 'lng': cur_location.raw['lon'], 'rad': RADIUS_ROLE[current_user_roles[0]]}

            if ("politician_country" in current_user_roles):
                zoom = 6
            elif ("politician_admin_area" in current_user_roles):
                zoom = 10
            elif ("politician_locality" in current_user_roles):
                zoom = 14
            else:
                zoom = 10

            return render_template('map.html', data=params, cur_location=cur_location.raw['display_name'], user_roles=json_roles, zoom=zoom)
    except requests.exceptions.RequestException as e:    
        raise SystemExit(e)

@map.route('/send_patrol', methods=['POST'])
@roles_required(['police'])
def send_patrol():
    ''' Send a MQTT message on the topic "Patrol" with the location selected in the map. 
    
        Returns:
            Redirect to the map page
    '''
    
    # Coordinates of the selected location
    data = request.form.to_dict()
    # Create the MQTT payload
    payload = json.dumps(data)
    # Publish the payload on the MQTT broker
    mqtt.publish("Patrol", payload)

    return redirect(url_for('map.show_map'))