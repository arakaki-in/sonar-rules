# SonarQube Python Custom Rules Plugin

A professional SonarQube plugin that adds custom static analysis rules for Python. This plugin serves as a template and starting point for implementing custom Python rules inside SonarQube.

## Features

This plugin registers three custom rules under the repository `python-custom-rules-example`:

1. **Avoid File Open Without With (`AvoidFileOpenWithoutWithCheck`)**
   - **Key**: `AvoidFileOpenWithoutWith`
   - **Description**: Ensures that file operations use the `with` statement to guarantee proper resource cleanup.
   - **Type**: Code Smell

2. **Custom Python Subscription Check (`CustomPythonSubscriptionCheck`)**
   - **Key**: `subscription`
   - **Description**: Demonstrates a syntax node consumer check that flags the use of `for` statements.
   - **Type**: Code Smell

3. **Custom Python Visitor Check (`CustomPythonVisitorCheck`)**
   - **Key**: `visitor`
   - **Description**: Demonstrates a visitor-based check that flags function definitions in Python test files.
   - **Type**: Code Smell

## Prerequisites

- **Java**: JDK 21
- **Maven**: 3.6+
- **SonarQube**: Compatible with SonarQube 9.9+ LTS and newer Community/Developer editions.

## Building the Plugin

To compile the codebase and package the plugin into a deployable jar file, run:

```bash
mvn clean package
```

The compiled plugin jar file will be located at `target/sonar-rules-arakakiin-1.0-SNAPSHOT.jar`.

## Deployment

1. Copy the plugin jar file to your SonarQube server's extensions directory:
   ```bash
   cp target/sonar-rules-arakakiin-1.0-SNAPSHOT.jar /path/to/sonarqube/extensions/plugins/
   ```
2. Restart the SonarQube server.
3. Log in as an administrator, navigate to **Quality Profiles**, and activate the new rules under the Python language profile.

## Running Tests

### Unit Tests

Unit tests execute quickly using the `PythonCheckVerifier` test harness and do not require a running SonarQube instance:

```bash
mvn test
```

### Integration Tests

Integration tests run an embedded SonarQube server using the Sonar Orchestrator to verify rule registration, profile activation, and analysis execution:

```bash
mvn clean verify -Pintegration-tests
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
