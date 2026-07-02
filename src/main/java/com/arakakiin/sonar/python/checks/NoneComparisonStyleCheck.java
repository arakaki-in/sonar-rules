/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = NoneComparisonStyleCheck.RULE_KEY)
public class NoneComparisonStyleCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "NoneComparisonStyle";
  private static final String MESSAGE =
      "Use 'is None' or 'is not None' for comparisons with None instead of '==' or '!='.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.COMPARISON, this::checkComparison);
  }

  private void checkComparison(SubscriptionContext ctx) {
    BinaryExpression comparison = (BinaryExpression) ctx.syntaxNode();
    String operator = comparison.operator().value();
    if ("==".equals(operator) || "!=".equals(operator)) {
      if (isNoneLiteral(comparison.leftOperand()) || isNoneLiteral(comparison.rightOperand())) {
        ctx.addIssue(comparison, MESSAGE);
      }
    }
  }

  private boolean isNoneLiteral(Expression expression) {
    if (expression.is(Tree.Kind.NONE)) {
      return true;
    }
    if (expression instanceof Name name) {
      return "None".equals(name.name());
    }
    return false;
  }
}
