/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidStarImportCheck.RULE_KEY)
public class AvoidStarImportCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidStarImport";
  private static final String MESSAGE =
      "Avoid wildcard imports ('from module import *'). Explicitly import required names to avoid"
          + " namespace pollution.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.IMPORT_FROM, this::checkImportFrom);
  }

  private void checkImportFrom(SubscriptionContext ctx) {
    ImportFrom importFrom = (ImportFrom) ctx.syntaxNode();
    if (importFrom.isWildcardImport()) {
      ctx.addIssue(importFrom, MESSAGE);
    }
  }
}
