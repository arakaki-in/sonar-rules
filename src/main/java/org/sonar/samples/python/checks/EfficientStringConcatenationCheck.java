/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Rule(key = EfficientStringConcatenationCheck.RULE_KEY)
public class EfficientStringConcatenationCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "EfficientStringConcatenation";
  private static final String MESSAGE = "Avoid repeated string concatenation using '+' or '+=' inside a loop. Use list appending and ''.join() for efficient string construction.";

  private static final Set<String> STRING_VAR_KEYWORDS = Set.of(
      "str", "text", "msg", "body", "html", "xml", "line", "buf", "output", "query", "sql", "content", "string"
  );

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.COMPOUND_ASSIGNMENT, this::checkCompoundAssignment);
    context.registerSyntaxNodeConsumer(Tree.Kind.ASSIGNMENT_STMT, this::checkAssignment);
  }

  private void checkCompoundAssignment(SubscriptionContext ctx) {
    CompoundAssignmentStatement stmt = (CompoundAssignmentStatement) ctx.syntaxNode();
    if (isInsideLoop(stmt) && "+=".equals(stmt.compoundAssignmentToken().value())) {
      Expression lhs = stmt.lhsExpression();
      Expression rhs = stmt.rhsExpression();
      if (isStringExpression(lhs) || isStringExpression(rhs)) {
        ctx.addIssue(stmt, MESSAGE);
      }
    }
  }

  private void checkAssignment(SubscriptionContext ctx) {
    AssignmentStatement stmt = (AssignmentStatement) ctx.syntaxNode();
    if (isInsideLoop(stmt)) {
      Expression assigned = stmt.assignedValue();
      if (assigned instanceof BinaryExpression binExpr && "+".equals(binExpr.operator().value())) {
        List<Name> lhsNames = new ArrayList<>();
        for (Expression lhs : stmt.lhsExpressions()) {
          getNames(lhs, lhsNames);
        }
        for (Name name : lhsNames) {
          String nameStr = name.name();
          Expression left = binExpr.leftOperand();
          Expression right = binExpr.rightOperand();
          boolean leftMatch = (left instanceof Name && nameStr.equals(((Name) left).name()));
          boolean rightMatch = (right instanceof Name && nameStr.equals(((Name) right).name()));
          if (leftMatch || rightMatch) {
            if (isStringExpression(name) || isStringExpression(left) || isStringExpression(right)) {
              ctx.addIssue(stmt, MESSAGE);
              return;
            }
          }
        }
      }
    }
  }

  private static void getNames(Expression expr, List<Name> names) {
    if (expr == null) {
      return;
    }
    if (expr instanceof Name name) {
      names.add(name);
    } else if (expr instanceof ExpressionList exprList) {
      for (Expression child : exprList.expressions()) {
        getNames(child, names);
      }
    } else if (expr instanceof Tuple tuple) {
      for (Expression child : tuple.elements()) {
        getNames(child, names);
      }
    }
  }

  private static boolean isStringExpression(Expression expr) {
    if (expr == null) {
      return false;
    }
    if (expr.is(Tree.Kind.STRING_LITERAL)) {
      return true;
    }
    if (expr.is(Tree.Kind.NAME)) {
      String name = ((Name) expr).name().toLowerCase();
      for (String keyword : STRING_VAR_KEYWORDS) {
        if (name.contains(keyword)) {
          return true;
        }
      }
    }
    if (expr instanceof BinaryExpression binExpr) {
      return isStringExpression(binExpr.leftOperand()) || isStringExpression(binExpr.rightOperand());
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
