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
import org.sonar.plugins.python.api.symbols.Symbol;
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
    Expression callee = callExpression.callee();

    // Check direct name: local()
    if (callee.is(Tree.Kind.NAME)) {
      Name name = (Name) callee;
      if ("local".equals(name.name())) {
        Symbol symbol = callExpression.calleeSymbol();
        if (symbol == null || "threading.local".equals(symbol.fullyQualifiedName())) {
          return true;
        }
      }
      // Check if callee is a known threading.local subclass
      if (threadingLocalSubclasses.contains(name.name())) {
        return true;
      }
    }
    // Check qualified: threading.local()
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
