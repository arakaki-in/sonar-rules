# Compliant cases
def build_str_compliant(lines):
    parts = []
    for line in lines:
        parts.append(line)
    return "".join(parts)

# Non-compliant cases
def build_str_noncompliant(lines):
    result_str = ""
    for line in lines:
        result_str += line  # Noncompliant {{Avoid repeated string concatenation using '+' or '+=' inside a loop. Use list appending and ''.join() for efficient string construction.}}

    msg_text = ""
    for line in lines:
        msg_text = msg_text + line  # Noncompliant {{Avoid repeated string concatenation using '+' or '+=' inside a loop. Use list appending and ''.join() for efficient string construction.}}
