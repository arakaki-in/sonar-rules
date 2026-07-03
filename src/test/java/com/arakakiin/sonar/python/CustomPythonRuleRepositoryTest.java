/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.Version;

class CustomPythonRuleRepositoryTest {

  @ParameterizedTest
  @MethodSource("sonarQubeVersions")
  void test_rule_repository(Version sqVersion) {
    SonarRuntime sonarRuntime =
        SonarRuntimeImpl.forSonarQube(sqVersion, SonarQubeSide.SCANNER, SonarEdition.DEVELOPER);
    CustomPythonRuleRepository customPythonRuleRepository =
        new CustomPythonRuleRepository(sonarRuntime);
    RulesDefinition.Context context = new RulesDefinition.Context();
    customPythonRuleRepository.define(context);
    assertThat(customPythonRuleRepository.repositoryKey()).isEqualTo("arakakiin-rules");
    assertThat(context.repositories())
        .hasSize(1)
        .extracting("key")
        .containsExactly(customPythonRuleRepository.repositoryKey());
    var rules = context.repositories().get(0).rules();
    assertThat(rules).hasSize(26);
    assertThat(customPythonRuleRepository.checkClasses()).hasSize(26);
  }

  private static Stream<Version> sonarQubeVersions() {
    return Stream.of(
        Version.create(9, 9), // Minimum supported (9.9 LTS)
        Version.create(10, 8), // Intermediate (10.x LTS)
        Version.parse("26.2.0.119303") // Build target (Community Build)
        );
  }
}
