/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python.checks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

@DisplayName("Custom Python Subscription Check Test Suite")
class CustomPythonSubscriptionCheckTest {

  @Test
  @DisplayName("Should flag for loop statements in Python source files")
  void verify_subscription_check_flags_for_loops() {
    PythonCheckVerifier.verify("src/test/resources/checks/customPythonSubscriptionCheck.py", new CustomPythonSubscriptionCheck());
  }
}
