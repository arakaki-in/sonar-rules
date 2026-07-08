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

@DisplayName("NoGlobalMutableStateCheck")
class NoGlobalMutableStateCheckTest {

  @Nested
  @DisplayName("All scenarios")
  class Scenarios {
    @Test
    @DisplayName("flags mutable global state and global keyword")
    void test() {
      PythonCheckVerifier.verify(
          "src/test/resources/checks/NoGlobalMutableState.py", new NoGlobalMutableStateCheck());
    }
  }

  @Nested
  @DisplayName("Known gaps")
  class Gaps {
    @Test
    @Disabled("Gap: annotated mutable globals like 'annotated_list: list = []' are not flagged")
    @DisplayName("annotated mutable global list should be flagged")
    void testAnnotatedMutableGlobal() {
      fail("Not yet implemented: see @Disabled annotation for details.");
    }

    @Test
    @Disabled(
        "Gap: multi-assignment mutable globals like 'tuple_a, tuple_b = [], {}' are not flagged")
    @DisplayName("multi-assignment mutable globals should be flagged")
    void testMultiAssignmentMutableGlobals() {
      fail("Not yet implemented: see @Disabled annotation for details.");
    }

    @Test
    @Disabled("Gap: class definitions at module level creating mutable instances are not flagged")
    @DisplayName("module-level class definition should be flagged")
    void testModuleLevelClassDefinition() {
      fail("Not yet implemented: see @Disabled annotation for details.");
    }

    @Test
    @Disabled("Gap: module-level instance creation from classes is not flagged")
    @DisplayName("module-level class instance should be flagged")
    void testModuleLevelClassInstance() {
      fail("Not yet implemented: see @Disabled annotation for details.");
    }
  }
}
