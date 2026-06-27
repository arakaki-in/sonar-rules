/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python.checks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

@DisplayName("Custom Python Visitor Check Test Suite")
class CustomPythonVisitorCheckTest {

  @Test
  @DisplayName("Should flag function definitions in Python test files")
  void verify_visitor_check_flags_function_definitions() {
    PythonCheckVerifier.verify("src/test/resources/checks/customPythonVisitorCheck.py", new CustomPythonVisitorCheck());
  }
}
