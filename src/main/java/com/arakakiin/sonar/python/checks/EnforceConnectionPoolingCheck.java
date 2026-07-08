/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Symbol;
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
    Symbol symbol = callExpression.calleeSymbol();
    String fqn = symbol != null ? symbol.fullyQualifiedName() : null;

    if (isDbConnectCall(callExpression, fqn)) {
      ctx.addIssue(
          callExpression,
          "Enforce connection pooling. Avoid creating raw database connections directly. Use"
              + " connection pools (e.g. SQLAlchemy engines or database-specific pool managers)"
              + " instead.");
    } else if (isHttpRawCall(callExpression, fqn) && TreeInspections.isInsideLoop(callExpression)) {
      ctx.addIssue(
          callExpression,
          "Avoid making raw HTTP requests inside a loop. Reuse TCP connections by using a"
              + " 'requests.Session()' instance instead.");
    }
  }

  private static boolean isDbConnectCall(CallExpression callExpression, String fqn) {
    if (fqn != null && DB_CONNECT_FUNCTIONS.contains(fqn)) {
      return true;
    }
    Expression callee = callExpression.callee();
    if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      String name = qualExpr.name().name();
      if ("connect".equals(name)) {
        Expression qualifier = qualExpr.qualifier();
        String qualifierStr = TreeInspections.resolveFullyQualifiedName(qualifier);
        if (qualifierStr != null
            && ("sqlite3".equals(qualifierStr)
                || "psycopg2".equals(qualifierStr)
                || "pymysql".equals(qualifierStr)
                || "psycopg".equals(qualifierStr)
                || qualifierStr.endsWith("mysql.connector"))) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isHttpRawCall(CallExpression callExpression, String fqn) {
    if (fqn != null) {
      if (fqn.startsWith("requests.api.")) {
        return true;
      }
      if (HTTP_RAW_CALLS.contains(fqn)) {
        return true;
      }
    }
    Expression callee = callExpression.callee();
    if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      String name = qualExpr.name().name();
      if (Set.of("get", "post", "put", "delete", "patch", "request").contains(name)) {
        Expression qualifier = qualExpr.qualifier();
        if (qualifier.is(Tree.Kind.NAME)) {
          String qualName = ((Name) qualifier).name();
          if ("requests".equals(qualName)) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
