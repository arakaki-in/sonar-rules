/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python;

import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.junit5.OrchestratorExtension;
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.locator.MavenLocation;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class CustomRulesIntegrationTest {

  private static final String ADMIN_LOGIN = "admin";
  private static final String DEFAULT_ADMIN_PASSWORD = "admin";
  private static final String PROJECT_KEY = "sample-python-project";
  private static final String SONAR_VERSION = "26.2.0.119303";

  private static final List<String> RULE_KEYS = List.of(
      "python-custom-rules-example:AvoidFileOpenWithoutWith",
      "python-custom-rules-example:subscription"
  );

  @RegisterExtension
  static final OrchestratorExtension ORCHESTRATOR = OrchestratorExtension.builderEnv()
      .setOrchestratorProperty("orchestrator.artifactory.url", "https://repo.maven.apache.org/maven2")
      .setSonarVersion(SONAR_VERSION)
      .addPlugin(MavenLocation.create("org.sonarsource.python", "sonar-python-plugin", "5.23.0.33560"))
      .addPlugin(FileLocation.byWildcardMavenFilename(new File("target"), "sonar-rules-arakakiin-*.jar"))
      .build();

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Test
  void testRulesIntegration() throws Exception {
    File projectDir = new File("src/test/resources/sample-python-project");
    assertThat(projectDir).isDirectory();

    String serverUrl = ORCHESTRATOR.getServer().getUrl();
    String token = generateAdminToken(serverUrl);
    activateRules(serverUrl, token);

    ORCHESTRATOR.executeBuild(SonarScanner.create(projectDir, "sonar.token", token));
    waitForAnalysisProcessing(serverUrl, token);

    for (String ruleKey : RULE_KEYS) {
      HttpResponse<String> response = get(serverUrl,
          "/api/issues/search?componentKeys=" + encode(PROJECT_KEY) + "&rules=" + encode(ruleKey),
          bearer(token));

      assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
      assertThat(issueTotal(response.body()))
          .as("Expected rule %s to trigger exactly 1 issue, response: %s", ruleKey, response.body())
          .isEqualTo(1);
    }
  }

  private String generateAdminToken(String serverUrl) throws Exception {
    HttpResponse<String> response = post(serverUrl,
        "/api/user_tokens/generate",
        form("name", "arakakiin-it-" + Instant.now().toEpochMilli()),
        basic(ADMIN_LOGIN, DEFAULT_ADMIN_PASSWORD));

    assertThat(response.statusCode()).as(response.body()).isEqualTo(200);

    Matcher matcher = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(response.body());
    assertThat(matcher.find()).as(response.body()).isTrue();
    return matcher.group(1);
  }

  private void activateRules(String serverUrl, String token) throws Exception {
    HttpResponse<String> createResponse = post(serverUrl,
        "/api/qualityprofiles/create",
        form(
            "language", "py",
            "name", "CustomPythonProfile"),
        bearer(token));
    assertThat(createResponse.statusCode()).as(createResponse.body()).isEqualTo(200);

    Matcher keyMatcher = Pattern.compile("\"key\"\\s*:\\s*\"([^\"]+)\"").matcher(createResponse.body());
    assertThat(keyMatcher.find()).as(createResponse.body()).isTrue();
    String profileKey = keyMatcher.group(1);

    HttpResponse<String> defaultResponse = post(serverUrl,
        "/api/qualityprofiles/set_default",
        form(
            "key", profileKey,
            "qualityProfile", "CustomPythonProfile",
            "language", "py"),
        bearer(token));
    assertThat(defaultResponse.statusCode()).as(defaultResponse.body()).isBetween(200, 204);

    for (String ruleKey : RULE_KEYS) {
      HttpResponse<String> activationResponse = post(serverUrl,
          "/api/qualityprofiles/activate_rule",
          form(
              "profile_key", profileKey,
              "key", profileKey,
              "qualityProfile", "CustomPythonProfile",
              "language", "py",
              "rule", ruleKey),
          bearer(token));

      assertThat(activationResponse.statusCode()).as(activationResponse.body()).isBetween(200, 204);
    }
  }

  private void waitForAnalysisProcessing(String serverUrl, String token) throws Exception {
    long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.MINUTES.toNanos(2);
    while (System.nanoTime() < deadline) {
      HttpResponse<String> response = get(serverUrl,
          "/api/ce/component?component=" + encode(PROJECT_KEY),
          bearer(token));

      assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
      String status = firstJsonValue(response.body(), "status");
      if ("SUCCESS".equals(status)) {
        return;
      }
      assertThat(status).as(response.body()).isNotEqualTo("FAILED").isNotEqualTo("CANCELED");
      Thread.sleep(1_000);
    }
    throw new AssertionError("Timed out waiting for SonarQube to process the analysis report");
  }

  private HttpResponse<String> get(String serverUrl, String path, String authorization) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(serverUrl + path))
        .header("Authorization", authorization)
        .GET()
        .build();
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private HttpResponse<String> post(String serverUrl, String path, String body, String authorization) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(serverUrl + path))
        .header("Authorization", authorization)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private static String basic(String login, String password) {
    String credentials = login + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
  }

  private static String bearer(String token) {
    return "Bearer " + token;
  }

  private static String form(String... keyValues) {
    assertThat(keyValues.length % 2).isZero();

    StringBuilder body = new StringBuilder();
    for (int index = 0; index < keyValues.length; index += 2) {
      if (body.length() > 0) {
        body.append('&');
      }
      body.append(encode(keyValues[index])).append('=').append(encode(keyValues[index + 1]));
    }
    return body.toString();
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private static int issueTotal(String responseBody) {
    Matcher matcher = Pattern.compile("\"total\"\\s*:\\s*(\\d+)").matcher(responseBody);
    assertThat(matcher.find()).as(responseBody).isTrue();
    return Integer.parseInt(matcher.group(1));
  }

  private static String firstJsonValue(String responseBody, String propertyName) {
    Matcher matcher = Pattern.compile("\"" + Pattern.quote(propertyName) + "\"\\s*:\\s*\"([^\"]+)\"").matcher(responseBody);
    assertThat(matcher.find()).as(responseBody).isTrue();
    return matcher.group(1);
  }
}
