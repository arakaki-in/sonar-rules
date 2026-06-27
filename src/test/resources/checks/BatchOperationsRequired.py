# Compliant cases
def add_users_compliant(session, users):
    session.add_all(users)

def delete_users_compliant(session, users):
    session.delete_all(users)

# Non-compliant cases
def add_users_noncompliant(session, users):
    for user in users:
        session.add(user)  # Noncompliant {{Use batch operations (like 'session.add_all()', bulk inserts, or 'cursor.executemany()') instead of single-row database operations inside a loop.}}

    for user in users:
        session.delete(user)  # Noncompliant {{Use batch operations (like 'session.add_all()', bulk inserts, or 'cursor.executemany()') instead of single-row database operations inside a loop.}}

    for user in users:
        cursor.execute("INSERT INTO users VALUES (1)")  # Noncompliant {{Use batch operations (like 'session.add_all()', bulk inserts, or 'cursor.executemany()') instead of single-row database operations inside a loop.}}
