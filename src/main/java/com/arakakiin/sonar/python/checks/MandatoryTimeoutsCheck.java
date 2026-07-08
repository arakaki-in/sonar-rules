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

@Rule(key = MandatoryTimeoutsCheck.RULE_KEY)
public class MandatoryTimeoutsCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "MandatoryTimeouts";
  private static final String MESSAGE =
      "Configure an explicit timeout for all network requests to avoid hanging indefinitely.";

  private static final Set<String> HTTP_METHODS =
      Set.of("get", "post", "put", "delete", "patch", "request");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    if (isHttpRequestCall(callExpression)) {
      if (!hasTimeoutArgument(callExpression)) {
        ctx.addIssue(callExpression, MESSAGE);
      }
    }
  }

  private static boolean isHttpRequestCall(CallExpression callExpression) {
    Symbol symbol = callExpression.calleeSymbol();
    if (symbol != null) {
      String fqn = symbol.fullyQualifiedName();
      if (fqn != null) {
        if (fqn.startsWith("requests.api.")
            || fqn.startsWith("requests.sessions.Session.")
            || "urllib.request.urlopen".equals(fqn)) {
          return true;
        }
      }
    }

    Expression callee = callExpression.callee();
    if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      String methodName = qualExpr.name().name();
      if (HTTP_METHODS.contains(methodName)) {
        Expression qualifier = qualExpr.qualifier();
        if (qualifier.is(Tree.Kind.NAME)) {
          String qualName = ((Name) qualifier).name();
          return "requests".equals(qualName)
              || "session".equals(qualName)
              || qualName.contains("client");
        }
      }
    } else if (callee.is(Tree.Kind.NAME)) {
      String name = ((Name) callee).name();
      if ("urlopen".equals(name)) {
        return true;
      }
    }
    return false;
  }

  private static boolean hasTimeoutArgument(CallExpression callExpression) {
    for (Argument argument : callExpression.arguments()) {
      if (argument instanceof RegularArgument regArg) {
        Expression keyword = regArg.keywordArgument();
        if (keyword != null && keyword.is(Tree.Kind.NAME)) {
          String argName = ((Name) keyword).name();
          if ("timeout".equals(argName)) {
            Expression value = regArg.expression();
            if (TreeInspections.isNoneLiteral(value)) {
              return false; // timeout=None is invalid
            }
            return true;
          }
        }
      }
    }
    return false;
  }
}
