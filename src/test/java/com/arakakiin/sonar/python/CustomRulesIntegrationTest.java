/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python;

import static org.assertj.core.api.Assertions.assertThat;

import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.junit5.OrchestratorExtension;
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.locator.MavenLocation;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("CustomRulesIntegrationTest")
class CustomRulesIntegrationTest {

  private static final String ADMIN_LOGIN = "admin";
  private static final String DEFAULT_ADMIN_PASSWORD = "admin";
  private static final String PROJECT_KEY = "sample-python-project";
  private static final String SONAR_VERSION = "26.2.0.119303";

  private static final List<String> RULE_KEYS = computeRuleKeys();

  private static List<String> computeRuleKeys() {
    return RulesList.getChecks().stream()
        .map(cls -> cls.getAnnotation(org.sonar.check.Rule.class).key())
        .map(key -> "arakakiin-rules:" + key)
        .toList();
  }

  @RegisterExtension
  static final OrchestratorExtension ORCHESTRATOR =
      OrchestratorExtension.builderEnv()
          .setOrchestratorProperty(
              "orchestrator.artifactory.url", "https://repo.maven.apache.org/maven2")
          .setSonarVersion(SONAR_VERSION)
          .addPlugin(
              MavenLocation.create("org.sonarsource.python", "sonar-python-plugin", "5.23.0.33560"))
          .addPlugin(
              FileLocation.byWildcardMavenFilename(
                  new File("target"), "arakakiin-rules-plugin-*.jar"))
          .build();

  /** API client authenticated with an admin-generated token. Set up once before all tests. */
  private static SonarQubeApiClient api;

  @BeforeAll
  static void setUp() throws Exception {
    String serverUrl = ORCHESTRATOR.getServer().getUrl();

    // Phase 1: admin operations (basic auth)
    SonarQubeApiClient adminApi =
        SonarQubeApiClient.withBasicAuth(serverUrl, ADMIN_LOGIN, DEFAULT_ADMIN_PASSWORD);
    String token = adminApi.generateToken("arakakiin-it-" + Instant.now().toEpochMilli());

    // Phase 2: authenticated operations (bearer token)
    api = SonarQubeApiClient.withBearerToken(serverUrl, token);

    String profileKey = api.createQualityProfile("CustomPythonProfile", "py");
    api.setDefaultQualityProfile(profileKey, "CustomPythonProfile", "py");
    for (String ruleKey : RULE_KEYS) {
      api.activateRule(profileKey, ruleKey);
    }

    ORCHESTRATOR.executeBuild(
        SonarScanner.create(new File("src/test/resources/checks"))
            .setProperty("sonar.projectKey", PROJECT_KEY)
            .setProperty("sonar.projectName", "Sample Python Project")
            .setProperty("sonar.sources", ".")
            .setProperty("sonar.token", token));

    api.waitForAnalysis(PROJECT_KEY, Duration.ofMinutes(2));
  }

  // ---------------------------------------------------------------------------
  // Tests
  // ---------------------------------------------------------------------------

  @Test
  @DisplayName("fixture directory exists")
  void fixtureDirectoryExists() {
    File projectDir = new File("src/test/resources/checks");
    assertThat(projectDir).isDirectory();
  }

  @ParameterizedTest
  @MethodSource("ruleKeys")
  @DisplayName("each rule produces at least 1 issue against its fixture")
  void eachRuleHasIssues(String ruleKey) throws Exception {
    int total = api.searchIssueCount(PROJECT_KEY, ruleKey);
    assertThat(total)
        .as(ruleKey + " has 0 issues — check fixture for missing # Noncompliant markers")
        .isPositive();
  }

  // ---------------------------------------------------------------------------
  // Method sources
  // ---------------------------------------------------------------------------

  static Stream<String> ruleKeys() {
    return RULE_KEYS.stream();
  }
}
