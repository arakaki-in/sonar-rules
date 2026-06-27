# Compliant cases
val1 = my_dict.get("key", "default")
val2 = getattr(obj, "attr", "default")

# Non-compliant cases
try:  # Noncompliant {{Avoid using try-except blocks for standard control flow (e.g. KeyError, AttributeError, IndexError). Use explicit checks like 'dict.get()', 'hasattr()', or index length validation instead.}}
    val3 = my_dict["key"]
except KeyError:
    val3 = "default"

try:  # Noncompliant {{Avoid using try-except blocks for standard control flow (e.g. KeyError, AttributeError, IndexError). Use explicit checks like 'dict.get()', 'hasattr()', or index length validation instead.}}
    val4 = obj.attr
except AttributeError:
    val4 = "default"

try:  # Noncompliant {{Avoid using try-except blocks for standard control flow (e.g. KeyError, AttributeError, IndexError). Use explicit checks like 'dict.get()', 'hasattr()', or index length validation instead.}}
    val5 = my_list[idx]
except IndexError:
    val5 = "default"
