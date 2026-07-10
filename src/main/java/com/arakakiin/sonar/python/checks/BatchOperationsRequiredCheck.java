/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = BatchOperationsRequiredCheck.RULE_KEY)
public class BatchOperationsRequiredCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "BatchOperationsRequired";
  private static final String MESSAGE =
      "Use batch operations (like 'session.add_all()', bulk inserts, or 'cursor.executemany()')"
          + " instead of single-row database operations inside a loop.";

  private static final Pattern BATCHABLE_SQL_PATTERN =
      Pattern.compile("(?i)\\b(insert|update|delete)\\b");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    if (TreeInspections.isInsideLoop(callExpression) && isBatchableDbOperation(callExpression)) {
      ctx.addIssue(callExpression, MESSAGE);
    }
  }

  private static boolean isBatchableDbOperation(CallExpression callExpression) {
    String methodName = CallMatcher.getMethodName(callExpression);
    if ("add".equals(methodName) || "delete".equals(methodName)) {
      String qualifier = CallMatcher.getQualifierName(callExpression);
      if (qualifier != null) {
        return qualifier.contains("session") || qualifier.contains("db");
      }
      return false;
    }
    if ("execute".equals(methodName)) {
      return hasBatchableSql(callExpression);
    }
    return false;
  }

  private static boolean hasBatchableSql(CallExpression callExpression) {
    if (callExpression.arguments().isEmpty()) {
      return false;
    }
    Argument firstArg = callExpression.arguments().get(0);
    if (firstArg instanceof RegularArgument regArg) {
      Expression expr = regArg.expression();
      if (expr.is(Tree.Kind.STRING_LITERAL)) {
        String value = TreeInspections.getStringValue((StringLiteral) expr);
        return value != null && BATCHABLE_SQL_PATTERN.matcher(value).find();
      }
    }
    return false;
  }

  // getStringValue delegated to TreeInspections.getStringValue
}
