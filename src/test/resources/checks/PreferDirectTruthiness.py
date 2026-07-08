"""
Prefer Direct Truthiness
========================
In Python, empty collections (lists, tuples, dicts, strings) are falsy, while populated
ones are truthy. Avoid checking length (`len(seq) > 0` or `len(seq) == 0`) and check
the collection directly (e.g. `if seq:` or `if not seq:`).
"""

# Compliant cases
items = [1, 2, 3]
if items:
    process(items)

if not items:
    pass

count = 5
if count > 0:
    process(count)

if len(items) > 5:
    process(items)

# Non-compliant cases
if len(items) > 0:  # Noncompliant {{Use the truthiness of the collection directly (e.g. 'if seq:' or 'if not seq:') instead of comparing 'len(seq) > 0' or 'len(seq) == 0'.}}
    process(items)

if len(items) == 0:  # Noncompliant {{Use the truthiness of the collection directly (e.g. 'if seq:' or 'if not seq:') instead of comparing 'len(seq) > 0' or 'len(seq) == 0'.}}
    pass

if len(items) != 0:  # Noncompliant
    process(items)
