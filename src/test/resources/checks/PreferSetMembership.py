"""
Prefer Set Membership
=====================
Testing membership (`in`) inside a loop using lists or tuples takes O(N) linear time per search.
Use a `set` for O(1) average constant-time lookup performance.
"""

# Compliant cases
items = {1, 2, 3, 4, 5}
for x in range(10):
    if x in items:
        pass

# Non-compliant cases
for x in range(10):
    if x in [1, 2, 3, 4, 5]:  # Noncompliant {{Use a 'set' instead of a list or tuple for membership testing ('in') inside a loop. Lists and tuples have O(n) lookup; sets have O(1) average lookup.}}
        pass

for x in range(10):
    if x in (1, 2, 3):  # Noncompliant {{Use a 'set' instead of a list or tuple for membership testing ('in') inside a loop. Lists and tuples have O(n) lookup; sets have O(1) average lookup.}}
        pass

for x in range(10):
    if x in [v for v in range(100)]:  # Noncompliant {{Use a 'set' instead of a list or tuple for membership testing ('in') inside a loop. Lists and tuples have O(n) lookup; sets have O(1) average lookup.}}
        pass
