/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.PluginContextImpl;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

class CustomPythonRulesPluginTest {
  @ParameterizedTest
  @MethodSource("sonarQubeVersions")
  void test(Version sqVersion) {
    SonarRuntime sonarRuntime =
        SonarRuntimeImpl.forSonarQube(sqVersion, SonarQubeSide.SCANNER, SonarEdition.DEVELOPER);
    Plugin.Context context = new PluginContextImpl.Builder().setSonarRuntime(sonarRuntime).build();
    new CustomPythonRulesPlugin().define(context);
    assertThat(context.getExtensions()).hasSize(1);
  }

  private static Stream<Version> sonarQubeVersions() {
    return Stream.of(
        Version.create(9, 9), // Minimum supported (9.9 LTS)
        Version.create(10, 8), // Intermediate (10.x LTS)
        Version.parse("26.2.0.119303") // Build target (Community Build)
        );
  }
}
