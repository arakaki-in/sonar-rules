/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python;

import static org.assertj.core.api.Assertions.assertThat;

import com.eclipsesource.json.Json;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Typed facade over SonarQube's REST API, hiding HTTP, authentication, and JSON parsing behind
 * domain operations. Used by integration tests that need to drive a live SonarQube instance.
 *
 * <p>The client is immutable: each instance holds a fixed {@code serverUrl} and {@code
 * authorization} header. Use the factory methods to connect with the right credentials for the
 * operation phase.
 *
 * <p>All URL encoding is handled internally. All JSON parsing uses {@link
 * com.eclipsesource.json.Json} (already on the test classpath via sonar-orchestrator). No method
 * returns a raw {@link HttpResponse} — callers receive typed results or void.
 */
class SonarQubeApiClient {

  private final HttpClient httpClient;
  private final String serverUrl;
  private final String authorization;

  private SonarQubeApiClient(String serverUrl, String authorization) {
    this.httpClient = HttpClient.newHttpClient();
    this.serverUrl = serverUrl;
    this.authorization = authorization;
  }

  // ---------------------------------------------------------------------------
  // Factories
  // ---------------------------------------------------------------------------

  /** Connect using HTTP Basic authentication (admin operations). */
  static SonarQubeApiClient withBasicAuth(String serverUrl, String login, String password) {
    String credentials = login + ":" + password;
    String encoded =
        Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    return new SonarQubeApiClient(serverUrl, "Basic " + encoded);
  }

  /** Connect using a bearer token (authenticated operations). */
  static SonarQubeApiClient withBearerToken(String serverUrl, String token) {
    return new SonarQubeApiClient(serverUrl, "Bearer " + token);
  }

  // ---------------------------------------------------------------------------
  // Auth
  // ---------------------------------------------------------------------------

  /** Generate an API token for the authenticated user. */
  String generateToken(String tokenName) throws IOException, InterruptedException {
    HttpResponse<String> response = postRaw("/api/user_tokens/generate", Map.of("name", tokenName));
    assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
    return Json.parse(response.body()).asObject().get("token").asString();
  }

  // ---------------------------------------------------------------------------
  // Quality profiles
  // ---------------------------------------------------------------------------

  /** Create a quality profile and return its key. */
  String createQualityProfile(String name, String language)
      throws IOException, InterruptedException {
    HttpResponse<String> response =
        postRaw("/api/qualityprofiles/create", Map.of("language", language, "name", name));
    assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
    return Json.parse(response.body()).asObject().get("key").asString();
  }

  /** Set a quality profile as the default for its language. */
  void setDefaultQualityProfile(String profileKey, String profileName, String language)
      throws IOException, InterruptedException {
    HttpResponse<String> response =
        postRaw(
            "/api/qualityprofiles/set_default",
            Map.of(
                "key", profileKey,
                "qualityProfile", profileName,
                "language", language));
    assertThat(response.statusCode()).as(response.body()).isBetween(200, 204);
  }

  /** Activate a rule in a quality profile. */
  void activateRule(String profileKey, String ruleKey) throws IOException, InterruptedException {
    HttpResponse<String> response =
        postRaw(
            "/api/qualityprofiles/activate_rule",
            Map.of("rule", ruleKey, "profile_key", profileKey));
    assertThat(response.statusCode()).as(response.body()).isBetween(200, 204);
  }

  // ---------------------------------------------------------------------------
  // Compute Engine
  // ---------------------------------------------------------------------------

  /** Poll the Compute Engine for the current analysis status of a project. */
  String getAnalysisStatus(String projectKey) throws IOException, InterruptedException {
    HttpResponse<String> response = getRaw("/api/ce/component?component=" + encode(projectKey));
    assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
    return Json.parse(response.body()).asObject().get("status").asString();
  }

  /**
   * Block until the CE finishes processing analysis for {@code projectKey}, or the {@code timeout}
   * is reached.
   *
   * <p>Uses exponential backoff: starts at 1 s, multiplies by 1.5× each step, capped at 15 s
   * between polls. Logs state transitions so CI failures are debuggable without re-running locally.
   *
   * @throws AssertionError if the analysis fails or is cancelled, or if the timeout expires
   */
  @SuppressWarnings("java:S2925")
  void waitForAnalysis(String projectKey, Duration timeout)
      throws IOException, InterruptedException {
    Instant deadline = Instant.now().plus(timeout);
    Duration delay = Duration.ofSeconds(1);
    Duration maxDelay = Duration.ofSeconds(15);
    String previousStatus = null;

    while (Instant.now().isBefore(deadline)) {
      String status = getAnalysisStatus(projectKey);

      if (!status.equals(previousStatus)) {
        System.out.printf(
            "[SonarQubeApiClient] CE status for %s: %s → %s%n", projectKey, previousStatus, status);
        previousStatus = status;
      }

      if (status.equals("SUCCESS")) {
        return;
      }
      if (status.equals("FAILED") || status.equals("CANCELED")) {
        throw new AssertionError("Analysis " + status.toLowerCase() + " for " + projectKey);
      }

      Thread.sleep(delay.toMillis());
      delay = cap(delay.multipliedBy(3).dividedBy(2), maxDelay);
    }
    throw new AssertionError(
        "Timed out waiting for analysis of " + projectKey + " after " + timeout);
  }

  private static Duration cap(Duration value, Duration max) {
    return value.compareTo(max) > 0 ? max : value;
  }

  // ---------------------------------------------------------------------------
  // Issues
  // ---------------------------------------------------------------------------

  /** Return the number of open issues for a given project and rule. */
  int searchIssueCount(String projectKey, String ruleKey) throws IOException, InterruptedException {
    HttpResponse<String> response =
        getRaw(
            "/api/issues/search?componentKeys=" + encode(projectKey) + "&rules=" + encode(ruleKey));
    assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
    return Json.parse(response.body()).asObject().get("total").asInt();
  }

  // ---------------------------------------------------------------------------
  // HTTP plumbing (private)
  // ---------------------------------------------------------------------------

  private HttpResponse<String> getRaw(String path) throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(serverUrl + path))
            .header("Authorization", authorization)
            .GET()
            .build();
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private HttpResponse<String> postRaw(String path, Map<String, String> params)
      throws IOException, InterruptedException {
    String body =
        params.entrySet().stream()
            .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
            .collect(Collectors.joining("&"));

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(serverUrl + path))
            .header("Authorization", authorization)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
