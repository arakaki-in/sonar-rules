/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = ThreadLocalUsageCheck.RULE_KEY)
public class ThreadLocalUsageCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "ThreadLocalUsage";
  private static final String MESSAGE =
      "Use 'contextvars.ContextVar' instead of 'threading.local' to ensure async-safe and"
          + " thread-safe context management.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    if (isThreadingLocalCall(callExpression)) {
      ctx.addIssue(callExpression, MESSAGE);
    }
  }

  private static boolean isThreadingLocalCall(CallExpression callExpression) {
    Expression callee = callExpression.callee();

    // Check direct name local()
    if (callee.is(Tree.Kind.NAME)) {
      Name name = (Name) callee;
      if ("local".equals(name.name())) {
        Symbol symbol = callExpression.calleeSymbol();
        if (symbol == null || "threading.local".equals(symbol.fullyQualifiedName())) {
          return true;
        }
      }
    }
    // Check qualified threading.local()
    else if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      if ("local".equals(qualExpr.name().name())) {
        Expression qualifier = qualExpr.qualifier();
        if (qualifier.is(Tree.Kind.NAME) && "threading".equals(((Name) qualifier).name())) {
          return true;
        }
      }
    }

    Symbol symbol = callExpression.calleeSymbol();
    if (symbol != null) {
      String fqn = symbol.fullyQualifiedName();
      if ("threading.local".equals(fqn)) {
        return true;
      }
    }
    return false;
  }
}
