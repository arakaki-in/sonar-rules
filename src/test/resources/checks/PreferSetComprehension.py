items = [1, 2, 3]

# Compliant cases
def test_compliant():
    result = {x for x in items}
    result2 = dict(a=1, b=2)
    result3 = set(items)
    result4 = tuple(x for x in items)

# Non-compliant cases
def test_noncompliant():
    result = set([x for x in items])  # Noncompliant {{Use a set comprehension ('{...}') or dict comprehension ('{k: v ...}') instead of passing a list comprehension to 'set()' or 'dict()'. This avoids creating an unnecessary intermediate list.}}
    result2 = dict([(k, v) for k, v in items])  # Noncompliant
    result3 = tuple([x for x in items])  # Noncompliant
