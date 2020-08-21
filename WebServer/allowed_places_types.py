from flask import Blueprint, render_template, request, flash, url_for, redirect
from flask_login import current_user
from flask_user import roles_required
from .static.constants import SERVER_API_URL
from . import geolocator

import requests
import ast

# Allowed places types blueprint
allowed_places = Blueprint('allowed_places', __name__)

# -------------- Allowed Places Types -------------- #

@allowed_places.route('/allowed_places_types', methods=['GET', 'POST'])
@roles_required(['admin', 'politician_country', 'politician_admin_area', 'politician_locality'])
def allowed_places_types():
    ''' Select allowed places types by user role and user location

        Returns:
            (De)Select allowed places types by user role or show the previously selected allowed places types
    '''
    # POST method -> (de)select the allowed places types and send the updates to the API
    if request.method == 'POST':
        return select_allowed_places_types(request)
    # GET method -> get the allowed places types from the API and the current user location and show them
    else:
        return retrieve_allowed_places_types()

def check_places_by_user_location(allowed_places_types, user_location):
    ''' Add a field to each element that has been previously selected, depending of the user role and location.

        Parameters:
            allowed_places_types (list[object]): 
                List with all the allowed places types stored in the API
            user_location (str):
                User location

        Returns:
            allowed_places_types (list[object]): Allowed places types with an extra field 
    '''
    # Get current user roles
    current_user_roles = [role.name for role in current_user.roles]

    for elem in allowed_places_types:
        if 'block' not in elem:
            # Check if the role is admin or politician_country to check the country field
            if 'politician_country' in current_user_roles or 'admin' in current_user_roles:
                for country in elem['country']:
                    if user_location == country:
                        # If the place type is allowed in the country
                        elem['is_allowed'] = True
            # Check if the role is politician_admin_area to check the admin_area field
            elif 'politician_admin_area' in current_user_roles: 
                for admin_area in elem['admin_area']:
                    if user_location == admin_area:
                        # If the place type is allowed in the admin_area
                        elem['is_allowed'] = True
            # Check if the role is politician_locality or police to check the locality field
            elif 'politician_locality' in current_user_roles or 'police' in current_user_roles:
                for locality in elem['locality']:
                    if user_location == locality:
                        # If the place type is allowed in the locality
                        elem['is_allowed'] = True
    return allowed_places_types
                    
def get_allowed_places_types_by_current_user_roles():
    ''' Request API to all the allowed places types by the current user roles

        Returns:
            list[(object)] : 
                List with the objects from the API request by current user roles     
    '''
    # Get current user roles
    current_user_roles = [role.name for role in current_user.roles]

    # Define params 
    # (e.g. in roles == ['admin', 'politician_country'] params must be null because we want to retrieve all the allowed places types)
    params = {}

    # Get current location
    location = geolocator.geocode(current_user.location, language='en')

    # If the current user has the role 'politician_admin_area'
    if 'politician_admin_area' in current_user_roles:
    
        # Get the country name
        country = location.raw['display_name'].split(', ')[1] # [Admin area, Country]

        # Define the params for the API request
        params = {
            "location_type": "country",
            "location": country
        }

    # If the current user has the role 'politician_locality' or 'police'
    elif 'politician_locality' in current_user_roles or 'police' in current_user_roles:

        # Get the admin area name
        admin_area = location.raw['display_name'].split(', ')[-2]  # [Locality, ... , Admin area, Country]

        # Define the params for the API request
        params = {
            "location_type": "admin_area",
            "location": admin_area
        }

    # Make the request to the API with the params
    places_r_server_api = requests.get(SERVER_API_URL + '/allowed_places_types', params=params)

    # Make the request to the API for all the allowed places
    all_places_r_server_api = requests.get(SERVER_API_URL + '/allowed_places_types')

    # Compare results
    places_results = compare_results (all_places_r_server_api.json()['results'], places_r_server_api.json()['results'])

    # Order the results
    ordered_response = sorted(places_results, key = lambda x : (x['icon'], x['title']))

    return ordered_response

def compare_results(all_places_results, places_results):
    ''' Compare both results and return the list with the values

        Parameters:
            all_places_results (list[object]): 
                List with all places stored in the API
            places_results (list[object]): 
                List with places allowed in a specific place

        Returns:
            places_results (list[object]): List with all the compared results 
    '''
    for item in all_places_results:
        if item not in places_results:
            item['is_allowed'] = False
            item['block'] = True
            places_results.append(item)

    return places_results

def retrieve_allowed_places_types():
    ''' Retrieve allowed places types from the API and show them in the allowed places types selection page

        Returns:
            Allowed places types selection page
    '''
    try:

        # Get the allowed places types from API by role
        api_results = get_allowed_places_types_by_current_user_roles()

        # Retrieve and process the API response results
        results = check_places_by_user_location(api_results, current_user.location)

        return render_template('allowed_places_type.html', results=results)

    except:
        # Exception -> show message    
        flash('Error, the allowed places types could not be loaded')
        return render_template('allowed_places_type.html')
    
def parse_selected(selected_results):
    ''' Parse selected allowed places types to a list of objects that fits the API

        Parameters:
            selected_results (list[object]): 
                List with all the selected allowed places types by the user

        Returns:
            list_parsed (list[object]): List with all the selected allowed places types 
    '''
    # Initilize list_parsed
    list_parsed = []

    for item in selected_results:
        # Parse str (representing a object) to object
        item = ast.literal_eval(item)
        # Create object item
        selected_item = dict()
        # Introduce data in the object
        selected_item['type'] = item['type']
        selected_item['title'] = item['title']
        selected_item['icon'] = item['icon']
        selected_item['country'] = item['country']
        selected_item['admin_area'] = item['admin_area']
        selected_item['locality'] = item['locality']
        # Add item to the list
        list_parsed.append(selected_item)
    return list_parsed

def select_allowed_places_types(request):
    ''' (De)Select allowed places types in the page and send the updates to the API

        Parameters:
            request (object): 
                request with the form data

        Returns:
            Redirect to the main page 
    '''
    try: 

        # Get the allowed places types from API by current user roles
        api_results = get_allowed_places_types_by_current_user_roles()

        # Get selected allowed places type by user
        selected_results = parse_selected(request.form.getlist("allowed_places_types"))

        # Get selected elements by intersect
        intersect_results = [item for item in api_results if item in selected_results]

        # Get non-selected elements
        difference_results = [item for item in api_results if item not in selected_results]
        
        # Get current user roles
        current_user_roles = [role.name for role in current_user.roles]

        for item in intersect_results:
            data = {
                "type": item['type'],
                "location": current_user.location,
                "action": "add"
            }
            # If the current user has the role 'admin' or 'politician_country'
            if 'politician_country' in current_user_roles or 'admin' in current_user_roles:
                data ["location_type"] = "country"
            # If the current user has the role 'politician_admin_area'
            elif 'politician_admin_area' in current_user_roles:
                data ["location_type"] = "admin_area"
            # If the current user has the role 'politician_locality' or 'police'
            elif 'politician_locality' in current_user_roles or 'police' in current_user_roles:
                data ["location_type"] = "locality"
            requests.put(SERVER_API_URL + '/allowed_places_types', json=data)
            

        for item in difference_results:
            data = {
                "type": item['type'],
                "location": current_user.location,
                "action": "remove"
            }
            # If the current user has the role 'admin' or 'politician_country'
            if 'politician_country' in current_user_roles or 'admin' in current_user_roles:
                data ["location_type"] = "country"
            # If the current user has the role 'politician_admin_area'
            elif 'politician_admin_area' in current_user_roles:
                data ["location_type"] = "admin_area"
            # If the current user has the role 'politician_locality' or 'police'
            elif 'politician_locality' in current_user_roles or 'police' in current_user_roles:
                data ["location_type"] = "locality"
            requests.put(SERVER_API_URL + '/allowed_places_types', json=data)

        return redirect(url_for('main.index'))

    except requests.exceptions.RequestException as e:    
        raise SystemExit(e)