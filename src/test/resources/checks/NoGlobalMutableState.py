"""
No Global Mutable State
=======================
Declaring mutable state (like list, dict, set, or custom objects) at the global/module
level is unsafe in concurrent execution contexts and can cause race conditions.
Use immutable data structures, function local variables, or context-scoped variable containers.
"""
import logging
import contextvars

logger = logging.getLogger(__name__)

# Compliant cases
MAX_RETRIES = 3
API_URL = "https://api.example.com"
IS_DEBUG = True
TIMEOUT = None
CONFIG_TUPLE = (1, 2, "three")

class Helper:
    def __init__(self):
        self.local_var = []

def process():
    local_list = []
    local_dict = {}

# Non-compliant cases
global_list = [] # Noncompliant {{Avoid declaring mutable global state at the module level. Use immutable structures or local/thread-safe scopes instead.}}
global_dict = {} # Noncompliant {{Avoid declaring mutable global state at the module level. Use immutable structures or local/thread-safe scopes instead.}}
global_set = set() # Noncompliant {{Avoid declaring mutable global state at the module level. Use immutable structures or local/thread-safe scopes instead.}}

class DatabaseConnection:
    pass

db_instance = DatabaseConnection() # Noncompliant {{Avoid declaring mutable global state at the module level. Use immutable structures or local/thread-safe scopes instead.}}

counter = 0

def increment():
    global counter # Noncompliant {{Avoid using the 'global' keyword. Global mutable state is not thread-safe.}}
    counter += 1

# Edge cases / Gaps verification
global_x = global_y = [] # Noncompliant {{Avoid declaring mutable global state at the module level. Use immutable structures or local/thread-safe scopes instead.}}

# Former gaps — now detected by the rule
annotated_list: list = [] # Noncompliant {{Avoid declaring mutable global state at the module level. Use immutable structures or local/thread-safe scopes instead.}}
tuple_a, tuple_b = [], {} # Noncompliant {{Avoid declaring mutable global state at the module level. Use immutable structures or local/thread-safe scopes instead.}}
class my_class:
    pass
global_inst = my_class() # Noncompliant {{Avoid declaring mutable global state at the module level. Use immutable structures or local/thread-safe scopes instead.}}
