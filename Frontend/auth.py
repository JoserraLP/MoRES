from flask import Blueprint, render_template, redirect, url_for, request, flash
from werkzeug.security import generate_password_hash, check_password_hash
from flask_login import login_user, login_required, logout_user
from .models import User
from . import db, mqtt

auth = Blueprint('auth', __name__)

# -------------- Login -------------- #

def do_login(request):
    email = request.form.get('email')
    password = request.form.get('password')
    remember = True if request.form.get('remember') else False

    user = User.query.filter_by(email=email).first()

    # check if user actually exists
    # get the input password, hash it, and compare it to the hashed password stored in database
    if not user or not check_password_hash(user.password, password):
        flash('Please check your login details and try again.') # Message 
        return redirect(url_for('auth.login')) # if user doesn't exist or password is wrong, reload the page

    # The user has the right credentials
    login_user(user, remember=remember)
    return redirect(url_for('main.profile'))

@auth.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        return do_login(request)
    else:
        return render_template('login.html')

# -------------- Logout -------------- #

@auth.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('main.index'))