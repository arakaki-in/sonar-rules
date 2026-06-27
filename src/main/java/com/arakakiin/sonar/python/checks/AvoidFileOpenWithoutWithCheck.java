/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.WithItem;

@Rule(key = AvoidFileOpenWithoutWithCheck.RULE_KEY)
public class AvoidFileOpenWithoutWithCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidFileOpenWithoutWith";
  private static final String MESSAGE = "Use a 'with' statement context manager to open files.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    if (isOpenCall(callExpression)) {
      if (!isManagedByWith(callExpression)) {
        ctx.addIssue(callExpression, MESSAGE);
      }
    }
  }

  private static boolean isOpenCall(CallExpression callExpression) {
    Expression callee = callExpression.callee();
    if (callee.is(Tree.Kind.NAME)) {
      Name name = (Name) callee;
      if ("open".equals(name.name())) {
        return true;
      }
    } else if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      if ("open".equals(qualExpr.name().name())) {
        Expression qualifier = qualExpr.qualifier();
        if (qualifier.is(Tree.Kind.NAME) && "io".equals(((Name) qualifier).name())) {
          return true;
        }
      }
    }

    Symbol symbol = callExpression.calleeSymbol();
    if (symbol != null) {
      String fqn = symbol.fullyQualifiedName();
      if ("open".equals(fqn) || "io.open".equals(fqn) || "builtins.open".equals(fqn)) {
        return true;
      }
    }

    return false;
  }

  private static boolean isManagedByWith(CallExpression callExpression) {
    Tree current = callExpression;
    Tree parent = current.parent();
    while (parent != null) {
      if (parent.is(Tree.Kind.WITH_ITEM)) {
        WithItem withItem = (WithItem) parent;
        if (current == withItem.test()) {
          return true;
        }
      }
      current = parent;
      parent = current.parent();
    }
    return false;
  }
}
