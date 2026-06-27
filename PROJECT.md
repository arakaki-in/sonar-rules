# Project: SonarQube Custom Rules Refactoring & Open-Source Preparation

## Architecture
- Custom rules plugin for SonarQube.
- Plugin loader: `CustomPythonRulesPlugin` (extends `org.sonar.api.Plugin`).
- Rule registration: `RulesList` and `CustomPythonRuleRepository`.
- Check classes: extend `PythonCheck` (or `PythonVisitorCheck` / `PythonSubscriptionCheck`).
- Test suite: unit tests using `PythonCheckVerifier` and integration tests using `Sonar Orchestrator` to verify rule activations and issue counts.

## Code Layout
- Root POM: `/pom.xml`
- Java Code: `/src/main/java/com/arakakiin/sonar/python/`
  - Plugin Class: `/src/main/java/com/arakakiin/sonar/python/CustomPythonRulesPlugin.java`
  - Repository Class: `/src/main/java/com/arakakiin/sonar/python/CustomPythonRuleRepository.java`
  - Check Registration: `/src/main/java/com/arakakiin/sonar/python/RulesList.java`
  - Rules: `/src/main/java/com/arakakiin/sonar/python/checks/`
- Resources (HTML & JSON metadata): `/src/main/resources/org/sonar/l10n/python/rules/python/`
- Unit Tests (Java): `/src/test/java/org/sonar/samples/python/checks/` -> to be migrated to `/src/test/java/com/arakakiin/sonar/python/checks/`
- Test Python files (unit): `/src/test/resources/checks/`
- Integration Tests (Java): `/src/test/java/com/arakakiin/sonar/CustomRulesIntegrationTest.java` -> to be migrated to `/src/test/java/com/arakakiin/sonar/python/CustomRulesIntegrationTest.java`

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | Maven & Repository Config | Change maven properties in pom.xml, modify repository key & name in repository class | none | PLANNED |
| 2 | Package Migration | Move all Java files to `com.arakakiin.sonar.python` and update imports/packages | M1 | PLANNED |
| 3 | Template Removal | Delete `CustomPythonSubscriptionCheck`, `CustomPythonVisitorCheck` and their associated tests, resource files, and registration | M2 | PLANNED |
| 4 | Test Consolidation | Delete `src/test/resources/sample-python-project/` and adapt integration test to run checks directly on `src/test/resources/checks/` | M3 | PLANNED |
| 5 | Validation & Integrity Audit | Execute build and full test suite (`mvn clean test`, `mvn clean verify -Pintegration-tests`), run Forensic Auditor, verify layout | M4 | PLANNED |

## Interface Contracts
- **RulesList ↔ CustomPythonRuleRepository**: `RulesList.getChecks()` returns the list of all registered custom rule classes to be defined in the repository.
- **CustomRulesIntegrationTest ↔ SonarQube Scanner API**: Programmatically points the SonarScanner to `src/test/resources/checks` using project key `sample-python-project`.
