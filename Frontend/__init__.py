from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_mqtt import Mqtt
from flask_login import LoginManager
from flask_user import UserManager

# init SQLAlchemy
db = SQLAlchemy()

# init MQTT
mqtt = Mqtt()

@mqtt.on_connect()
def handle_connect(client, userdata, flags, rc):
    print("Connected to MQTT")

SERVER_API_URL = "http://192.168.1.83:8080"
PLACES_API_URL = "https://places.demo.api.here.com/places/v1/categories/places/?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg#"

def create_app():
    app = Flask(__name__)

    app.config['SECRET_KEY'] = 'super_secret_key_for_the_flask_server'

    # -------------- MQTT -------------- #

    app.config['MQTT_BROKER_URL'] = '192.168.1.83'
    app.config['MQTT_BROKER_PORT'] = 1883

    mqtt.init_app(app)

    # Import models to create the tables
    from .models import User, Role

    # -------------- Flask user manager -------------- #

    # Flask-User settings
    app.config['USER_APP_NAME'] = "TFG Frontend"      # Shown in and email templates and page footers
    app.config['USER_ENABLE_EMAIL'] = True        # Enable email authentication
    app.config['USER_ENABLE_USERNAME'] = False    # Disable username authentication
    app.config['USER_EMAIL_SENDER_NAME'] = "TFG Frontend"
    app.config['USER_EMAIL_SENDER_EMAIL'] = "noreply@example.com"

    user_manager = UserManager(app, db, User)

    # -------------- DB -------------- #
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///db.sqlite'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False  # This will be removed in the future

    db.init_app(app)
    with app.app_context():
        db.create_all()
        # If the are no users create them
        if not User.query.limit(1).all() and not Role.query.limit(1).all():
            from .add_to_db import add_to_db
            add_to_db(db)

    # -------------- Flask log in -------------- #
    login_manager = LoginManager()
    login_manager.login_view = 'auth.login'
    login_manager.init_app(app)

    @login_manager.user_loader
    def load_user(user_id):
        # since the user_id is just the primary key of our user table, use it in the query for the user
        try:
            return User.query.get(int(user_id))
        except:
            return None
 
    # Register project blueprints 
    # Auth routes
    from .auth import auth as auth_blueprint
    app.register_blueprint(auth_blueprint)

    # Non-auth routes 
    from .main import main as main_blueprint
    app.register_blueprint(main_blueprint)

    return app
