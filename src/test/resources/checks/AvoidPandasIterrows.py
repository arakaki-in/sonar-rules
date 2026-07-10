"""
Avoid Pandas iterrows
=====================
Iterating through a Pandas DataFrame using `iterrows()` is slow and inefficient.
Use vectorized operations, `itertuples()`, or `apply()` for significantly better performance.
"""

# Compliant
for row in df.itertuples():
    pass

# Non-compliant
for index, row in df.iterrows():  # Noncompliant {{Avoid using pandas 'iterrows()'. Use vectorized operations, 'itertuples()', or 'apply()' instead for better performance.}}
    pass
