from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_mqtt import Mqtt
from flask_login import LoginManager

# init SQLAlchemy
db = SQLAlchemy()

# init MQTT
mqtt = Mqtt()

def create_app():
    app = Flask(__name__)

    app.config['SECRET_KEY'] = 'super_secret_key'

    # -------------- MQTT -------------- #

    app.config['MQTT_BROKER_URL'] = '192.168.1.83'
    app.config['MQTT_BROKER_PORT'] = 1883

    mqtt = Mqtt(app)

    # -------------- DB -------------- #
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///db.sqlite'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False  # This will be removed in the future

    db.init_app(app)

    # -------------- Flask log in -------------- #
    login_manager = LoginManager()
    login_manager.login_view = 'auth.login'
    login_manager.init_app(app)

    from .models import User

    @login_manager.user_loader
    def load_user(user_id):
        # since the user_id is just the primary key of our user table, use it in the query for the user
        return User.query.get(int(user_id))

    # Register project blueprints 
    # Auth routes
    from .auth import auth as auth_blueprint
    app.register_blueprint(auth_blueprint)

    # Non-auth routes 
    from .main import main as main_blueprint
    app.register_blueprint(main_blueprint)

    return app
