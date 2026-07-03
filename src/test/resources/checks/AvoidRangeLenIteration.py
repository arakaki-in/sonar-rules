# Compliant cases
items = [1, 2, 3]
for item in items:
    pass

for i, item in enumerate(items):
    pass

for a, b in zip(items, items):
    pass

for i in range(5):
    process(i)

# Non-compliant cases
for i in range(len(items)):  # Noncompliant {{Avoid 'for i in range(len(seq))'. Use 'for item in seq', 'enumerate(seq)', or 'zip(seq1, seq2)' for better readability and performance.}}
    process(items[i])

