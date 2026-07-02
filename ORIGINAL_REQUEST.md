# Original User Request

## Initial Request — 2026-06-27T01:30:09Z

Implement the custom static analysis rules for Python in the SonarQube custom rules Java plugin repository. Natively supported SonarQube rules must be excluded from custom implementation to avoid duplication. All test resources/examples must use clean, modern Python standards.

Working directory: /home/everton/arakakiin/regras-python/sonar-rules-arakakiin
Integrity mode: development

## Requirements

### R1. Rule Implementation
Implement Java check classes extending `PythonCheck` (or its subtypes `PythonSubscriptionCheck` / `PythonVisitorCheck`) for the 15 custom rules not natively covered by SonarQube:
- **Category 1 (Concurrency):** No Global Mutable State (1.1), ThreadLocal vs ContextVar (1.3), Immutable Data Transfer (1.4).
- **Category 2 (Resources):** Enforce Connection Pooling (2.1), Mandatory Timeouts (2.3). *(Rule 2.2 is already implemented as `AvoidFileOpenWithoutWithCheck`)*.
- **Category 3 (Database):** Zero N+1 Queries (3.1), Avoid `SELECT *` on Heavy Tables (3.2), Batch Operations Required (3.3), DB-Level Aggregation (3.4).
- **Category 4 (CPU & Memory):** Avoid Eager Regex Compilation (4.1), Generators Over Lists for Large Data (4.2), Efficient String Concatenation (4.3), Use `__slots__` for High-Volume Objects (4.4), Fast JSON Parsing (4.5).
- **Category 5 (Logging & Errors):** Avoid Try/Except for Control Flow (5.2).

*Note: Rule 1.2 (Mutable Defaults), Rule 5.1 (Lazy Log Formatting), and Rule 5.3 (Bare Excepts) are natively supported by SonarQube (python:S5712, python:S2637, python:S5754) and must not be implemented.*

### R2. Rule Metadata & Registration
Register all rule classes in RulesList.java. Provide a matching `.json` and `.html` metadata pair for each rule under `src/main/resources/org/sonar/l10n/python/rules/python/`.

### R3. Unit Testing
Create unit tests under `src/test/java/org/sonar/samples/python/checks/` and corresponding Python source files under `src/test/resources/checks/` using the `PythonCheckVerifier` test harness. Ensure tests cover both compliant and non-compliant samples with standard caret-based assertions (`# Noncompliant {{...}}`).

### R4. Modern Python Test Resources
Audit, review, and refactor all existing and new Python test resource examples (e.g. under `src/test/resources/checks/` and `src/test/resources/sample-python-project/`). They must be updated to use clean, modern Python 3.10+ conventions (such as modern type hinting, standard libraries, and robust modular structures) instead of legacy, bloated, or poorly formatted structures.

### R5. Integration Testing
Update CustomRulesIntegrationTest.java to include all custom rule keys. Update the sample Python project in `src/test/resources/sample-python-project/` to contain instances that trigger each rule, and verify that the integration test suite successfully executes via Sonar Orchestrator and reports the expected issue counts.

## Acceptance Criteria

### Compilation & Static Analysis
- [ ] Maven project compiles without errors: `mvn clean compile`

### Test Suite Execution
- [ ] Unit tests pass successfully: `mvn test`
- [ ] Integration tests pass successfully (with Sonar Orchestrator): `mvn clean verify -Pintegration-tests`

## Follow-up — 2026-06-27T13:21:44+02:00

Refactor the SonarQube custom Python rules repository to prepare it for open-sourcing on GitHub under the Arakakiin organization.

Working directory: /home/everton/arakakiin/regras-python/sonar-rules-arakakiin
Integrity mode: development

## Requirements

### R1. Maven Naming Update (pom.xml)
Update project naming properties in `pom.xml`:
- Change `groupId` from `org.sonar.samples.python` to `com.arakakiin.sonar`.
- Change `artifactId` from `sonar-rules-arakakiin` to `arakakiin-rules-plugin`.
- Update the `<pluginClass>` in `sonar-packaging-maven-plugin` configuration to point to `com.arakakiin.sonar.python.CustomPythonRulesPlugin`.

### R2. Java Package Migration
Move all Java files from `org.sonar.samples.python` and `org.sonar.samples.python.checks` to `com.arakakiin.sonar.python` and `com.arakakiin.sonar.python.checks` respectively. Ensure all `package` declarations and `import` statements are updated accordingly.

### R3. Rule Repository Key & Name
- Change `REPOSITORY_KEY` in `CustomPythonRuleRepository.java` to `"arakakiin-rules"`.
- Change `REPOSITORY_NAME` in `CustomPythonRuleRepository.java` to `"Arakakiin Custom Rules"`.
- Update `CustomRulesIntegrationTest.java` rule keys to use the new repository key prefix `arakakiin-rules`.

### R4. Removal of Templated Examples
Delete the template examples `CustomPythonSubscriptionCheck`, `CustomPythonVisitorCheck`, their tests, their python resource files, and their metadata JSON/HTML files under `src/main/resources/org/sonar/l10n/python/rules/python/`. Remove their registration from `RulesList.java`.

### R5. Test Resources Consolidation
Delete `src/test/resources/sample-python-project/` entirely and consolidate all test files inside `src/test/resources/checks/`. Update `CustomRulesIntegrationTest.java` to scan `src/test/resources/checks/` directly and pass scanner parameters programmatically in the test:
```java
SonarScanner.create(new File("src/test/resources/checks"))
    .setProperty("sonar.projectKey", "sample-python-project")
    .setProperty("sonar.projectName", "Sample Python Project")
    .setProperty("sonar.sources", ".")
    .setProperty("sonar.token", token);
```

## Acceptance Criteria

### Compilation & Unit Tests
- [ ] Maven compiles cleanly and all unit tests pass: `mvn clean test` completes successfully.

### Integration Tests
- [ ] Maven integration tests pass: `mvn clean verify -Pintegration-tests` completes successfully.

## Follow-up — 2026-06-27T13:25:10+02:00

Prepare the SonarQube custom Python rules repository for open-sourcing under the com.arakakiin.sonar group and arakakiin-rules-plugin artifact name by refactoring Java packaging, updating maven/repository metadata, removing sample checks, and consolidating tests.

Working directory: /home/everton/arakakiin/regras-python/sonar-rules-arakakiin
Integrity mode: development

## Requirements

### R1. Maven Configuration Updates
Update `pom.xml`:
- Change `groupId` to `com.arakakiin.sonar`.
- Change `artifactId` to `arakakiin-rules-plugin`.
- Update `<pluginClass>` in `sonar-packaging-maven-plugin` configuration to `com.arakakiin.sonar.python.CustomPythonRulesPlugin`.

### R2. Java Packaging and Import Migration
Move all Java source and test files from package `org.sonar.samples.python` and `org.sonar.samples.python.checks` to `com.arakakiin.sonar.python` and `com.arakakiin.sonar.python.checks` respectively. All package declarations, imports, and related references must be updated.

### R3. Rule Repository Key and Name Updates
- In `CustomPythonRuleRepository.java`, change `REPOSITORY_KEY` to `"arakakiin-rules"` and `REPOSITORY_NAME` to `"Arakakiin Custom Rules"`.
- In `CustomRulesIntegrationTest.java`, update the rule key prefix in `RULE_KEYS` to use `"arakakiin-rules"`.

### R4. Removal of Templated/Placeholder Checks and Resource Files
Delete:
- `CustomPythonSubscriptionCheck.java` & `CustomPythonVisitorCheck.java`.
- `CustomPythonSubscriptionCheckTest.java` & `CustomPythonVisitorCheckTest.java`.
- `customPythonSubscriptionCheck.py` & `customPythonVisitorCheck.py` from `src/test/resources/checks/`.
- `subscription.json`, `subscription.html`, `visitor.json`, and `visitor.html` under `src/main/resources/org/sonar/l10n/python/rules/python/`.
- Remove registration from `RulesList.java`.

### R5. Test Resources Consolidation and Programmatic Integration Scan
- Delete `src/test/resources/sample-python-project/` entirely.
- Consolidate all test files inside `src/test/resources/checks/`.
- Update `CustomRulesIntegrationTest.java` to scan `src/test/resources/checks/` directly and pass scanner parameters programmatically in the test using `SonarScanner.create(new File("src/test/resources/checks"))` setting key, name, sources, and token.

## Acceptance Criteria

### Compiling and Package Structure
- [ ] Group ID is `com.arakakiin.sonar` and Artifact ID is `arakakiin-rules-plugin`.
- [ ] No files or directories exist under packages `org.sonar.samples.python`.
- [ ] All Java package declarations and imports point to `com.arakakiin.sonar.python` or subpackages.
- [ ] Deleted templated checks, tests, python resource files, and rules metadata do not exist in the codebase.
- [ ] `RulesList.java` does not reference the deleted checks.

### Repository and Testing
- [ ] `REPOSITORY_KEY` in `CustomPythonRuleRepository.java` is `"arakakiin-rules"`.
- [ ] `REPOSITORY_NAME` in `CustomPythonRuleRepository.java` is `"Arakakiin Custom Rules"`.
- [ ] `CustomRulesIntegrationTest.java` uses `src/test/resources/checks/` and programs the SonarScanner options.
- [ ] `src/test/resources/sample-python-project/` is deleted.
- [ ] `mvn clean test` compiles and all unit tests pass successfully.
- [ ] `mvn clean verify -Pintegration-tests` builds and runs the Sonar Orchestrator integration test successfully.

## Follow-up — 2026-06-27T19:27:36+02:00

Implement enterprise-grade CI/CD pipeline upgrades for the arakakiin-rules-plugin SonarQube plugin project to support formatting verification, dependency checks, SAST scanning, build matrix execution, and signed GPG release artifacts.

Working directory: /home/everton/arakakiin/regras-python/sonar-rules-arakakiin
Integrity mode: development

## Requirements

### R1. Java Code Formatting Linting (Spotless)
* Configure `spotless-maven-plugin` in `pom.xml` using the Google Java Format to enforce standard styling.
* Add a step in the CI build job to verify formatting using `mvn spotless:check`.

### R2. Code Coverage Integration (JaCoCo & Codecov)
* Add `jacoco-maven-plugin` configuration in `pom.xml` to produce XML coverage reports during the `test` phase.
* Add a step in `.github/workflows/release.yml` to upload these reports to Codecov via the `codecov/codecov-action`.

### R3. Dependency Verification & SAST Security Scanning (OWASP & CodeQL)
* Configure the OWASP `dependency-check-maven` plugin under a dedicated `security-audit` profile to check for CVEs in libraries.
* Create a dedicated `.github/workflows/codeql.yml` workflow to run GitHub CodeQL SAST scanning on pull requests and pushes to `main`.

### R4. JDK Build Matrix Testing
* Configure the GitHub Actions build job to test the codebase against a matrix of JDK 21 (LTS) and the latest JDK 22 or 23 on Ubuntu Linux.

### R5. Release Artifact GPG Signing
* Configure GPG artifact signing via a `gpg` step in `.github/workflows/release.yml` when creating releases on git tag (`v*`) pushes.
* Attach signed files (`.asc` and `.jar`) to the created GitHub Release.

## Acceptance Criteria

### Formatting & Linting
- [ ] Running `mvn spotless:check` passes without errors on formatted Java files.
- [ ] Build workflow in `.github/workflows/release.yml` contains a step verifying formatting.

### Coverage & Security
- [ ] JaCoCo XML reports are generated in `target/site/jacoco/jacoco.xml` when running tests.
- [ ] CI workflow uploads coverage reports to Codecov.
- [ ] A dedicated `codeql.yml` workflow file exists in `.github/workflows/` and is valid.
- [ ] Running `mvn verify -Psecurity-audit` executes the OWASP dependency check successfully.

### Build Matrix & Release Signing
- [ ] The build job in `release.yml` successfully executes unit and integration tests across JDK 21 and the latest JDK 22/23.
- [ ] The publish job in `release.yml` decrypts/imports GPG keys and signs the packaged JAR file.
- [ ] The GitHub Release step attaches both the `.jar` and its `.asc` signature.
- [ ] Local build (`mvn clean test`) compiles and unit tests pass successfully.

## Follow-up — 2026-06-27T19:42:50+02:00

* Note: The user has updated `pom.xml` to configure `<autoUpdate>false</autoUpdate>` and `<failOnError>false</failOnError>` on the OWASP dependency-check-maven plugin.
* New Requirement: We need to update the agent harness rules inside `AGENTS.md` (located in the workspace root). The update must state that any future contribution or task is only considered successfully completed and ready for delivery/merge once all the newly integrated checks (Spotless formatting, JaCoCo test coverage, and OWASP dependency checks) pass successfully.

## Follow-up — 2026-07-02T23:24:41+02:00

Configure a Python test and benchmarking setup using `uv`, integrated directly into the Maven build pipeline so it is executed during `mvn test`.

Working directory: /home/everton/arakakiin/regras-python/sonar-rules-arakakiin
Integrity mode: development

## Requirements

### R1. Python Environment and Test Integration via Maven
- Initialize the python project environment in the repository using `uv`. All python libraries (e.g., Pandas, pytest, httpx) must have their versions pinned.
- Implement a JUnit test class in the Java test suite (e.g. `PythonBenchmarksTest.java`) that executes the python test suite by invoking `uv run pytest` as a subprocess.
- Ensure the setup runs against multiple versions of Python (from 3.10 to 3.13) to verify compatibility and measure version-specific performance changes.
- In the custom SonarQube rule description HTML files, add recommendations suggesting to the user if upgrading their Python version will bring performance gains for that specific pattern.
- Ensure that this JUnit test runs during the standard `mvn test` phase, and fails the Maven build if any Python benchmark test fails.
- Follow testing pyramid best practices inside Maven, cleanly separating unit tests, integration tests, and the benchmarking/performance pipeline.

### R2. Library-Specific Performance Verification Benchmarks
- Provide Python pytest benchmarks under `python_benchmarks/` showing clear performance comparisons for the active rules.
- Do not include assertions or tests verifying deleted/nonsense rules.
- Implement tests verifying performance under the following library scenarios:
  1. **FastAPI & HTTP Clients**: Show async non-blocking operations vs. blocking operations (requests vs. httpx/aiohttp) under concurrency.
  2. **SQLAlchemy Queries**: Compare bulk/batch operations or optimized query patterns against sequential row loops or unoptimized ORM lookups.
  3. **Pandas & NumPy Vectorization**: Compare vectorized operations against standard python loops (e.g. `.iterrows()`) on datasets of at least 1,000 rows.
  4. **Standard library collections**: Compare `collections.deque` against list left insert/pop operations.

### R3. Extensible Test Harness
- Structure the test harness in both Java and Python to be highly modular and documented so that developers cloning the repository can easily add or edit rules.

### R4. GitHub Actions CI/CD Pipeline Integration
- Add or configure a GitHub Actions workflow (e.g. `.github/workflows/ci.yml`) to automatically run the entire Maven and Python benchmark test pipeline on every pull request.

## Acceptance Criteria

### Test Execution & Integration
- [ ] Running `mvn test` automatically bootstraps the `uv` environment, runs the Python benchmarks, and reports them as part of the Maven test suite.
- [ ] All 19 active Sonar rules are validated by their corresponding benchmark tests proving their performance/correctness value.
- [ ] Benchmark execution metrics are logged, comparing Python versions where possible.
- [ ] The Java build passes spotless checks and tests compile and run successfully.
- [ ] The GitHub Actions configuration successfully runs and tests both Maven/Java and uv/Python.

## Follow-up — 2026-07-02T21:38:15Z

Configure a Python test and benchmarking setup using `uv`, integrated directly into the Maven build pipeline so it is executed during `mvn test`.

Working directory: /home/everton/arakakiin/regras-python/sonar-rules-arakakiin
Integrity mode: development

> IMPORTANT: Most implementation files already exist from a previous run. Your PRIMARY task is to:
> 1. Audit and fix any issues with existing files
> 2. Run `mvn spotless:apply && mvn clean test` to verify everything passes
> 3. Commit all changes on a new branch `feature/python-benchmarks-uv` and open a GitHub Pull Request
>
> Do NOT regenerate files that already exist and are correct. Check them first.

## What already exists (verify and fix if needed)
- `pyproject.toml` — uv python project with pinned deps (pytest==8.2.2, pandas==2.2.2, sqlalchemy==2.0.31, httpx==0.27.0, fastapi==0.111.0, numpy==1.26.4, aiohttp==3.9.5, requests==2.32.3)
- `uv.lock` — pinned lockfile
- `python_benchmarks/test_deque.py` — deque vs list benchmarks
- `python_benchmarks/test_fastapi.py` — async vs sync HTTP benchmarks
- `python_benchmarks/test_pandas.py` — vectorized vs iterrows benchmarks
- `python_benchmarks/test_sqlalchemy.py` — bulk vs sequential ORM benchmarks
- `src/test/java/com/arakakiin/sonar/python/checks/PythonBenchmarksTest.java` — JUnit class invoking `uv run pytest`
- `.github/workflows/ci.yml` — GitHub Actions pipeline
- Updated HTML rule description files with Python upgrade recommendations

## Requirements

### R1. Python Environment and Test Integration via Maven
- All python libraries must have their versions pinned in `pyproject.toml`.
- The JUnit test class `PythonBenchmarksTest.java` must execute the python test suite by invoking `uv run pytest` as a subprocess.
- The setup must run against multiple versions of Python (from 3.10 to 3.13).
- The SonarQube rule description HTML files must contain recommendations about Python version upgrade performance gains.
- This JUnit test must run during the standard `mvn test` phase and fail the Maven build if any Python benchmark test fails.
- Follow testing pyramid best practices in Maven: unit tests, integration tests, and benchmarking pipeline cleanly separated.

### R2. Library-Specific Performance Verification Benchmarks
- Python pytest benchmarks under `python_benchmarks/` must show clear performance comparisons for the active rules.
- Do NOT include tests for deleted/nonsense rules.
- Cover: FastAPI & HTTP Clients, SQLAlchemy bulk vs sequential, Pandas vectorization vs iterrows (1,000+ rows), collections.deque vs list left insert.

### R3. Extensible Test Harness
- Structure the harness (both Java and Python) to be modular and documented so developers can easily add/edit rules.

### R4. GitHub Actions CI/CD Pipeline
- `.github/workflows/ci.yml` must trigger on pull requests and run the full Maven + Python benchmark pipeline.

## Acceptance Criteria
- [ ] `mvn spotless:check` passes with no formatting violations.
- [ ] `mvn clean test` passes with all Java unit tests AND Python benchmarks green.
- [ ] All active Sonar rules are covered by at least one benchmark test.
- [ ] Changes are committed on branch `feature/python-benchmarks-uv` and a GitHub Pull Request is opened against `main` on `arakaki-in/sonar-rules` with a benchmark summary as the PR description.
- [ ] The GitHub Actions CI config is valid and triggers on pull_request events.
