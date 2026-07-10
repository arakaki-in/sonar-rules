"""
Prefer F-String Over Format
===========================
F-strings (`f"Hello, {name}"`) are cleaner, more readable, and faster than calling
`.format()` or using the `%` operator because they are evaluated at runtime by a
specialized opcode.
"""

# Compliant
name = "Alice"
greeting = f"Hello, {name}"
val = 10 % 3

# Non-compliant
greeting_format = "Hello, {}".format(name)  # Noncompliant {{Prefer f-strings over '.format()' or '%' string formatting for readability and performance.}}
greeting_percent = "Hello, %s" % name  # Noncompliant {{Prefer f-strings over '.format()' or '%' string formatting for readability and performance.}}
