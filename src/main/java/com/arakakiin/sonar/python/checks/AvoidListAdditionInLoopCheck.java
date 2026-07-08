/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidListAdditionInLoopCheck.RULE_KEY)
public class AvoidListAdditionInLoopCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidListAdditionInLoop";
  private static final String MESSAGE =
      "Avoid creating a new list with '+' or '+= [x]' inside a loop. Use 'list.append(item)' or"
          + " 'list.extend(items)' for O(1) amortized additions. Repeated list addition creates a"
          + " new list each time, making the loop O(n^2).";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.PLUS, this::checkAddition);
    context.registerSyntaxNodeConsumer(Tree.Kind.COMPOUND_ASSIGNMENT, this::checkCompoundAssign);
  }

  private void checkAddition(SubscriptionContext ctx) {
    BinaryExpression expr = (BinaryExpression) ctx.syntaxNode();
    if (TreeInspections.isInsideLoop(expr)
        && "+".equals(expr.operator().value())
        && (isListExpression(expr.leftOperand()) || isListExpression(expr.rightOperand()))) {
      ctx.addIssue(expr, MESSAGE);
    }
  }

  private void checkCompoundAssign(SubscriptionContext ctx) {
    CompoundAssignmentStatement stmt = (CompoundAssignmentStatement) ctx.syntaxNode();
    if (TreeInspections.isInsideLoop(stmt) && "+=".equals(stmt.compoundAssignmentToken().value())) {
      if (stmt.rhsExpression() instanceof ListLiteral) {
        ctx.addIssue(stmt, MESSAGE);
      }
    }
  }

  private static boolean isListExpression(Expression expr) {
    if (expr.is(Tree.Kind.LIST_LITERAL)) {
      return true;
    }
    if (expr instanceof Name) {
      return true;
    }
    return false;
  }
}
