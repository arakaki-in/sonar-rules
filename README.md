# SonarQube Python Custom Rules Plugin (Arakakiin Rules)

[![CI/CD Pipeline](https://github.com/arakaki-in/sonar-rules/actions/workflows/release.yml/badge.svg)](https://github.com/arakaki-in/sonar-rules/actions/workflows/release.yml)
[![MIT License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A highly optimized SonarQube plugin implementing 26 advanced performance, resource management, database safety, concurrency, and sustainability custom rules for Python static analysis.

---

## 🚀 Features & Custom Rules

This plugin registers custom static analysis rules under the repository **Arakakiin Custom Rules** (Key: `arakakiin-rules`). All rules carry the `"sustainability"` tag.

To find the complete catalog, source code, and detailed documentation for the rules:
* **Rule Registry**: [RulesList.java](file:///home/everton/arakakiin/regras-python/sonar-rules-arakakiin/src/main/java/com/arakakiin/sonar/python/checks/RulesList.java) declares and registers all active rules.
* **Rule Implementations**: The Java check classes are located under [src/main/java/com/arakakiin/sonar/python/checks/](file:///home/everton/arakakiin/regras-python/sonar-rules-arakakiin/src/main/java/com/arakakiin/sonar/python/checks/).
* **Rule Descriptions**: The HTML documentation and JSON configurations for the rules are located under [src/main/resources/org/sonar/l10n/python/rules/python/](file:///home/everton/arakakiin/regras-python/sonar-rules-arakakiin/src/main/resources/org/sonar/l10n/python/rules/python/).

---

## 🌱 Sustainability

Rule severities are assigned according to estimated carbon impact per occurrence, following the Green Software Foundation SCI specification (ISO 21031:2024) and precedents from ecoCode/creedengo.

---

## 📋 Prerequisites

* **Java**: JDK 21 (required to build and run)
* **Maven**: 3.6.3 or newer
* **SonarQube**: 9.9 LTS, 10.x, or newer

---

## 🛠️ Building the Plugin

To compile the source code and build the plugin deployable JAR file:

```bash
mvn clean package
```

The output plugin JAR will be created at:
`target/arakakiin-rules-plugin-1.0-SNAPSHOT.jar`

---

## 💾 Installation & Local Running (Docker)

1. Ensure you have built the plugin JAR using `mvn clean package`.
2. Start a SonarQube container using Docker, mounting the plugin JAR directly into the extensions directory:

```bash
docker run -d --name sonarqube \
  -p 9000:9000 \
  -v $(pwd)/target/arakakiin-rules-plugin-1.0-SNAPSHOT.jar:/opt/sonarqube/extensions/plugins/arakakiin-rules-plugin-1.0-SNAPSHOT.jar \
  sonarqube:community
```

Alternatively, you can use Docker Compose:

```yaml
version: "3"
services:
  sonarqube:
    image: sonarqube:community
    ports:
      - "9000:9000"
    volumes:
      - ./target/arakakiin-rules-plugin-1.0-SNAPSHOT.jar:/opt/sonarqube/extensions/plugins/arakakiin-rules-plugin-1.0-SNAPSHOT.jar
```

---

## ⚙️ Rule Activation & Usage

Once SonarQube is restarted, the custom rules must be activated in a **Quality Profile** associated with your target project:

### Step 1: Create or Extend a Quality Profile
1. Log into SonarQube as an **Administrator**.
2. Navigate to **Quality Profiles** in the top navigation bar.
3. Find the **Python** language section.
4. Click **Create** to make a new custom Quality Profile (or copy/extend the default "Sonar way" profile).

### Step 2: Activate the Arakakiin Custom Rules
1. Click on the name of your custom Quality Profile.
2. In the profile page, click **Activate More Rules**.
3. Under the **Repository** filter on the left pane, select **Arakakiin Custom Rules**.
4. Click **Bulk Activate** (or activate individual rules matching your needs).

### Step 3: Associate the Quality Profile with Your Project
1. Navigate to your project's main dashboard in SonarQube.
2. Click **Project Settings** $\rightarrow$ **Quality Profiles**.
3. Locate the **Python** row, click the dropdown menu, and select your custom Quality Profile.
4. Run your next project analysis (e.g., using `sonar-scanner`). The new rules will automatically execute and report findings.

---

## 🧪 Testing

### Running Unit Tests
Unit tests use the `PythonCheckVerifier` harness to verify check logic against mock Python resources:
```bash
mvn test
```

Repository and plugin registration tests (`CustomPythonRuleRepositoryTest`, `CustomPythonRulesPluginTest`) use JUnit 5 `@ParameterizedTest` to verify compatibility against three SonarQube versions (9.9 LTS, 10.x LTS, 26.2 Community Build).

### Running Integration Tests
Integration tests run a full SonarQube server instance via Sonar Orchestrator to verify plugin loading, API compliance, rule registration, and scanner report processing:
```bash
mvn clean verify -Pintegration-tests
```

### Code Coverage & SonarCloud

JaCoCo coverage reports are generated at `target/site/jacoco/jacoco.xml` after running `mvn clean test`. Test fixture Python files under `src/test/resources/checks/` are excluded from SonarCloud analysis via `<sonar.test.exclusions>` in `pom.xml` to avoid false positives on files that intentionally contain rule violations.

---

## 📄 License

Licensed under the [MIT License](LICENSE).
