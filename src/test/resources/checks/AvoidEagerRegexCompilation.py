import re

# Compliant cases
result = re.match(r"\d+", "123")

# Non-compliant cases
pattern = re.compile(r"\d+")  # Noncompliant {{Avoid eager compilation of regular expressions using 're.compile()'. Use 're' module functions directly to leverage the built-in lazy compilation and caching, or defer compilation until needed.}}
