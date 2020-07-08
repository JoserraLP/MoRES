from flask import Blueprint, render_template, request, redirect
from flask_login import login_required
from . import mqtt

import json
import requests

# News blueprint
news = Blueprint('news', __name__)

# -------------- News -------------- #

def create_news_item(request):
    ''' Create a news item with the data inserted on the form and publish it throw MQTT broker

        Parameters:
            request (object): Request with the form data

        Returns:
            Redirect to main web server page.
    '''
    try: 
        # Get data from request
        data = request.form.to_dict()
        # Parse the relevance to a int
        data['relevance'] = int(data['relevance'])
        # Create the MQTT payload
        payload = json.dumps(data)
        # Publish the payload on the MQTT broker
        mqtt.publish('News', payload)

        return render_template('index.html')
    except requests.exceptions.RequestException as e:   
        raise SystemExit(e)

@news.route('/add_news', methods=['GET', 'POST'])
@login_required
def add_news():
    ''' Add news method

        Returns:
            Add news item form page and publish the news item or show the news form depending on the request method
    '''
    # POST method -> create a news item an publish it
    if request.method == 'POST':
        return create_news_item(request)
    # GET method -> show add news item form
    else:
        return render_template('add_news_item.html')