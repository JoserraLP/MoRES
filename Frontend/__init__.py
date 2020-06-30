from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_mqtt import Mqtt
from flask_login import LoginManager
from flask_user import UserManager

from .config import default

from geopy.geocoders import Nominatim

# init geolocator
geolocator = Nominatim(user_agent=__name__)

# init SQLAlchemy
db = SQLAlchemy()

# init MQTT
mqtt = Mqtt()

@mqtt.on_connect()
def handle_connect(client, userdata, flags, rc):
    print("Connected to MQTT")

def create_app():
    app = Flask(__name__, instance_relative_config=True)

    # Configure the application with the config file
    app.config.from_object(default)

    # Register project blueprints 

    # Auth routes
    from .auth import auth as auth_blueprint
    app.register_blueprint(auth_blueprint)

    # Non-auth routes 
    from .main import main as main_blueprint
    app.register_blueprint(main_blueprint)

    # Init the MQTT service
    mqtt.init_app(app)

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
            from .add_to_db import add_to_db
            add_to_db(db)

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
