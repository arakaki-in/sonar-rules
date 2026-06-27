/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.Version;

class CustomPythonRuleRepositoryTest {

  @Test
  void test_rule_repository() {
    SonarRuntime sonarRuntime =
        SonarRuntimeImpl.forSonarQube(
            Version.create(9, 9), SonarQubeSide.SCANNER, SonarEdition.DEVELOPER);
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
    assertThat(rules).hasSize(16);
    assertThat(customPythonRuleRepository.checkClasses()).hasSize(16);
  }
}
