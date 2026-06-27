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

We enforce a strict 18-rule performance and safety ruleset. To prevent duplicate implementation work, we map these against native SonarQube Python analyzer rules:

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
