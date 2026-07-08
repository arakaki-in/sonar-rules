# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Compile and run unit tests
mvn clean test

# Run a single Java test class
mvn test -Dtest=YourCheckTest -pl .

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

# Run Python benchmarks directly (bypassing Maven)
uv run pytest python_benchmarks/ -v

# Run Python benchmarks for a specific Python version
uv run --python 3.12 pytest python_benchmarks/ -v

# Build the plugin JAR without tests
mvn clean package -DskipTests
```

The plugin JAR is output at `target/arakakiin-rules-plugin-<version>.jar`.

## Architecture

### Plugin Registration Chain

This is a SonarQube plugin for Python static analysis, implemented in Java and packaged as a `sonar-plugin`. The registration chain:

1. **`CustomPythonRulesPlugin`** — Entry point. Implements `org.sonar.api.Plugin`. Registers `CustomPythonRuleRepository` as an extension.

2. **`CustomPythonRuleRepository`** — Implements both `RulesDefinition` (for rule metadata/UI) and `PythonCustomRuleRepository` (for analysis engine). Using `RuleMetadataLoader`, it loads HTML descriptions from the classpath at `/org/sonar/l10n/python/rules/python/` and discovers rules via the `@Rule` annotation on check classes. Repository key: `arakakiin-rules`.

3. **`RulesList`** — Central catalog. Two separate lists: `getPythonChecks()` for main-code rules (19 rules) and `getPythonTestChecks()` for test-code-only rules (currently empty). Adding a new rule requires adding it here.

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

Python performance benchmarks live under `python_benchmarks/` and are managed with `uv` (not pip). Dependencies are pinned in `python_benchmarks/pyproject.toml` (requires-python `>=3.10,<3.14`).

The bridge between Maven and Python: `PythonBenchmarksTest.java` (tagged `@Tag("benchmark")`) runs `uv run --python <version> pytest python_benchmarks/` as a subprocess for each available Python version (3.10–3.13). It uses `pb.inheritIO()` to forward pytest output directly. Benchmarks prove performance properties (e.g., `deque` O(1) insert-left is faster than list O(n) insert-at-0), not rule logic.

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

## Adding a New Rule

1. Create the check class in `src/main/java/com/arakakiin/sonar/python/checks/` following the pattern above
2. Create the HTML description in `src/main/resources/org/sonar/l10n/python/rules/python/<RuleKey>.html`
3. Register the class in `RulesList.getPythonChecks()` (or `getPythonTestChecks()` for test-only rules)
4. Create a test class in `src/test/java/com/arakakiin/sonar/python/checks/` using `PythonCheckVerifier`

## Agent skills

### Issue tracker

GitHub Issues for arakaki-in/sonar-rules. PRs are not a triage surface. See `docs/agents/issue-tracker.md`.

### Triage labels

Default vocabulary: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context — `CONTEXT.md` + `docs/adr/` at the repo root. See `docs/agents/domain.md`.
