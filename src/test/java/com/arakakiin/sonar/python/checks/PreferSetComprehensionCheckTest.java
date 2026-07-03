/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

class PreferSetComprehensionCheckTest {
  @Test
  void test() {
    PythonCheckVerifier.verify(
        "src/test/resources/checks/PreferSetComprehension.py", new PreferSetComprehensionCheck());
  }
}
