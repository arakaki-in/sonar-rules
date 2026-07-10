# Sonar Rules Arakakiin

## Language

**Custom Rule**:
A custom static analysis rule implemented in Java for Python/IPython code using SonarQube's Python analyzer API.
_Avoid_: check

**Arakakiin Engine**:
The custom ruleset engine containing the python rules.
_Avoid_: Ruleset, plugin backend

**Rule Metadata**:
The HTML description and JSON configuration files defining rule properties.

**Version Bumping**:
Automated calculation and incrementing of the release version based on Conventional Commits since the last release tag.
_Avoid_: Manual bumping, snapshot release
