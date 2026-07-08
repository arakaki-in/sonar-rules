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
    String fqn = CallMatcher.getCalleeFqn(callExpression);
    if (fqn != null) {
      if (fqn.startsWith("requests.api.")
          || fqn.startsWith("requests.sessions.Session.")
          || "urllib.request.urlopen".equals(fqn)) {
        return true;
      }
    }

    String methodName = CallMatcher.getMethodName(callExpression);
    if (methodName != null && HTTP_METHODS.contains(methodName)) {
      String qualifier = CallMatcher.getQualifierName(callExpression);
      if (qualifier != null) {
        return "requests".equals(qualifier)
            || "session".equals(qualifier)
            || qualifier.contains("client");
      }
    }
    return "urlopen".equals(methodName);
  }

  private static boolean hasTimeoutArgument(CallExpression callExpression) {
    for (Argument argument : callExpression.arguments()) {
      if (argument instanceof RegularArgument regArg) {
        Expression keyword = regArg.keywordArgument();
        if (keyword != null && keyword.is(Tree.Kind.NAME)) {
          String argName = ((Name) keyword).name();
          if ("timeout".equals(argName)) {
            return !TreeInspections.isNoneLiteral(regArg.expression());
          }
        }
      }
    }

    // Check positional timeout for urlopen (3rd positional argument is timeout)
    if (isUrlopenCall(callExpression)) {
      int positionalCount = 0;
      for (Argument argument : callExpression.arguments()) {
        if (argument instanceof RegularArgument regArg && regArg.keywordArgument() == null) {
          positionalCount++;
          if (positionalCount == 3) {
            return !TreeInspections.isNoneLiteral(regArg.expression());
          }
        }
      }
    }

    return false;
  }

  private static boolean isUrlopenCall(CallExpression callExpression) {
    String fqn = CallMatcher.getCalleeFqn(callExpression);
    if (fqn != null && "urllib.request.urlopen".equals(fqn)) {
      return true;
    }
    return "urlopen".equals(CallMatcher.getMethodName(callExpression));
  }
}
