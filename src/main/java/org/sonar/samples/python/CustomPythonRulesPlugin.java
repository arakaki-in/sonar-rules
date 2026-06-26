/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python;

import org.sonar.api.Plugin;

public class CustomPythonRulesPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtension(CustomPythonRuleRepository.class);
  }

}
