from flask_login import UserMixin
from . import db

class User(UserMixin, db.Model):
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
        return any(elem in [role.name for role in self.roles] for elem in args[0])

    def get_roles(self):
        return self.roles

# Define the Role data-model
class Role(db.Model):
    __tablename__ = 'roles'
    id = db.Column(db.Integer(), primary_key=True)
    name = db.Column(db.String(50), unique=True)

# Define the UserRoles association table
class UserRoles(db.Model):
    __tablename__ = 'user_roles'
    id = db.Column(db.Integer(), primary_key=True)
    user_id = db.Column(db.Integer(), db.ForeignKey('users.id', ondelete='CASCADE'))
    role_id = db.Column(db.Integer(), db.ForeignKey('roles.id', ondelete='CASCADE'))
