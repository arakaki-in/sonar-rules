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
urlopen("https://example.com", None, 5.0)  # positional timeout (3rd argument)

# Non-compliant cases
requests.get("https://example.com") # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}
session.post("https://example.com/api", data={}) # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}
requests.put("https://example.com/api", timeout=None) # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}
urlopen("https://example.com") # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}

# Edge cases / Gaps verification

# False Positive: Using **kwargs is flagged as missing timeout (still a gap — needs flow analysis)
kwargs = {"timeout": 5}
requests.get("https://example.com", **kwargs) # Noncompliant {{Configure an explicit timeout for all network requests to avoid hanging indefinitely.}}

# False Negative / Gap: A variable assigned to None bypasses the timeout check (still a gap — needs flow analysis)
t = None
requests.get("https://example.com", timeout=t)
