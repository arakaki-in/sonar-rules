# Compliant cases
def test_compliant():
    seq = [1, 2, 3]
    for item in reversed(seq):
        process(item)

    r = list(reversed(seq))
    assert r

    # slice with non-reverse step is fine
    for item in seq[::2]:
        process(item)


# Non-compliant cases
def test_noncompliant():
    for item in seq[::-1]:  # Noncompliant {{Use 'reversed(seq)' instead of 'seq[::-1]' for reverse iteration. reversed() returns an iterator without creating a copy of the sequence.}}
        process(item)

    copy = seq[::-1]  # Noncompliant
    assert copy
