from models import User, UserEmail

def get_user(username):
    """
    Get user corresponding to the given username. The username can be either
    mobile number or email.
    """
    # try first as email
    email = UserEmail.objects.filter(email=username)
    if email.count() == 1:
        return email[0].user
    # try as mobile number
    user = User.objects.filter(mobile_no=username).order_by('-created_on')
    if user.count() >= 1:
        return user[0]
    return None


def verify_passowrd(pasword):
    pasword_hash = User.objects.filter(password_hash= pasword);
    
    if pasword_hash.count() == 1:
        return pasword_hash[0].user
    return None