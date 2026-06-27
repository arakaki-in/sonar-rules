# Compliant cases
query1 = "SELECT id, name FROM users"
query2 = "select email from users where id = 1"

# Non-compliant cases
query3 = "SELECT * FROM users"  # Noncompliant {{Avoid using 'SELECT *' in SQL queries. Explicitly project only the columns required to minimize network payload and database overhead.}}
query4 = "select * from users"  # Noncompliant {{Avoid using 'SELECT *' in SQL queries. Explicitly project only the columns required to minimize network payload and database overhead.}}
