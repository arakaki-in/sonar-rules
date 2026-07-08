"""
Batch Operations Required
=========================
Performing single-row database operations (like `session.add()` or `cursor.execute()`)
inside a loop causes massive database roundtrip overhead. Use batch methods like
`session.add_all()` or `cursor.executemany()` instead.
"""

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
