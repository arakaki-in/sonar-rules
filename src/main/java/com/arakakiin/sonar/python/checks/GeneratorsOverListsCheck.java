/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = GeneratorsOverListsCheck.RULE_KEY)
public class GeneratorsOverListsCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "GeneratorsOverLists";
  private static final String MESSAGE =
      "Use generator expressions instead of list comprehensions when passing iterables to functions"
          + " like 'sum()', 'any()', 'all()', 'min()', 'max()', or 'join()'. This avoids"
          + " unnecessary memory allocation for list creation.";

  private static final Set<String> ITERABLE_CONSUMING_FUNCTIONS =
      Set.of("sum", "any", "all", "min", "max");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    String methodName = CallMatcher.getMethodName(callExpression);

    boolean targetFunction =
        ITERABLE_CONSUMING_FUNCTIONS.contains(methodName) || "join".equals(methodName);

    if (targetFunction && !callExpression.arguments().isEmpty()) {
      Argument firstArg = callExpression.arguments().get(0);
      if (firstArg instanceof RegularArgument regArg) {
        Expression expr = regArg.expression();
        if (expr.is(Tree.Kind.LIST_COMPREHENSION)) {
          ctx.addIssue(callExpression, MESSAGE);
        }
      }
    }
  }
}
