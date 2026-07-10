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

@Rule(key = EnforceConnectionPoolingCheck.RULE_KEY)
public class EnforceConnectionPoolingCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "EnforceConnectionPooling";

  private static final Set<String> DB_CONNECT_FUNCTIONS =
      Set.of(
          "psycopg2.connect",
          "sqlite3.connect",
          "pymysql.connect",
          "mysql.connector.connect",
          "psycopg.connect");

  private static final Set<String> HTTP_RAW_CALLS =
      Set.of(
          "requests.get",
          "requests.post",
          "requests.put",
          "requests.delete",
          "requests.patch",
          "requests.request");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();

    if (isDbConnectCall(callExpression)) {
      ctx.addIssue(
          callExpression,
          "Enforce connection pooling. Avoid creating raw database connections directly. Use"
              + " connection pools (e.g. SQLAlchemy engines or database-specific pool managers)"
              + " instead.");
    } else if (isHttpRawCall(callExpression) && TreeInspections.isInsideLoop(callExpression)) {
      ctx.addIssue(
          callExpression,
          "Avoid making raw HTTP requests inside a loop. Reuse TCP connections by using a"
              + " 'requests.Session()' instance instead.");
    }
  }

  private static boolean isDbConnectCall(CallExpression callExpression) {
    String fqn = CallMatcher.getCalleeFqn(callExpression);
    if (fqn != null && DB_CONNECT_FUNCTIONS.contains(fqn)) {
      return true;
    }
    if ("connect".equals(CallMatcher.getMethodName(callExpression))) {
      String qualifier = CallMatcher.getQualifierName(callExpression);
      if (qualifier != null
          && ("sqlite3".equals(qualifier)
              || "psycopg2".equals(qualifier)
              || "pymysql".equals(qualifier)
              || "psycopg".equals(qualifier)
              || qualifier.endsWith("mysql.connector"))) {
        return true;
      }
    }
    return false;
  }

  private static boolean isHttpRawCall(CallExpression callExpression) {
    String fqn = CallMatcher.getCalleeFqn(callExpression);
    if (fqn != null && (fqn.startsWith("requests.api.") || HTTP_RAW_CALLS.contains(fqn))) {
      return true;
    }
    if ("requests".equals(CallMatcher.getQualifierName(callExpression))) {
      String method = CallMatcher.getMethodName(callExpression);
      return method != null
          && Set.of("get", "post", "put", "delete", "patch", "request").contains(method);
    }
    return false;
  }
}
