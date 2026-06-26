/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonVisitorCheck;
import org.sonar.plugins.python.api.tree.FunctionDef;

@Rule(key = CustomPythonVisitorCheck.RULE_KEY)
public class CustomPythonVisitorCheck extends PythonVisitorCheck {

  public static final String RULE_KEY = "visitor";

  @Override
  public void visitFunctionDef(FunctionDef pyFunctionDefTree) {
    addIssue(pyFunctionDefTree.name(), "Function def.");
    super.visitFunctionDef(pyFunctionDefTree);
  }

}
