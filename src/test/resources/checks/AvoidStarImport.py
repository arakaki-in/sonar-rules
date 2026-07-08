"""
Avoid Star Import
=================
Wildcard imports (`from module import *`) pollute the global namespace, make debugging
difficult, and can lead to silent name conflicts. Always import explicitly.
"""

# Compliant
import math
from math import sin, cos

# Non-compliant
from math import *  # Noncompliant {{Avoid wildcard imports ('from module import *'). Explicitly import required names to avoid namespace pollution.}}
