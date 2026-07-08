"""
Mandatory Timeouts
==================
All network request methods (like `requests.get()` or `urlopen()`) should specify an
explicit, reasonable timeout limit. Without a timeout, calls can hang indefinitely,
blocking runtime resources.
"""
import requests
from urllib.request import urlopen

session = requests.Session()

# Compliant cases
requests.get("https://example.com", timeout=5)
session.post("https://example.com/api", data={}, timeout=10)
urlopen("https://example.com", timeout=3.0)

# Non-compliant cases
requests.get("https://example.com") # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}
session.post("https://example.com/api", data={}) # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}
requests.put("https://example.com/api", timeout=None) # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}
urlopen("https://example.com") # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}

# Edge cases / Gaps verification

# False Positive: Using **kwargs is flagged as missing timeout
kwargs = {"timeout": 5}
requests.get("https://example.com", **kwargs) # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}

# False Positive: Positional timeout argument is flagged as missing timeout
urlopen("https://example.com", None, 5.0) # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}

# False Negative / Gap: A variable assigned to None bypasses the timeout check
t = None
requests.get("https://example.com", timeout=t)
