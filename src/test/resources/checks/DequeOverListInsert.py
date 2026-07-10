"""
Deque Over List Insert
======================
Inserting or popping elements at the beginning of a standard Python `list` (index 0)
takes O(N) time because all subsequent items must be shifted.
Use `collections.deque` instead, which supports O(1) operations from both ends.
"""

# Compliant
my_list = []
my_list.insert(1, "item")
my_list.pop()
my_list.pop(1)

# Non-compliant
my_list.insert(0, "item")  # Noncompliant {{Use 'collections.deque' instead of 'list' for FIFO queue operations or when inserting/popping at index 0.}}
my_list.pop(0)  # Noncompliant {{Use 'collections.deque' instead of 'list' for FIFO queue operations or when inserting/popping at index 0.}}
