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
    if (isInsideLoop(callExpression) && isBatchableDbOperation(callExpression)) {
      ctx.addIssue(callExpression, MESSAGE);
    }
  }

  private static boolean isBatchableDbOperation(CallExpression callExpression) {
    Expression callee = callExpression.callee();
    if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      String methodName = qualExpr.name().name();
      if ("add".equals(methodName) || "delete".equals(methodName)) {
        Expression qualifier = qualExpr.qualifier();
        if (qualifier.is(Tree.Kind.NAME)) {
          String qualName = ((Name) qualifier).name();
          return qualName.contains("session") || qualName.contains("db");
        }
        return false;
      }
      if ("execute".equals(methodName)) {
        return hasBatchableSql(callExpression);
      }
    } else if (callee.is(Tree.Kind.NAME)) {
      String name = ((Name) callee).name();
      if ("execute".equals(name)) {
        return hasBatchableSql(callExpression);
      }
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
        String value = getStringValue((StringLiteral) expr);
        return value != null && BATCHABLE_SQL_PATTERN.matcher(value).find();
      }
    }
    return false;
  }

  private static String getStringValue(StringLiteral stringLiteral) {
    StringBuilder sb = new StringBuilder();
    for (Object el : stringLiteral.stringElements()) {
      if (el instanceof StringElement element) {
        sb.append(element.trimmedQuotesValue());
      }
    }
    return sb.toString();
  }

  private static boolean isInsideLoop(Tree tree) {
    Tree parent = tree.parent();
    while (parent != null) {
      if (parent.is(Tree.Kind.FOR_STMT) || parent.is(Tree.Kind.WHILE_STMT)) {
        return true;
      }
      parent = parent.parent();
    }
    return false;
  }
}
