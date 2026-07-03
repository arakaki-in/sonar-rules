/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = PreferIsinstanceOverTypeEqualityCheck.RULE_KEY)
public class PreferIsinstanceOverTypeEqualityCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "PreferIsinstanceOverTypeEquality";
  private static final String MESSAGE =
      "Use 'isinstance(x, SomeType)' instead of 'type(x) == SomeType' or 'type(x) is SomeType'."
          + " isinstance() supports inheritance and is the Pythonic way to check types.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.COMPARISON, this::checkComparison);
    context.registerSyntaxNodeConsumer(Tree.Kind.IS, this::checkIdentity);
  }

  private void checkComparison(SubscriptionContext ctx) {
    BinaryExpression expr = (BinaryExpression) ctx.syntaxNode();
    String op = expr.operator().value();
    if (("==".equals(op) || "!=".equals(op))
        && (isTypeCall(expr.leftOperand()) || isTypeCall(expr.rightOperand()))) {
      ctx.addIssue(expr, MESSAGE);
    }
  }

  private void checkIdentity(SubscriptionContext ctx) {
    IsExpression expr = (IsExpression) ctx.syntaxNode();
    if (isTypeCall(expr.leftOperand()) || isTypeCall(expr.rightOperand())) {
      ctx.addIssue(expr, MESSAGE);
    }
  }

  private static boolean isTypeCall(Expression expr) {
    if (expr instanceof CallExpression call) {
      Expression callee = call.callee();
      if (callee instanceof Name name && "type".equals(name.name())) {
        return !call.arguments().isEmpty();
      }
    }
    return false;
  }
}
