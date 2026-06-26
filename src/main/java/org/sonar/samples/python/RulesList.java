/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.sonar.plugins.python.api.PythonCheck;
import org.sonar.samples.python.checks.CustomPythonSubscriptionCheck;
import org.sonar.samples.python.checks.CustomPythonVisitorCheck;
import org.sonar.samples.python.checks.AvoidFileOpenWithoutWithCheck;

public final class RulesList {

  private RulesList() {
  }

  public static List<Class<? extends PythonCheck>> getChecks() {
    return new ArrayList<>(Stream.concat(
      getPythonChecks().stream(),
      getPythonTestChecks().stream()
    ).toList());
  }

  /**
   * These rules are going to target MAIN code only
   */
  public static List<Class<? extends PythonCheck>> getPythonChecks() {
    return new ArrayList<>(List.of(
      CustomPythonSubscriptionCheck.class,
      AvoidFileOpenWithoutWithCheck.class
    ));
  }

  /**
   * These rules are going to target TEST code only
   */
  public static List<Class<? extends PythonCheck>> getPythonTestChecks() {
    return new ArrayList<>(List.of(
      CustomPythonVisitorCheck.class
    ));
  }
}
