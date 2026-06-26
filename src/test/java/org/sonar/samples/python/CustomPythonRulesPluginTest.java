/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python;

import org.junit.jupiter.api.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.PluginContextImpl;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

import static org.assertj.core.api.Assertions.assertThat;

class CustomPythonRulesPluginTest {
  @Test
  void test() {
    SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarQube(Version.create(9, 9), SonarQubeSide.SCANNER, SonarEdition.DEVELOPER);
    Plugin.Context context = new PluginContextImpl.Builder().setSonarRuntime(sonarRuntime).build();
    new CustomPythonRulesPlugin().define(context);
    assertThat(context.getExtensions()).hasSize(1);
  }
}
