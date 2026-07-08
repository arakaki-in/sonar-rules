/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import java.util.HashSet;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = ThreadLocalUsageCheck.RULE_KEY)
public class ThreadLocalUsageCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "ThreadLocalUsage";
  private static final String MESSAGE =
      "Use 'contextvars.ContextVar' instead of 'threading.local' to ensure async-safe and"
          + " thread-safe context management.";

  private final Set<String> threadingLocalSubclasses = new HashSet<>();

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CLASSDEF, this::checkClassDef);
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkClassDef(SubscriptionContext ctx) {
    ClassDef classDef = (ClassDef) ctx.syntaxNode();
    ArgList args = classDef.args();
    if (args != null) {
      for (Argument arg : args.arguments()) {
        if (arg instanceof RegularArgument regArg) {
          String baseFqn = TreeInspections.resolveFullyQualifiedName(regArg.expression());
          if ("threading.local".equals(baseFqn)) {
            threadingLocalSubclasses.add(classDef.name().name());
            break;
          }
        }
      }
    }
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    if (isThreadingLocalCall(callExpression)) {
      ctx.addIssue(callExpression, MESSAGE);
    }
  }

  private boolean isThreadingLocalCall(CallExpression callExpression) {
    String fqn = CallMatcher.getCalleeFqn(callExpression);
    if ("threading.local".equals(fqn)) {
      return true;
    }
    String methodName = CallMatcher.getMethodName(callExpression);
    if ("local".equals(methodName)) {
      String qualifier = CallMatcher.getQualifierName(callExpression);
      if (qualifier == null || "threading".equals(qualifier)) {
        return true;
      }
    }
    return methodName != null && threadingLocalSubclasses.contains(methodName);
  }
}
