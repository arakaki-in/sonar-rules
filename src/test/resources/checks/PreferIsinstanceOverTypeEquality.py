# Compliant cases
if isinstance(x, int):
    process(x)

if isinstance(x, (int, float)):
    process(x)

# Non-compliant cases
if type(x) == int:  # Noncompliant {{Use 'isinstance(x, SomeType)' instead of 'type(x) == SomeType' or 'type(x) is SomeType'. isinstance() supports inheritance and is the Pythonic way to check types.}}
    process(x)

if type(x) is str:  # Noncompliant {{Use 'isinstance(x, SomeType)' instead of 'type(x) == SomeType' or 'type(x) is SomeType'. isinstance() supports inheritance and is the Pythonic way to check types.}}
    process(x)

if str == type(x):  # Noncompliant
    process(x)
