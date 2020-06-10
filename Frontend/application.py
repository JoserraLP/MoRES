from flask import Flask, render_template, request

import requests
import ast

SERVER_API_URL = "http://192.168.1.83:8080"
PLACES_API_URL = "https://places.demo.api.here.com/places/v1/categories/places/?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg#"

app = Flask(__name__)

@app.route('/')
def index():
    return 'Index Page'

@app.route('/allowed_places_types', methods=['GET', 'POST'])
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
        raise SystemExit(e)
    
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
        return render_template('allowed_places_type.html', results=parsed_selected)
    except requests.exceptions.RequestException as e:    
        raise SystemExit(e)

if __name__ == "__main__":
    app.run()