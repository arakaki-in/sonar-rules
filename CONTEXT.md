# Sonar Rules Arakakiin

SonarQube Custom Python Rules repository enforcing a strict 26-rule performance, safety, and sustainability ruleset.

## Language

**Custom Rule**:
A custom static analysis rule implemented in Java for Python/IPython code using SonarQube's Python analyzer API.
_Avoid_: Plugin rule, check

**Arakakiin Engine**:
The custom ruleset engine containing the 26 performance, safety, and sustainability rules.
_Avoid_: Ruleset, plugin backend

**Rule Metadata**:
The HTML description and JSON configuration files defining rule properties, severity, and tags.

**Python Benchmark**:
Subprocess execution of pytest benchmarks to verify rule-underlined python performance properties.
_Avoid_: Python test, runtime test

**Version Bumping**:
Automated calculation and incrementing of the release version based on Conventional Commits since the last release tag.
_Avoid_: Manual bumping, snapshot release
