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

@Rule(key = ZeroNPlusOneQueriesCheck.RULE_KEY)
public class ZeroNPlusOneQueriesCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "ZeroNPlusOneQueries";
  private static final String MESSAGE =
      "Avoid executing database queries inside a loop (N+1 query problem). Use join fetching or"
          + " prefetching instead.";

  private static final Set<String> QUERY_METHODS =
      Set.of(
          "execute", "query", "filter", "filter_by", "all", "first", "exclude", "delete", "update");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    if (TreeInspections.isInsideLoop(callExpression)
        && isDbQueryCall(callExpression)
        && !isNestedQueryCall(callExpression)) {
      ctx.addIssue(callExpression, MESSAGE);
    }
  }

  private static boolean isNestedQueryCall(CallExpression call) {
    Tree parent = call.parent();
    if (parent instanceof QualifiedExpression qual) {
      Tree grandParent = qual.parent();
      if (grandParent instanceof CallExpression parentCall) {
        return isDbQueryCall(parentCall);
      }
    }
    return false;
  }

  private static boolean isDbQueryCall(CallExpression callExpression) {
    String methodName = CallMatcher.getMethodName(callExpression);
    if ("get".equals(methodName)) {
      String qualifier = CallMatcher.getQualifierName(callExpression);
      if (qualifier != null) {
        return qualifier.contains("db")
            || qualifier.contains("session")
            || qualifier.contains("query")
            || qualifier.contains("repo");
      }
      return false;
    }
    if (methodName != null && QUERY_METHODS.contains(methodName)) {
      return true;
    }
    return "execute".equals(methodName) || "query".equals(methodName);
  }
}
