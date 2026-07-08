/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

@DisplayName("DequeOverListInsertCheck")
class DequeOverListInsertCheckTest {
  @Nested
  @DisplayName("All scenarios")
  class Scenarios {
    @Test
    @DisplayName("flags list.insert(0) and list.pop(0)")
    void test() {
      PythonCheckVerifier.verify(
          "src/test/resources/checks/DequeOverListInsert.py", new DequeOverListInsertCheck());
    }
  }
}
