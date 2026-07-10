"""
Avoid Dict Keys Iteration
=========================
Checking membership in dictionary keys using `.keys()` is slower and redundant in Python.
Use `key in dict` directly, which leverages O(1) average-time dictionary lookup.
"""

# Compliant
if "key" in my_dict:
    pass

if "key" not in my_dict:
    pass

# Non-compliant
if "key" in my_dict.keys():  # Noncompliant {{Avoid calling '.keys()' when checking for key membership in a dictionary. Use 'key in dict' directly.}}
    pass

if "key" not in my_dict.keys():  # Noncompliant {{Avoid calling '.keys()' when checking for key membership in a dictionary. Use 'key in dict' directly.}}
    pass
