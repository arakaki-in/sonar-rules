/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidMapLambdaCheck.RULE_KEY)
public class AvoidMapLambdaCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidMapLambda";
  private static final String MESSAGE =
      "Avoid using 'map(lambda ...)' or 'filter(lambda ...)'. Use list comprehensions or generator"
          + " expressions for better readability and performance.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression call = (CallExpression) ctx.syntaxNode();
    Expression callee = call.callee();
    if (callee instanceof Name name) {
      String funcName = name.name();
      if ("map".equals(funcName) || "filter".equals(funcName)) {
        if (!call.arguments().isEmpty()
            && call.arguments().get(0) instanceof RegularArgument regArg) {
          if (regArg.expression() instanceof LambdaExpression) {
            ctx.addIssue(call, MESSAGE);
          }
        }
      }
    }
  }
}
