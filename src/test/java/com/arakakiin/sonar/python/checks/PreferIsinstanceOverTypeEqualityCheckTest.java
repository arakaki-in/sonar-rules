/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

@DisplayName("PreferIsinstanceOverTypeEqualityCheck")
class PreferIsinstanceOverTypeEqualityCheckTest {

  @Nested
  @DisplayName("All scenarios")
  class Scenarios {
    @Test
    @DisplayName("flags type() == comparisons instead of isinstance()")
    void test() {
      PythonCheckVerifier.verify(
          "src/test/resources/checks/PreferIsinstanceOverTypeEquality.py",
          new PreferIsinstanceOverTypeEqualityCheck());
    }
  }
}
