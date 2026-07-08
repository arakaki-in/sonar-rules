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

@DisplayName("EnforceConnectionPoolingCheck")
class EnforceConnectionPoolingCheckTest {

  @Nested
  @DisplayName("All scenarios")
  class Scenarios {
    @Test
    @DisplayName("flags raw DB connections and HTTP calls in loops without pooling")
    void test() {
      PythonCheckVerifier.verify(
          "src/test/resources/checks/EnforceConnectionPooling.py",
          new EnforceConnectionPoolingCheck());
    }
  }

  @Nested
  @DisplayName("Known gaps")
  class Gaps {
    @Test
    @Disabled(
        "Gap closed: list comprehensions were previously a false negative but are now flagged")
    @DisplayName("list comprehension HTTP calls without session are now flagged (gap closed)")
    void testListComprehensionHttpCalls() {}
  }
}
