from flask import Blueprint, render_template, redirect, url_for, request, flash
from werkzeug.security import generate_password_hash, check_password_hash
from flask_login import login_user, login_required, logout_user
from .models import User
from . import db, mqtt

# Auth blueprint
auth = Blueprint('auth', __name__)

# -------------- Login -------------- #

def do_login(request):
    ''' Login the user specified in the form if the password is correct, otherwise redirect to the login and show an error message.

        Parameters:
            request (object): Request with the data necessary to login

        Returns:
            Redirect the user to its profile page or to login page

    '''

    # Retrieve login data
    email = request.form.get('email')
    password = request.form.get('password')
    remember = True if request.form.get('remember') else False

    # Search user in the db
    user = User.query.filter_by(email=email).first()

    # Check if user actually exists
    # Get the input password, hash it, and compare it to the hashed password stored in database
    if not user or not check_password_hash(user.password, password):
        # If user doesn't exist or password is wrong, reload the page
        flash('Please check your login details and try again.') # Message 
        return redirect(url_for('auth.login')) 

    # The user has the right credentials so do the login
    login_user(user, remember=remember)

    # Redirect to user profile
    return redirect(url_for('main.profile'))

@auth.route('/login', methods=['GET', 'POST'])
def login():
    ''' Login method

        Returns:
            Login form page or profile page depending on login data
    '''
    # POST method -> do the login
    if request.method == 'POST':
        return do_login(request)
    # GET method -> show login form
    else:
        return render_template('login.html')

# -------------- Logout -------------- #

@auth.route('/logout')
@login_required
def logout():
    ''' Logout method

        Returns:
            Redirect to web server main page
    '''
    logout_user()
    return redirect(url_for('main.index'))