/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = PreferSetMembershipCheck.RULE_KEY)
public class PreferSetMembershipCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "PreferSetMembership";
  private static final String MESSAGE =
      "Use a 'set' instead of a list or tuple for membership testing ('in') inside a loop."
          + " Lists and tuples have O(n) lookup; sets have O(1) average lookup.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.IN, this::checkInExpression);
  }

  private void checkInExpression(SubscriptionContext ctx) {
    InExpression inExpr = (InExpression) ctx.syntaxNode();
    if (!isInsideLoop(inExpr)) {
      return;
    }
    Expression right = inExpr.rightOperand();
    if (isListOrTupleLiteral(right)) {
      ctx.addIssue(inExpr, MESSAGE);
    }
  }

  private static boolean isListOrTupleLiteral(Expression expr) {
    if (expr.is(Tree.Kind.LIST_LITERAL)
        || expr.is(Tree.Kind.TUPLE)
        || expr.is(Tree.Kind.LIST_COMPREHENSION)) {
      return true;
    }
    return false;
  }

  private static boolean isInsideLoop(Tree tree) {
    Tree parent = tree.parent();
    while (parent != null) {
      if (parent.is(Tree.Kind.FOR_STMT) || parent.is(Tree.Kind.WHILE_STMT)) {
        return true;
      }
      parent = parent.parent();
    }
    return false;
  }
}
