"""
Prefer Reversed Over Slice
==========================
Using ``seq[::-1]`` creates a full reversed copy of the sequence, allocating O(n)
memory. The built-in ``reversed(seq)`` returns an iterator that traverses the
sequence backwards without copying it, providing O(1) memory overhead.
"""
seq = [1, 2, 3]

# Compliant cases
for item in reversed(seq):
    process(item)

r = list(reversed(seq))
assert r

# Slice with non-reverse step is fine
for item in seq[::2]:
    process(item)

# Slice with bounds but non-reverse step is fine
partial = seq[1:3]

# Non-compliant cases
for item in seq[::-1]:  # Noncompliant {{Use 'reversed(seq)' instead of 'seq[::-1]' for reverse iteration. reversed() returns an iterator without creating a copy of the sequence.}}
    process(item)

copy = seq[::-1]  # Noncompliant {{Use 'reversed(seq)' instead of 'seq[::-1]' for reverse iteration. reversed() returns an iterator without creating a copy of the sequence.}}
