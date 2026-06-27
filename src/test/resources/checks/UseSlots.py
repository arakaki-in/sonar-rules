from dataclasses import dataclass

# Compliant cases
@dataclass(slots=True)
class CompliantDTO:
    id: int

class AnotherCompliantDTO:
    __slots__ = ("id",)
    def __init__(self, id):
        self.id = id

# Non-compliant cases
@dataclass
class NonCompliantDTO:  # Noncompliant {{Define '__slots__' for high-volume data classes to reduce memory usage and improve attribute access speed. For dataclasses, use '@dataclass(slots=True)'.}}
    id: int

class UserDTO:  # Noncompliant {{Define '__slots__' for high-volume data classes to reduce memory usage and improve attribute access speed. For dataclasses, use '@dataclass(slots=True)'.}}
    def __init__(self, id):
        self.id = id
