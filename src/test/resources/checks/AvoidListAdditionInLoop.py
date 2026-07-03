items = [1, 2, 3]
a, b = [1], [2]

# Compliant cases
def test_compliant():
    result = []
    for item in items:
        result.append(item)

    _ = a + b  # not in a loop — not flagged

    for item in items:
        result.extend([item])

    assert result

# Non-compliant cases
def test_noncompliant():
    result = []
    for item in items:
        result = result + [item]  # Noncompliant {{Avoid creating a new list with '+' or '+= [x]' inside a loop. Use 'list.append(item)' or 'list.extend(items)' for O(1) amortized additions. Repeated list addition creates a new list each time, making the loop O(n^2).}}

    for item in items:
        result += [item]  # Noncompliant

    assert result
