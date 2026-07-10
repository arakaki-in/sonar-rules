/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

@DisplayName("AvoidPandasIterrowsCheck")
class AvoidPandasIterrowsCheckTest {
  @Nested
  @DisplayName("All scenarios")
  class Scenarios {
    @Test
    @DisplayName("flags DataFrame.iterrows() usage")
    void test() {
      PythonCheckVerifier.verify(
          "src/test/resources/checks/AvoidPandasIterrows.py", new AvoidPandasIterrowsCheck());
    }
  }
}
