"""
Avoid File Open Without With
============================
Opening files without a context manager (`with` statement) can lead to resource leaks
if the file is not closed properly. Always open files using context managers.
"""
import io

# Compliant cases
with open("file.txt") as f:
    pass

with io.open("file.txt") as f:
    pass

with open("a"), open("b") as (fa, fb):
    pass

# Non-compliant cases
f = open("file.txt") # Noncompliant {{Use a 'with' statement context manager to open files.}}

f2 = io.open("file.txt") # Noncompliant {{Use a 'with' statement context manager to open files.}}

print(open("file.txt").read()) # Noncompliant {{Use a 'with' statement context manager to open files.}}

with my_ctx:
    open("file.txt") # Noncompliant {{Use a 'with' statement context manager to open files.}}

with open("a") as f:
    io.open("b") # Noncompliant {{Use a 'with' statement context manager to open files.}}
