from flask_login import UserMixin
from . import db

class User(UserMixin, db.Model):
    ''' Class representing a web server user

    Attributes
    ----------
    id : int 
        User identificator
    email : str 
        User email
    password : str
        User password
    name : str 
        User name
    location : str 
        User location
    email_confirmed_at : date
        Email confirmation date
    roles: list[str] 
        User roles 
    '''
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(100), unique=True)
    password = db.Column(db.String(20))
    name = db.Column(db.String(100))
    location = db.Column(db.String(50))
    
    # Necessary to Flask user
    email_confirmed_at = db.Column(db.DateTime())

    # Define the relationship to Role via UserRoles
    roles = db.relationship('Role', secondary='user_roles')

    # Necessary to Flask user
    def has_roles(self, *args):
        ''' Check if the user has the roles specified in *args

            Parameters:
            *args (list[str]): list with the roles to check 

            Returns:
            Bool: True if the user has the role, otherwise False
        '''
        return any(elem in [role.name for role in self.roles] for elem in args[0])

    def get_roles(self):
        ''' User roles getter

            Returns: 
            list[str]: User roles
        '''
        return self.roles

class Role(db.Model):
    ''' Class representing a role

    Attributes
    ----------
    id : int 
        Role identificator
    name : str 
        Role name
    '''
    __tablename__ = 'roles'
    id = db.Column(db.Integer(), primary_key=True)
    name = db.Column(db.String(50), unique=True)

# Define the UserRoles association table
class UserRoles(db.Model):
    ''' Class representing the association between a user and a role

    Attributes
    ----------
    id : int 
        UserRole identificator
    user_id : int 
        User identificator
    role_id : int 
        Role identificator
    '''
    __tablename__ = 'user_roles'
    id = db.Column(db.Integer(), primary_key=True)
    user_id = db.Column(db.Integer(), db.ForeignKey('users.id', ondelete='CASCADE'))
    role_id = db.Column(db.Integer(), db.ForeignKey('roles.id', ondelete='CASCADE'))
