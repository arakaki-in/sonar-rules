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
