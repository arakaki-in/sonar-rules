/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = DequeOverListInsertCheck.RULE_KEY)
public class DequeOverListInsertCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "DequeOverListInsert";
  private static final String MESSAGE =
      "Use 'collections.deque' instead of 'list' for FIFO queue operations or when"
          + " inserting/popping at index 0.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression call = (CallExpression) ctx.syntaxNode();
    Expression callee = call.callee();
    if (callee instanceof QualifiedExpression qualifiedExpression) {
      String methodName = qualifiedExpression.name().name();
      if ("insert".equals(methodName)) {
        if (!call.arguments().isEmpty()
            && call.arguments().get(0) instanceof RegularArgument regArg) {
          if (regArg.expression() instanceof NumericLiteral literal
              && "0".equals(literal.valueAsString())) {
            ctx.addIssue(call, MESSAGE);
          }
        }
      } else if ("pop".equals(methodName)) {
        if (!call.arguments().isEmpty()
            && call.arguments().get(0) instanceof RegularArgument regArg) {
          if (regArg.expression() instanceof NumericLiteral literal
              && "0".equals(literal.valueAsString())) {
            ctx.addIssue(call, MESSAGE);
          }
        }
      }
    }
  }
}
