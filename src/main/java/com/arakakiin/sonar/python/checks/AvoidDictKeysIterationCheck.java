/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidDictKeysIterationCheck.RULE_KEY)
public class AvoidDictKeysIterationCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidDictKeysIteration";
  private static final String MESSAGE =
      "Avoid calling '.keys()' when checking for key membership in a dictionary. Use 'key in dict'"
          + " directly.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.IN, this::checkInExpression);
  }

  private void checkInExpression(SubscriptionContext ctx) {
    InExpression inExpr = (InExpression) ctx.syntaxNode();
    Expression right = inExpr.rightOperand();
    if (right instanceof CallExpression call) {
      Expression callee = call.callee();
      if (callee instanceof QualifiedExpression qualifiedExpression) {
        if ("keys".equals(qualifiedExpression.name().name()) && call.arguments().isEmpty()) {
          ctx.addIssue(inExpr, MESSAGE);
        }
      }
    }
  }
}
