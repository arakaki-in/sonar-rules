items = [1, 2, 3]

# Compliant cases
result_compliant_set = {x for x in items}
result_compliant_dict = dict(a=1, b=2)
result_compliant_set2 = set(items)
result_compliant_tuple = tuple(x for x in items)

# Non-compliant cases
result_noncompliant_set = set([x for x in items])  # Noncompliant {{Use a set comprehension ('{...}') or dict comprehension ('{k: v ...}') instead of passing a list comprehension to 'set()' or 'dict()'. This avoids creating an unnecessary intermediate list.}}
result_noncompliant_dict = dict([(k, v) for k, v in items])  # Noncompliant
result_noncompliant_tuple = tuple([x for x in items])  # Noncompliant
