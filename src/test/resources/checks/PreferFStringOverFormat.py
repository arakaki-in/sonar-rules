# Compliant
name = "Alice"
greeting = f"Hello, {name}"
val = 10 % 3

# Non-compliant
greeting_format = "Hello, {}".format(name)  # Noncompliant {{Prefer f-strings over '.format()' or '%' string formatting for readability and performance.}}
greeting_percent = "Hello, %s" % name  # Noncompliant {{Prefer f-strings over '.format()' or '%' string formatting for readability and performance.}}
