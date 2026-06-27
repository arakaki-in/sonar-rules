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
