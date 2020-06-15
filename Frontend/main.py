from flask import Blueprint, render_template, request, flash
from flask_login import login_required, current_user
from . import db, SERVER_API_URL, PLACES_API_URL

from flask_mqtt import Mqtt

import json
import requests
import ast

main = Blueprint('main', __name__)

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
        params = {'lat': -6.9706100, 'lng': 38.8778900, 'rad': 1000000000}

        return render_template('map.html', data=params)
    except requests.exceptions.RequestException as e:    
        raise SystemExit(e)

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

def check_results(results, server_results):
    for elem in results:
        for server_elem in server_results:
            if elem['id'] == server_elem['type']:
                elem['isChecked'] = True

def retrieve_allowed_places_types(results=None):
    try:
        r_places_api = requests.get(PLACES_API_URL)
        results = r_places_api.json()['items']
        r_server_api = requests.get(SERVER_API_URL + '/allowed_places_types')
        server_results = r_server_api.json()['results']
        
        check_results(results, server_results)

        return render_template('allowed_places_type.html', results=results)
    except requests.exceptions.RequestException as e:    
        flash('Error, the allowed places types could not be loaded')
        return render_template('allowed_places_type.html', results=results)
    
def parse_selected(selected):
    list_parsed = []
    for item in selected:
        item = ast.literal_eval(item)
        selected_item = dict()
        selected_item['type'] = item['id']
        selected_item['title'] = item['title']
        selected_item['icon'] = item['icon']
        list_parsed.append(selected_item)
    return list_parsed

def select_allowed_places_types(request):
    try: 
        parsed_selected = parse_selected(request.form.getlist("allowed_places_types"))

        requests.post(SERVER_API_URL + '/allowed_places_types', json=parsed_selected)
        return render_template('index.html')
    except requests.exceptions.RequestException as e:    
        raise SystemExit(e)