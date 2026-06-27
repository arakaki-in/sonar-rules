# Compliant cases
def get_users_compliant(session, user_ids):
    users = session.query(User).filter(User.id.in_(user_ids)).all()
    for user in users:
        print(user.name)

# Non-compliant cases
def get_users_noncompliant(session, user_ids):
    for user_id in user_ids:
        user = session.query(User).filter(User.id == user_id).first()  # Noncompliant {{Avoid executing database queries inside a loop (N+1 query problem). Use join fetching or prefetching instead.}}
        print(user.name)

    i = 0
    while i < len(user_ids):
        user = session.query(User).filter(User.id == user_ids[i]).first()  # Noncompliant {{Avoid executing database queries inside a loop (N+1 query problem). Use join fetching or prefetching instead.}}
        print(user.name)
        i += 1

    # In list comprehension
    [session.query(User).get(uid) for uid in user_ids]  # Noncompliant {{Avoid executing database queries inside a loop (N+1 query problem). Use join fetching or prefetching instead.}}
