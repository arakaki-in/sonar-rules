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

@Rule(key = DbLevelAggregationCheck.RULE_KEY)
public class DbLevelAggregationCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "DbLevelAggregation";
  private static final String MESSAGE =
      "Avoid database-to-memory aggregation. Perform aggregation at the database level using SQL"
          + " aggregate functions (e.g. COUNT, SUM, AVG) instead of loading all records into"
          + " Python.";

  private static final Set<String> AGGREGATE_FUNCTIONS = Set.of("sum", "len", "min", "max");
  private static final Set<String> QUERY_METHODS =
      Set.of("all", "filter", "filter_by", "execute", "fetchall");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    Expression callee = callExpression.callee();
    if (callee.is(Tree.Kind.NAME)) {
      String funcName = ((Name) callee).name();
      if (AGGREGATE_FUNCTIONS.contains(funcName) && !callExpression.arguments().isEmpty()) {
        Argument firstArg = callExpression.arguments().get(0);
        if (firstArg instanceof RegularArgument regArg) {
          Expression expr = regArg.expression();
          if (isQueryCall(expr) || usesQueryInComprehension(expr)) {
            ctx.addIssue(callExpression, MESSAGE);
          }
        }
      }
    }
  }

  private static boolean isQueryCall(Expression expr) {
    if (expr instanceof CallExpression call) {
      String methodName = CallMatcher.getMethodName(call);
      return methodName != null && QUERY_METHODS.contains(methodName);
    }
    return false;
  }

  private static boolean usesQueryInComprehension(Expression expr) {
    if (expr instanceof ComprehensionExpression comp) {
      ComprehensionClause clause = comp.comprehensionFor();
      while (clause != null) {
        if (clause instanceof ComprehensionFor compFor) {
          if (isQueryCall(compFor.iterable())) {
            return true;
          }
          clause = compFor.nestedClause();
        } else {
          break;
        }
      }
    }
    return false;
  }
}
