/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidTryExceptControlFlowCheck.RULE_KEY)
public class AvoidTryExceptControlFlowCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidTryExceptControlFlow";
  private static final String MESSAGE =
      "Avoid using try-except blocks for standard control flow (e.g. KeyError, AttributeError,"
          + " IndexError). Use explicit checks like 'dict.get()', 'hasattr()', or index length"
          + " validation instead.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.TRY_STMT, this::checkTryStatement);
  }

  private void checkTryStatement(SubscriptionContext ctx) {
    TryStatement tryStmt = (TryStatement) ctx.syntaxNode();
    for (ExceptClause exceptClause : tryStmt.exceptClauses()) {
      if (isControlFlowException(exceptClause)) {
        if (isControlFlowBody(tryStmt.body())) {
          ctx.addIssue(tryStmt.tryKeyword(), MESSAGE);
          return;
        }
      }
    }
  }

  private static boolean isControlFlowException(ExceptClause exceptClause) {
    Expression exception = exceptClause.exception();
    if (exception == null) {
      return false;
    }
    return isTargetException(exception);
  }

  private static boolean isTargetException(Expression expr) {
    if (expr.is(Tree.Kind.NAME)) {
      String name = ((Name) expr).name();
      return "KeyError".equals(name) || "AttributeError".equals(name) || "IndexError".equals(name);
    } else if (expr instanceof Tuple tuple) {
      for (Expression element : tuple.elements()) {
        if (isTargetException(element)) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isControlFlowBody(StatementList body) {
    if (body == null || body.statements().isEmpty()) {
      return false;
    }
    if (body.statements().size() > 2) {
      return false;
    }
    for (Statement stmt : body.statements()) {
      if (stmt instanceof ExpressionStatement exprStmt) {
        for (Expression expr : exprStmt.expressions()) {
          if (isControlFlowExpression(expr)) {
            return true;
          }
        }
      } else if (stmt instanceof AssignmentStatement assignStmt) {
        if (isControlFlowExpression(assignStmt.assignedValue())) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isControlFlowExpression(Expression expr) {
    if (expr == null) {
      return false;
    }
    return expr.is(Tree.Kind.SUBSCRIPTION) || expr.is(Tree.Kind.QUALIFIED_EXPR);
  }
}
