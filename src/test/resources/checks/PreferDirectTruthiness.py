# Compliant cases
def test_compliant():
    items = [1, 2, 3]
    if items:
        process(items)

    if not items:
        return None

    count = 5
    if count > 0:
        process(count)

    if len(items) > 5:
        process(items)

# Non-compliant cases
def test_noncompliant():
    if len(items) > 0:  # Noncompliant {{Use the truthiness of the collection directly (e.g. 'if seq:' or 'if not seq:') instead of comparing 'len(seq) > 0' or 'len(seq) == 0'.}}
        process(items)

    if len(items) == 0:  # Noncompliant {{Use the truthiness of the collection directly (e.g. 'if seq:' or 'if not seq:') instead of comparing 'len(seq) > 0' or 'len(seq) == 0'.}}
        return None

    if len(items) != 0:  # Noncompliant
        process(items)
