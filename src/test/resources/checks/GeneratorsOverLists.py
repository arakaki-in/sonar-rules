# Compliant cases
total1 = sum(x for x in range(10))
joined1 = ", ".join(str(x) for x in range(10))

# Non-compliant cases
total2 = sum([x for x in range(10)])  # Noncompliant {{Use generator expressions instead of list comprehensions when passing iterables to functions like 'sum()', 'any()', 'all()', 'min()', 'max()', or 'join()'. This avoids unnecessary memory allocation for list creation.}}
joined2 = ", ".join([str(x) for x in range(10)])  # Noncompliant {{Use generator expressions instead of list comprehensions when passing iterables to functions like 'sum()', 'any()', 'all()', 'min()', 'max()', or 'join()'. This avoids unnecessary memory allocation for list creation.}}
