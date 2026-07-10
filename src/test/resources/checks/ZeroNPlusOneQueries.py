"""
Zero N+1 Queries
================
Executing database queries inside loops creates the "N+1 queries" performance bottleneck,
sending individual query requests to the database for each loop iteration.
Use batch operations, prefetching, or SQL joins to fetch all required records in a single query.
"""

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
