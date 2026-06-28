# Contributing to Arakakiin Rules Plugin

Thank you for your interest in contributing to the **Arakakiin Rules Plugin**! This document provides guidelines and instructions on how to set up the project, implement new custom rules, run tests, and submit contributions.

---

## 🛠️ Development Setup

### Prerequisites
* **Java Development Kit (JDK)**: JDK 21 (LTS) or newer.
* **Apache Maven**: version 3.6.3 or newer.
* **SonarQube**: 9.9 LTS, 10.x, or newer (for local manual verification).

### Cloning and Building
Clone the repository and build the package to verify your environment is set up correctly:
```bash
git clone https://github.com/arakaki-in/sonar-rules.git
cd sonar-rules
mvn clean package
```

The output plugin JAR will be built at `target/arakakiin-rules-plugin-1.0-SNAPSHOT.jar`.

---

## 📐 Project Structure

* `src/main/java/com/arakakiin/sonar/python/`
  * `CustomPythonRulesPlugin.java`: Entrypoint of the plugin.
  * `CustomPythonRuleRepository.java`: Custom rules repository definition.
  * `RulesList.java`: Active registration list of all custom rules.
* `src/main/java/com/arakakiin/sonar/python/checks/`: Implementation of custom rules (`PythonCheck` instances).
* `src/main/resources/org/sonar/l10n/python/rules/python/`: JSON and HTML files containing rule metadata (descriptions and settings).
* `src/test/java/com/arakakiin/sonar/python/checks/`: JUnit 5 test classes.
* `src/test/resources/checks/`: Python test resources containing compliant and non-compliant code examples.

---

## ✍️ How to Add a New Custom Rule

To implement and register a new custom Python rule:

### Step 1: Implement the Check class
Create a class extending `PythonCheck` (or `PythonSubscriptionCheck` / `PythonVisitorCheck` depending on complexity) under `src/main/java/com/arakakiin/sonar/python/checks/`. 

* Prefix the class name with a descriptive check name, ending in `Check` (e.g. `MyNewRuleCheck.java`).
* Annotate the class with `@Rule(key = "MyNewRule")`.
* Maintain the standard license header at the top of the file.

### Step 2: Register the check
Add your check class to `RulesList.java` inside the list of active checks:
```java
// RulesList.java
List.of(
    // ...
    MyNewRuleCheck.class
)
```

### Step 3: Add Rule Metadata
Create the following two files under `src/main/resources/org/sonar/l10n/python/rules/python/`:
1. `MyNewRule.json`: Contains the rule metadata (title, status, severity, tags).
2. `MyNewRule.html`: HTML file detailing the rule description, compliant code, and non-compliant code examples.

### Step 4: Write Unit Tests
Create a test class `MyNewRuleCheckTest.java` under `src/test/java/com/arakakiin/sonar/python/checks/`. Use the `PythonCheckVerifier` harness:
```java
package com.arakakiin.sonar.python.checks;

import org.junit.jupiter.api.Test;
import org.sonar.plugins.python.api.PythonCheckVerifier;

class MyNewRuleCheckTest {
  @Test
  void test() {
    PythonCheckVerifier.verify("src/test/resources/checks/myNewRule.py", new MyNewRuleCheck());
  }
}
```

Create a corresponding Python file `myNewRule.py` under `src/test/resources/checks/`. Mark non-compliant lines using comments:
```python
def bad_code():
    x = 1 + 1 # Noncompliant {{Describe the rule violation message here}}

def good_code():
    pass
```

### Step 5: Update Integration Tests
Register the new rule key in CustomRulesIntegrationTest.java under the `RULE_KEYS` list to ensure that it runs successfully inside a full SonarQube instance.

---

## 🧪 Testing Commands

Ensure all tests pass before making a pull request.

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests (Requires Docker)
Integration tests start a local SonarQube community edition instance and analyze the checks:
```bash
mvn clean verify -Pintegration-tests
```

### Run Security Audit
Scan the project dependencies for CVEs:
```bash
mvn verify -Psecurity-audit
```

---

## 🎨 Code Quality & Formatting

The codebase strictly adheres to the **Google Java Format**. Code formatting is enforced at compile and package time using `spotless-maven-plugin`.

* To check code formatting:
  ```bash
  mvn spotless:check
  ```
* To automatically format the codebase and prepend the license headers:
  ```bash
  mvn spotless:apply
  ```
