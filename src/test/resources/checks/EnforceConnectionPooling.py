import sqlite3
import psycopg2
import requests
from sqlalchemy import create_engine

# Compliant cases
engine = create_engine("postgresql://user:pass@localhost/db")

session = requests.Session()
for url in ["http://a.com", "http://b.com"]:
    session.get(url)

# Non-compliant cases
conn = sqlite3.connect("mydb.db") # Noncompliant {{Enforce connection pooling. Avoid creating raw database connections directly. Use connection pools (e.g. SQLAlchemy engines or database-specific pool managers) instead.}}
conn2 = psycopg2.connect("dbname=test") # Noncompliant {{Enforce connection pooling. Avoid creating raw database connections directly. Use connection pools (e.g. SQLAlchemy engines or database-specific pool managers) instead.}}

urls = ["http://api1.com", "http://api2.com"]
for url in urls:
    requests.get(url) # Noncompliant {{Avoid making raw HTTP requests inside a loop. Reuse TCP connections by using a 'requests.Session()' instance instead.}}

# Gap closed: list comprehensions are now flagged by the rule
responses = [requests.get(url) for url in urls] # Noncompliant {{Avoid making raw HTTP requests inside a loop. Reuse TCP connections by using a 'requests.Session()' instance instead.}}

