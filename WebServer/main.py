from flask import Blueprint, render_template, request
from flask_login import login_required, current_user

# Main blueprint
main = Blueprint('main', __name__)

@main.route('/')
def index():
    ''' Main page. 
    
        Returns:
            Main web server page
    '''
    return render_template('index.html')

@main.route('/profile')
@login_required
def profile():
    ''' Profile page. 
    
        Returns:
            User profile page
    '''
    return render_template('profile.html', name=current_user.name)
