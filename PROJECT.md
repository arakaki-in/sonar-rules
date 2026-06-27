# Project: SonarQube Custom Python Rules Open-Sourcing

## Architecture
- Language/Framework: Java 21, Maven, SonarQube Python Analyzer API.
- Source Code layout:
  - Custom rules located in Java package `com.arakakiin.sonar.python` (migrated from `org.sonar.samples.python`).
  - Unit tests verify checks using `PythonCheckVerifier` harness.
  - Integration tests use Sonar Orchestrator to spin up SonarQube and run analysis.

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | Exploration & Initial Verification | Run initial Maven build & check codebase layout | none | DONE |
| 2 | Maven & Packaging Refactoring | Update pom.xml and move all Java files to com.arakakiin.sonar.python | M1 | IN_PROGRESS (87ec0160-78d8-4934-806f-7c2b57d0403e) |
| 3 | Rule Repository Keys & Metadata | Change REPOSITORY_KEY to arakakiin-rules and name to Arakakiin Custom Rules | M2 | PLANNED |
| 4 | Clean Placeholder Checks | Delete CustomPythonSubscriptionCheck / CustomPythonVisitorCheck and their resource/test files | M2 | PLANNED |
| 5 | Test Resources Consolidation & Scanner Setup | Delete sample project, move all test files to checks/, program scanner in integration test | M2, M3, M4 | PLANNED |
| 6 | E2E & Final Verification | Run full suite of unit and integration tests | M5 | PLANNED |

## Interface Contracts
- Rules registration: `RulesList.java` provides checks list to `CustomPythonRuleRepository.java` which is registered in `CustomPythonRulesPlugin.java`.
