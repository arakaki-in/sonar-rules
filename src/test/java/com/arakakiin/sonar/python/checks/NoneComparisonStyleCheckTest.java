/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

@DisplayName("NoneComparisonStyleCheck")
class NoneComparisonStyleCheckTest {

  @Nested
  @DisplayName("All scenarios")
  class Scenarios {
    @Test
    @DisplayName("flags == and != with None, accepts 'is None' and 'is not None'")
    void test() {
      PythonCheckVerifier.verify(
          "src/test/resources/checks/NoneComparisonStyle.py", new NoneComparisonStyleCheck());
    }
  }
}
