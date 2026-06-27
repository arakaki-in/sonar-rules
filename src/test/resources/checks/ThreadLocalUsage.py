import threading
from threading import local
import contextvars

# Compliant cases
request_ctx = contextvars.ContextVar("request_ctx", default=None)

# Non-compliant cases
my_local = threading.local() # Noncompliant {{Use 'contextvars.ContextVar' instead of 'threading.local' to ensure async-safe and thread-safe context management.}}
my_local2 = local() # Noncompliant {{Use 'contextvars.ContextVar' instead of 'threading.local' to ensure async-safe and thread-safe context management.}}

# Gaps: The following subclass of threading.local is instantiated but NOT flagged by the rule
class CustomLocal(threading.local):
    pass
custom_local = CustomLocal()

