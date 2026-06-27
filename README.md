# SonarQube Python Custom Rules Plugin (Arakakiin Rules)

[![CI/CD Pipeline](https://github.com/arakaki-in/sonar-rules/actions/workflows/release.yml/badge.svg)](https://github.com/arakaki-in/sonar-rules/actions/workflows/release.yml)
[![MIT License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A highly optimized SonarQube plugin implementing 16 advanced performance, resource management, database safety, and concurrency custom rules for Python static analysis.

---

## 🚀 Features & Custom Rules

This plugin registers 16 custom rules under the repository **Arakakiin Custom Rules** (Key: `arakakiin-rules`).

### 1. Concurrency
* **No Global Mutable State (`NoGlobalMutableState`)**
  * Prevents the usage of global variables containing mutable types (like dictionaries, lists, or custom class instances) to avoid race conditions.
* **ThreadLocal vs ContextVar (`ThreadLocalUsage`)**
  * Promotes using modern, async-safe `contextvars.ContextVar` instead of legacy `threading.local` for scoping variables in concurrent environments.
* **Immutable Data Transfer (`ImmutableDataTransfer`)**
  * Enforces the use of immutable structures (e.g., `frozenset`, `tuple`, or read-only structures) when transferring data across concurrency boundaries.

### 2. Resource Management
* **Enforce Connection Pooling (`EnforceConnectionPooling`)**
  * Flags raw socket connections or single database connection initializations that bypass established connection pools.
* **Context Managers Mandatory (`AvoidFileOpenWithoutWith`)**
  * Ensures file, network, and database operations use `with` statements to guarantee correct resource allocation and cleanup.
* **Mandatory Timeouts (`MandatoryTimeouts`)**
  * Flags network requests (e.g., via the `requests` library) that do not specify an explicit timeout parameter, preventing hung threads.

### 3. Database Safety & Performance
* **Zero N+1 Queries (`ZeroNPlusOneQueries`)**
  * Detects database queries executed inside iterative loops, recommending batch loading instead.
* **Avoid `SELECT *` on Heavy Tables (`AvoidSelectStar`)**
  * Flags the use of `SELECT *` or unbound ORM queries on large tables to prevent unnecessary CPU and network overhead.
* **Batch Operations Required (`BatchOperationsRequired`)**
  * Recommends batch inserts/updates (e.g., `bulk_create`) when inserting or modifying multiple records in a loop.
* **DB-Level Aggregation (`DbLevelAggregation`)**
  * Restricts fetching all table records only to perform sums, counts, or averages in-memory in Python; enforces utilizing database aggregation functions.

### 4. CPU & Memory Optimization
* **Avoid Eager Regex Compilation (`AvoidEagerRegexCompilation`)**
  * Recommends static compilation of regular expressions rather than repeatedly compiles within critical loop sections.
* **Generators Over Lists for Large Data (`GeneratorsOverLists`)**
  * Encourages using generators or iterators instead of building massive lists in memory when processing large files or query streams.
* **Efficient String Concatenation (`EfficientStringConcatenation`)**
  * Flags sequential string addition (`+` or `+=`) inside loops, recommending `''.join()` for optimized memory allocation.
* **Use `__slots__` for High-Volume Objects (`UseSlots`)**
  * Mandates defining `__slots__` in data models or instances that are instantiated in high volume to save RAM.
* **Fast JSON Parsing (`FastJsonParsing`)**
  * Recommends high-performance JSON libraries (like `orjson` or `ujson`) instead of standard `json` in performance-critical execution blocks.

### 5. Logging & Error Handling
* **Avoid Try/Except for Control Flow (`AvoidTryExceptControlFlow`)**
  * Prevents using try/except exception blocks for normal, predictable control flow logic.

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

### Running Integration Tests
Integration tests run a full SonarQube server instance via Sonar Orchestrator to verify plugin loading, API compliance, rule registration, and scanner report processing:
```bash
mvn clean verify -Pintegration-tests
```

---

## 📄 License

Licensed under the [MIT License](LICENSE).
