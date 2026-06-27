/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python.checks;

import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

class GeneratorsOverListsCheckTest {
  @Test
  void test() {
    PythonCheckVerifier.verify("src/test/resources/checks/GeneratorsOverLists.py", new GeneratorsOverListsCheck());
  }
}
