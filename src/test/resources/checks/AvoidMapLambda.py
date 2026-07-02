# Compliant
squares = [x**2 for x in numbers]
evens = (x for x in numbers if x % 2 == 0)
mapped_func = map(my_func, numbers)

# Non-compliant
squares_map = map(lambda x: x**2, numbers)  # Noncompliant {{Avoid using 'map(lambda ...)' or 'filter(lambda ...)'. Use list comprehensions or generator expressions for better readability and performance.}}
evens_filter = filter(lambda x: x % 2 == 0, numbers)  # Noncompliant {{Avoid using 'map(lambda ...)' or 'filter(lambda ...)'. Use list comprehensions or generator expressions for better readability and performance.}}
