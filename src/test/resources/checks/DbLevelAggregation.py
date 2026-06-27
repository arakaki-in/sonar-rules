# Compliant cases
def count_users_compliant(session):
    return session.query(func.count(User.id)).scalar()

# Non-compliant cases
def count_users_noncompliant(session):
    users = session.query(User).all()
    count1 = len(users)
    count2 = len(session.query(User).all())  # Noncompliant {{Avoid database-to-memory aggregation. Perform aggregation at the database level using SQL aggregate functions (e.g. COUNT, SUM, AVG) instead of loading all records into Python.}}

    total_age1 = sum([u.age for u in session.query(User).all()])  # Noncompliant {{Avoid database-to-memory aggregation. Perform aggregation at the database level using SQL aggregate functions (e.g. COUNT, SUM, AVG) instead of loading all records into Python.}}
    total_age2 = sum(u.age for u in session.query(User).all())  # Noncompliant {{Avoid database-to-memory aggregation. Perform aggregation at the database level using SQL aggregate functions (e.g. COUNT, SUM, AVG) instead of loading all records into Python.}}
