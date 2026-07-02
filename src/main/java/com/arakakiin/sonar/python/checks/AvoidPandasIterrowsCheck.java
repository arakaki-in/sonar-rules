/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidPandasIterrowsCheck.RULE_KEY)
public class AvoidPandasIterrowsCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidPandasIterrows";
  private static final String MESSAGE =
      "Avoid using pandas 'iterrows()'. Use vectorized operations, 'itertuples()', or 'apply()'"
          + " instead for better performance.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression call = (CallExpression) ctx.syntaxNode();
    Expression callee = call.callee();
    if (callee instanceof QualifiedExpression qualifiedExpression) {
      if ("iterrows".equals(qualifiedExpression.name().name())) {
        ctx.addIssue(call, MESSAGE);
      }
    }
  }
}
