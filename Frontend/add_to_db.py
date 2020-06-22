import datetime

from .models import User, Role
from werkzeug.security import generate_password_hash

def add_to_db(db):

    # ------- User ------- #

    user = User(
        email = "admin@admin.com",
        password = generate_password_hash("admin", method='sha256'),
        name = "admin",
        # Necessary to redirect to authorized views
        email_confirmed_at=datetime.datetime.utcnow()
    )

    user.roles.append(Role(name='admin'))

    db.session.add(user)

    user = User(
        email = "p_country@gmail.com",
        password = generate_password_hash("admin", method='sha256'),
        name = "Country politician",
        # Necessary to redirect to authorized views
        email_confirmed_at=datetime.datetime.utcnow()
    )
    
    user.roles.append(Role(name='politician_country'))

    db.session.add(user)

    user = User(
        email = "p_admin_area@gmail.com",
        password = generate_password_hash("admin", method='sha256'),
        name = "Admin area politician",
        # Necessary to redirect to authorized views
        email_confirmed_at=datetime.datetime.utcnow()
    )

    user.roles.append(Role(name='politician_admin_area'))

    db.session.add(user)

    user = User(
        email = "p_locality@gmail.com",
        password = generate_password_hash("admin", method='sha256'),
        name = "Locality politician",
        # Necessary to redirect to authorized views
        email_confirmed_at=datetime.datetime.utcnow()
    )

    user.roles.append(Role(name='politician_locality'))

    db.session.add(user)

    user = User(
        email = "police@gmail.com",
        password = generate_password_hash("admin", method='sha256'),
        name = "Police",
        # Necessary to redirect to authorized views
        email_confirmed_at=datetime.datetime.utcnow()
    )

    user.roles.append(Role(name='police'))

    db.session.add(user)

    db.session.commit()