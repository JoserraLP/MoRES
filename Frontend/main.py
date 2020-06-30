from flask import Blueprint, render_template, request, flash, jsonify, url_for, redirect
from flask_login import login_required, current_user
from flask_user import roles_required

from .static.constants import SERVER_API_URL, PLACES_API_URL

from . import db, mqtt, geolocator

import json
import requests
import ast

radius_role = {
    'admin': 10000000000,
    'politician_country': 10000000000,
    'politician_admin_area': 1000000,
    'politician_locality': 10000
}

main = Blueprint('main', __name__)

def get_cur_location(ip_address):
    try:
        response = requests.get("http://ip-api.com/json/{}".format(ip_address))
        location = response.json()
        return location
    except Exception as e:
        return "Unknown"


@main.route('/')
def index():
    return render_template('index.html')

@main.route('/profile')
@login_required
def profile():
    return render_template('profile.html', name=current_user.name)

# -------------- Map -------------- #

@main.route('/map')
@login_required
def map():
    try:
        ip_address = request.remote_addr
        cur_location = get_cur_location(ip_address)

        if cur_location['status'] != 'fail':
            user_roles = current_user.get_roles()

            params = {'lat': cur_location['lat'], 'lng': cur_location['lon'], 'rad': radius_role[user_roles[0]]}
            
            #TODO make the map with the location of the user
            return render_template('map.html', data=params)
            
        else:
            # Static if IP could not be loaded
            params = {'lat': -6.9706100, 'lng': 38.8778900, 'rad': 1000000000}

            return render_template('map.html', data=params)
    except requests.exceptions.RequestException as e:    
        raise SystemExit(e)

@main.route('/send_patrol', methods=['POST'])
def send_patrol():
    data = request.form.to_dict()
    payload = json.dumps(data)
    mqtt.publish("Patrol", payload)
    return redirect(url_for('main.map'))

# -------------- News -------------- #

def create_news_item(request):
    try: 
        # Get data from request
        data = request.form.to_dict()
        data['relevance'] = int(data['relevance'])
        payload = json.dumps(data)
        # MQTT send news item
        mqtt.publish('News', payload)
        return render_template('index.html')
    except requests.exceptions.RequestException as e:   
        raise SystemExit(e)

@main.route('/add_news', methods=['GET', 'POST'])
def add_news():
    if request.method == 'POST':
        return create_news_item(request)
    else:
        return render_template('add_news_item.html')

# -------------- Allowed Places Types -------------- #

@main.route('/allowed_places_types', methods=['GET', 'POST'])
def allowed_places_types():
    if request.method == 'POST':
        return select_allowed_places_types(request)
    else:
        return retrieve_allowed_places_types()

def check_places_by_user_location(results, user_location, user_roles):
    for elem in results:
        if 'admin' in user_roles or 'politician_country' in user_roles:
            for country in elem['country']:
                if user_location == country:
                    elem['is_allowed'] = True
        elif 'politician_admin_area' in user_roles:
            for admin_area in elem['admin_area']:
                if user_location == admin_area:
                    results['is_allowed'] = True
        elif 'politician_locality' in user_roles or 'police' in user_roles:
            for locality in elem['locality']:
                if user_location == locality:
                    results['is_allowed'] = True
    return results
                    


def retrieve_allowed_places_types(results=None):
    try:
        user_roles = [role.name for role in current_user.roles]
        
        if 'politician_country' in user_roles or 'admin' in user_roles:

            r_server_api = requests.get(SERVER_API_URL + '/allowed_places_types')

            results = check_places_by_user_location(r_server_api.json()['results'], current_user.location, user_roles)

        elif 'politician_admin_area' in user_roles:
            pass
        elif 'politician_locality' in user_roles or 'police' in user_roles:
            pass

        return render_template('allowed_places_type.html', results=results)
    except:    
        flash('Error, the allowed places types could not be loaded')
        return render_template('allowed_places_type.html', results=results)
    
def parse_selected(selected):
    list_parsed = []
    for item in selected:
        item = ast.literal_eval(item)
        selected_item = dict()
        selected_item['type'] = item['type']
        selected_item['title'] = item['title']
        selected_item['icon'] = item['icon']
        selected_item['country'] = item['country']
        selected_item['admin_area'] = item['admin_area']
        selected_item['locality'] = item['locality']
        list_parsed.append(selected_item)
    return list_parsed


def select_allowed_places_types(request):
    try: 
        user_roles = [role.name for role in current_user.roles]
        
        if 'politician_country' in user_roles or 'admin' in user_roles:

            r_server_api = requests.get(SERVER_API_URL + '/allowed_places_types')
            api_results = r_server_api.json()['results']

            selected_results = parse_selected(request.form.getlist("allowed_places_types"))

            # Get selected elements by intersect
            intersect_results = [item for item in api_results if item in selected_results]

            for item in intersect_results:
                data = {
                    "type": item['type'],
                    "location_type": "country",
                    "location": current_user.location,
                    "action": "add"
                }
                requests.put(SERVER_API_URL + '/allowed_places_types', json=data)
            
            # Get non-selected elements
            difference_results = [item for item in api_results if item not in selected_results]
            print(difference_results) 
            for item in difference_results:
                data = {
                    "type": item['type'],
                    "location_type": "country",
                    "location": current_user.location,
                    "action": "remove"
                }
                requests.put(SERVER_API_URL + '/allowed_places_types', json=data)
                
        elif 'politician_admin_area' in user_roles:
            pass
        elif 'politician_locality' in user_roles or 'police' in user_roles:
            pass
        
        return redirect(url_for('main.index'))
    except requests.exceptions.RequestException as e:    
        raise SystemExit(e)