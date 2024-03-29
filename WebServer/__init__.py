from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_mqtt import Mqtt
from flask_login import LoginManager
from flask_user import UserManager

from geopy.geocoders import Nominatim

from .config import default

# init geolocator
geolocator = Nominatim(user_agent=__name__)

# init SQLAlchemy
db = SQLAlchemy()

# init MQTT
mqtt = Mqtt()

@mqtt.on_connect()
def handle_connect(client, userdata, flags, rc):
    ''' Handle connection to MQTT broker. '''
    print("Connected to MQTT")

def create_app():
    ''' Create a Flask app, configure it. register some project blueprints as 'auth' or 'main', 
        initialize related services as MQTT or SQLAlchemy (with data insertion) and the flask login manager.

        Returns:
            app (object): Configured Flask app

    '''
    # Create Flask app 
    app = Flask(__name__, instance_relative_config=True)

    # Configure the application with the config file
    app.config.from_object(default)

    # Register project blueprints 

    # -> Auth routes
    from .auth import auth as auth_blueprint
    app.register_blueprint(auth_blueprint)

    # -> Non-auth routes 
    from .main import main as main_blueprint
    app.register_blueprint(main_blueprint)

    # -> Map routes 
    from .map import map as map_blueprint
    app.register_blueprint(map_blueprint)

    # -> Allowed places types routes 
    from .allowed_places_types import allowed_places as allowed_places_types_blueprint
    app.register_blueprint(allowed_places_types_blueprint)

    # -> News routes 
    from .news import news as news_blueprint
    app.register_blueprint(news_blueprint)

    # -> Statistics routes 
    from .statistics import statistics as statistics_blueprint
    app.register_blueprint(statistics_blueprint)

    # Init the MQTT service
    mqtt.init_app(app)

    # Subscribe to CEP observations
    mqtt.subscribe("Observations")

    # Import models to create the tables
    from .models import User, Role

    # Init the Flask-User Manager service 
    user_manager = UserManager(app, db, User)

    # Init the SQLAlchemy - DB service
    db.init_app(app)

    # Create the tables with the application context
    with app.app_context():
        db.create_all()
        # If there are no users and no roles create and insert them on the db
        if not User.query.limit(1).all() and not Role.query.limit(1).all():
            from .utils.insert_data_to_db import insert_user_data
            insert_user_data(db)

    @mqtt.on_message()
    def on_message(client, userdata, message):
        if (message.topic == "Observations"):
            with app.app_context():
                obs = str(message.payload.decode("utf-8")).split(',')

                from .utils.insert_data_to_db import insert_observation_data        
                
                insert_observation_data(db, obs=obs)

    # Create the LoginManager
    login_manager = LoginManager()

    # Set the login manager main view as the login service
    login_manager.login_view = 'auth.login'

    # Init the LoginManager service
    login_manager.init_app(app)

    # Define the user loader for the LoginManager
    @login_manager.user_loader
    def load_user(user_id):
        # since the user_id is just the primary key of our user table, use it in the query for the user
        try:
            return User.query.get(int(user_id))
        except:
            return None

    return app
