/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

@DisplayName("ThreadLocalUsageCheck")
class ThreadLocalUsageCheckTest {

  @Nested
  @DisplayName("All scenarios")
  class Scenarios {
    @Test
    @DisplayName("flags threading.local() usage instead of ContextVar")
    void test() {
      PythonCheckVerifier.verify(
          "src/test/resources/checks/ThreadLocalUsage.py", new ThreadLocalUsageCheck());
    }
  }

  @Nested
  @DisplayName("Known gaps")
  class Gaps {
    @Test
    @Disabled("Gap: threading.local subclass instantiation is not yet flagged")
    @DisplayName("subclass of threading.local should be flagged when instantiated")
    void testThreadingLocalSubclass() {
      fail("Not yet implemented: see @Disabled annotation for details.");
    }
  }
}
