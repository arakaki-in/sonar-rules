# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Compile and run unit tests
mvn clean test

# Run a single Java test class
mvn test -Dtest=YourCheckTest -pl .

# Run specific repository or plugin registration tests
mvn test -Dtest=CustomPythonRuleRepositoryTest -pl .
mvn test -Dtest=CustomPythonRulesPluginTest -pl .

# Run only benchmark-tagged tests (Python performance benchmarks)
mvn test -Dgroups="benchmark"

# Run integration tests (requires Docker for SonarQube orchestrator)
mvn clean verify -Pintegration-tests

# Format Java code with Spotless (Google Java Format + license header)
mvn spotless:apply

# Check code formatting without applying
mvn spotless:check

# Run security audit (OWASP dependency check)
mvn verify -Psecurity-audit -DskipTests

# Run Python benchmarks (single version, Python 3.15)
uv run --python 3.15 pytest python_benchmarks/ -v

# Build the plugin JAR without tests
mvn clean package -DskipTests
```

The plugin JAR is output at `target/arakakiin-rules-plugin-<version>.jar`.

## Architecture

### Plugin Registration Chain

This is a SonarQube plugin for Python static analysis, implemented in Java and packaged as a `sonar-plugin`. The registration chain:

1. **`CustomPythonRulesPlugin`** — Entry point. Implements `org.sonar.api.Plugin`. Registers `CustomPythonRuleRepository` as an extension.

2. **`CustomPythonRuleRepository`** — Implements both `RulesDefinition` (for rule metadata/UI) and `PythonCustomRuleRepository` (for analysis engine). Using `RuleMetadataLoader`, it loads HTML descriptions from the classpath at `/org/sonar/l10n/python/rules/python/` and discovers rules via the `@Rule` annotation on check classes. Repository key: `arakakiin-rules`.

3. **`RulesList`** — Central catalog. Two separate lists: `getPythonChecks()` for main-code rules (26 rules) and `getPythonTestChecks()` for test-code-only rules (currently empty). Adding a new rule requires adding it here.

### Rule Check Pattern

Every check class follows this pattern:

- Extends `PythonSubscriptionCheck`
- Annotated with `@Rule(key = CheckName.RULE_KEY)`
- Has a public `RULE_KEY` constant matching the HTML description filename
- Registers syntax-node consumers in `initialize()` via `context.registerSyntaxNodeConsumer(Tree.Kind.<NODE_TYPE>, this::checkMethod)`
- Issues are raised via `ctx.addIssue(syntaxNode, MESSAGE)`

Example: `MandatoryTimeoutsCheck` registers on `Tree.Kind.CALL_EXPR`, inspects call expressions for HTTP request patterns (requests library, urllib), checks for a `timeout` argument, and raises if absent.

### Rule Metadata

Each rule has an HTML description file at `src/main/resources/org/sonar/l10n/python/rules/python/<RuleKey>.html`. These are loaded by `RuleMetadataLoader` and displayed in the SonarQube UI. Rule keys must match between: the `@Rule` annotation, the `RULE_KEY` constant, and the HTML filename.

### Python Benchmarks Integration

Python performance benchmarks live under `python_benchmarks/` and are managed with `uv` (not pip). Dependencies are pinned in `pyproject.toml` (root, requires-python `>=3.10,<3.14`).

The bridge between Maven and Python: `PythonBenchmarksTest.java` (tagged `@Tag("benchmark")`) runs `uv run --python 3.15 pytest python_benchmarks/` as a subprocess for a single Python version (defined by `BENCHMARK_PYTHON_VERSION = "3.15"`). It uses `pb.inheritIO()` to forward pytest output directly and includes retry logic for flaky `uv venv` creation. Benchmarks prove performance properties (e.g., `deque` O(1) insert-left is faster than list O(n) insert-at-0), not rule logic.

### Multi-Version Testing

Repository and plugin tests (`CustomPythonRuleRepositoryTest`, `CustomPythonRulesPluginTest`) use JUnit 5 `@ParameterizedTest` with a `@MethodSource` that supplies three SonarQube versions (9.9, 10.8, 26.2) to verify compatibility across the supported SonarQube range.

### Maven Build Profiles

| Profile | Purpose |
|---------|---------|
| `integration-tests` | Activates failsafe for integration tests |
| `security-audit` | Activates OWASP dependency check |
| `release` | Activates source and javadoc JAR generation |

### CI/CD

- **`_build.yml`** — Reusable workflow called by `ci.yml` and `release.yml`. Runs: Spotless check → unit tests → integration tests → security audit (optional) → JaCoCo report artifact upload.
- **`ci.yml`** — Runs on PRs and pushes to `main`. Single build job against latest SonarQube (with benchmarks and security audit). Uploads coverage to Codecov.
- **`release.yml`** — Runs on push to `main` or tag `v*`. Builds with JDK 21 and 23, auto-bumps patch version via `.github/scripts/bump-version.sh` on main pushes, GPG-signs the JAR, creates a GitHub Release.
- **`codeql.yml`** — CodeQL analysis on `main` and PRs.

### Project Requirements

- **Java 21** (JDK 21 required; build enforces via maven-enforcer-plugin)
- **Maven 3.6.3+** (also enforced)
- **SonarQube** target: 9.9 LTS through latest (10.x+)
- Plugin manifest declares support for languages `py` and `ipynb`
- **SonarCloud**: Test fixture Python files under `src/test/resources/checks/` are excluded from analysis via `<sonar.test.exclusions>` in `pom.xml` to avoid false positives on files that intentionally contain rule violations

## Adding a New Rule

1. Create the check class in `src/main/java/com/arakakiin/sonar/python/checks/` following the pattern above
2. Create the HTML description in `src/main/resources/org/sonar/l10n/python/rules/python/<RuleKey>.html`
3. Register the class in `RulesList.getPythonChecks()` (or `getPythonTestChecks()` for test-only rules)
4. Create a test class in `src/test/java/com/arakakiin/sonar/python/checks/` using `PythonCheckVerifier`
5. Create or update the JSON metadata file (`<RuleKey>.json`) in the same directory as the HTML description, matching the severity and tags (WHEN APPLICABLE, Add the `"sustainability"` tag)
