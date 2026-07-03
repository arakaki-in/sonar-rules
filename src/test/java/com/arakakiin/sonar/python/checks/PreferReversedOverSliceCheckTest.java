/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

class PreferReversedOverSliceCheckTest {
  @Test
  void test() {
    PythonCheckVerifier.verify(
        "src/test/resources/checks/PreferReversedOverSlice.py", new PreferReversedOverSliceCheck());
  }
}
