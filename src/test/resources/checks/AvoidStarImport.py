# Compliant
import math
from math import sin, cos

# Non-compliant
from math import *  # Noncompliant {{Avoid wildcard imports ('from module import *'). Explicitly import required names to avoid namespace pollution.}}
