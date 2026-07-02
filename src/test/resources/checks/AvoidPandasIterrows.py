# Compliant
for row in df.itertuples():
    pass

# Non-compliant
for index, row in df.iterrows():  # Noncompliant {{Avoid using pandas 'iterrows()'. Use vectorized operations, 'itertuples()', or 'apply()' instead for better performance.}}
    pass
