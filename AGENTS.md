# Project Customizations for `sonar-rules-arakakiin`

These rules apply to any agent working on this SonarQube Custom Python Rules repository.

## Development Guidelines

- **Language & Frameworks**: This project uses Java 21, Maven, and the SonarQube Python Analyzer API.
- **Rule Implementation**:
  - Extend the standard `PythonCheck` (or `PythonVisitorCheck` / `PythonSubscriptionCheck`) provided by the `org.sonarsource.python` API.
  - Make sure to define the rule metadata appropriately using annotations (`@Rule(key = "...")`) and place them in the correct repository.
  - When you create a new rule, do not forget to register it in the main Rule Repository class (e.g., `CustomPythonRuleRepository`).
- **Testing**:
  - **Unit Tests**: Use JUnit 5. For testing rule logic against sample Python files, use the `PythonCheckVerifier` test harness. 
  - **Integration Tests**: Integration tests use the `Sonar Orchestrator` to spin up a SonarQube instance. Put integration tests in `src/test/java` but ensure they are correctly grouped or executed specifically if they require full Orchestrator execution.
- **Code Quality**:
  - Keep the plugin lightweight. Avoid introducing unnecessary external dependencies in `pom.xml`.
  - Maintain the existing MIT License headers at the top of new source files.

## Useful Maven Commands

- Compile and package: `mvn clean package`
- Run unit tests: `mvn test`
- Run integration tests (with Sonar Orchestrator): `mvn clean verify -Pintegration-tests`

## Custom Python Ruleset (arakakiin Engine)

We enforce a strict 26-rule performance, safety, and sustainability ruleset. To prevent duplicate implementation work, we map a subset against native SonarQube Python analyzer rules:

### Natively Supported (Do Not Re-Implement)
1. **Rule 1.2: No Mutable Default Arguments** (Natively covered by `python:S5712`)
2. **Rule 5.1: Lazy Log Formatting** (Natively covered by `python:S2637` / `python:S3457` / `python:S8554`)
3. **Rule 5.3: Bare Excepts Forbidden** (Natively covered by `python:S5754`)

### Custom Rules Requiring Implementation
- **Category 1 (Concurrency):** No Global Mutable State (1.1), ThreadLocal vs ContextVar (1.3), Immutable Data Transfer (1.4)
- **Category 2 (Resources):** Enforce Connection Pooling (2.1), Context Managers Mandatory (2.2 - already in repository as `AvoidFileOpenWithoutWithCheck`), Mandatory Timeouts (2.3)
- **Category 3 (Database):** Zero N+1 Queries (3.1), Avoid `SELECT *` on Heavy Tables (3.2), Batch Operations Required (3.3), DB-Level Aggregation (3.4)
- **Category 4 (CPU & Memory):** Avoid Eager Regex Compilation (4.1), Generators Over Lists for Large Data (4.2), Efficient String Concatenation (4.3), Use `__slots__` for High-Volume Objects (4.4), Fast JSON Parsing (4.5)
- **Category 5 (Logging & Errors):** Avoid Try/Except for Control Flow (5.2)

## Merge Readiness & Verification

Any future contribution, pull request, or task is only considered successfully completed and ready for delivery/merge once all of the following checks pass successfully:
1. **Code Formatting (Spotless)**: Run `mvn spotless:check` to ensure the codebase strictly adheres to the configured Google Java Format style.
2. **Test Coverage (JaCoCo)**: Run `mvn clean test` to ensure that all unit tests execute and pass sequentially, and the coverage report is successfully generated at `target/site/jacoco/jacoco.xml`.
3. **Security Audit (OWASP Dependency Check)**: Ensure that dependency scans are executed and pass successfully to verify there are no known vulnerabilities.

## Refactoring Workflow & Team Structure

Major refactoring tasks follow a multi-agent team structure:

- **Lead Agent** — Coordinates the effort, delegates work to specialized sub-agents, and integrates deliverables.
- **Researcher** — Analyzes existing codebase, identifies patterns, and proposes design specifications (e.g., severity framework based on Green Software Foundation SCI spec, ecoCode/creedengo precedent).
- **Config Engineer** — Updates build configuration files (`pom.xml`, CI/CD workflows, `PythonBenchmarksTest.java`).
- **Java SDET** — Implements Java test and rule changes (check classes, test classes, JSON metadata, `RulesList` registration).
- **Tech Writer** — Updates documentation files (`AGENTS.md`, `CLAUDE.md`, `README.md`) to reflect all changes.

## Design Principles

### CO2-Based Severity Framework

Rule severities are assigned according to estimated carbon impact per occurrence, following the Green Software Foundation SCI specification (ISO 21031:2024) and precedents from ecoCode/creedengo:

| Severity | Carbon Impact | Rules |
|----------|---------------|-------|
| BLOCKER | Very high, systemic | 0 |
| CRITICAL | High, significant carbon savings | 7 |
| MAJOR | Moderate, meaningful savings | 8 |
| MINOR | Low, incremental improvement | 8 |
| INFO | Negligible, best practice | 3 |

Three rules were upgraded and six were downgraded during refactoring based on refined carbon impact analysis.

### Multi-Version Testing Strategy

All repository and plugin tests (`CustomPythonRuleRepositoryTest`, `CustomPythonRulesPluginTest`) use JUnit 5 `@ParameterizedTest` with a `@MethodSource` supplying three SonarQube versions:

- **9.9** (minimum supported — 9.9 LTS)
- **10.8** (intermediate — 10.x LTS)
- **26.2** (build target — Community Build)

This ensures compatibility across the supported SonarQube range without duplicating test methods.

### Benchmark Strategy

Python performance benchmarks run against a single Python version (3.15) via `uv run --python 3.15 pytest python_benchmarks/`. Benchmarks are invoked from `PythonBenchmarksTest.java` (tagged `@Tag("benchmark")`) and use retry logic for flaky `uv venv` creation. Previously, benchmarks ran across a matrix of Python 3.10–3.13; single-version execution reduces CI time while maintaining coverage of the primary target runtime.

