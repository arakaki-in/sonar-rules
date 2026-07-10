/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

@DisplayName("PreferSetComprehensionCheck")
class PreferSetComprehensionCheckTest {

  @Nested
  @DisplayName("All scenarios")
  class Scenarios {
    @Test
    @DisplayName("flags set([list comprehension]) over set comprehension")
    void test() {
      PythonCheckVerifier.verify(
          "src/test/resources/checks/PreferSetComprehension.py", new PreferSetComprehensionCheck());
    }
  }
}
