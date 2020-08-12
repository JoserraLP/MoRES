"""Flask config."""

SECRET_KEY = 'super_secret_key_for_testing_purpose'

# -------------- MQTT -------------- #
MQTT_BROKER_URL = '90.169.70.108'
MQTT_BROKER_PORT = 1883

# -------------- Flask-User -------------- #
USER_APP_NAME = "TFG Frontend"      # Shown in and email templates and page footers
USER_ENABLE_EMAIL = True            # Enable email authentication
USER_ENABLE_USERNAME = False        # Disable username authentication
USER_EMAIL_SENDER_NAME = USER_APP_NAME
USER_EMAIL_SENDER_EMAIL = "noreply@example.com"

# -------------- SQLAlchemy - DB -------------- #
SQLALCHEMY_DATABASE_URI = 'sqlite:///db.sqlite'
SQLALCHEMY_TRACK_MODIFICATIONS = False  # This will be deprecated in the future