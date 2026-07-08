"""
None Comparison Style
=====================
Comparing values to `None` using equality operators (`==` or `!=`) can be bypassed
or overridden by custom class `__eq__` implementations. Always use identity operators
(`is None` or `is not None`) which are faster and guaranteed to be correct.
"""

# Compliant
if x is None:
    pass

if x is not None:
    pass

# Non-compliant
if x == None:  # Noncompliant {{Use 'is None' or 'is not None' for comparisons with None instead of '==' or '!='.}}
    pass

if x != None:  # Noncompliant {{Use 'is None' or 'is not None' for comparisons with None instead of '==' or '!='.}}
    pass

if None == x:  # Noncompliant {{Use 'is None' or 'is not None' for comparisons with None instead of '==' or '!='.}}
    pass

if None != x:  # Noncompliant {{Use 'is None' or 'is not None' for comparisons with None instead of '==' or '!='.}}
    pass
