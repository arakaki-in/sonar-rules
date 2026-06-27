# Project: SonarQube Custom Python Rules CI/CD Upgrades

## Architecture
- Language/Framework: Java 21, Maven, SonarQube Python Analyzer API.
- Code Layout:
  - Custom rules in `com.arakakiin.sonar.python.checks`.
  - Workflow files in `.github/workflows/`.
  - Plugins and profiles in `pom.xml`.

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | Exploration & Initial Verification | Run initial Maven build, verify existing tests. | none | DONE |
| 2 | Java Code Formatting (Spotless) | Configure spotless-maven-plugin (Google Java Format) in pom.xml, format codebase, verify. | M1 | DONE |
| 3 | Coverage & Security Integration | Configure jacoco-maven-plugin and OWASP dependency-check-maven plugin in pom.xml, verify locally. | M2 | DONE |
| 4 | GitHub Actions Workflows | Create release.yml (build matrix, spotless verify, Codecov, GPG signing) and codeql.yml workflows. | M3 | DONE |
| 5 | E2E & Final Verification | Run final spotless checks, tests, security audit, validate workflows, and update AGENTS.md with new completion rules. | M4 | DONE |

## Interface Contracts
- CI Build Step: `mvn spotless:check` must pass.
- Test Coverage: JaCoCo reports outputted to `target/site/jacoco/jacoco.xml`.
- Security Audit: `mvn verify -Psecurity-audit` must run OWASP dependency check.
- Workflows: GitHub Actions files must be syntactically valid YAML.
