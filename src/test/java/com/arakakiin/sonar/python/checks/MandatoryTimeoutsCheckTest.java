/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

@DisplayName("MandatoryTimeoutsCheck")
class MandatoryTimeoutsCheckTest {

  @Nested
  @DisplayName("All scenarios")
  class Scenarios {
    @Test
    @DisplayName("flags missing timeout, accepts explicit timeout")
    void test() {
      PythonCheckVerifier.verify(
          "src/test/resources/checks/MandatoryTimeouts.py", new MandatoryTimeoutsCheck());
    }
  }

  @Nested
  @DisplayName("Known gaps")
  class Gaps {
    @Test
    @Disabled("False positive: **kwargs with timeout=5 is not detected")
    @DisplayName("**kwargs with timeout=5 should not be flagged")
    void testKwargsTimeout() {}

    @Test
    @Disabled("False positive: positional timeout argument is not detected")
    @DisplayName("urlopen with positional timeout should not be flagged")
    void testPositionalTimeout() {}

    @Test
    @Disabled("False negative: variable assigned to None bypasses check")
    @DisplayName("timeout=t where t=None should be flagged")
    void testVariableTimeoutNone() {}
  }
}
