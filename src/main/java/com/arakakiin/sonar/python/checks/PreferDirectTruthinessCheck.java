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

@Rule(key = PreferDirectTruthinessCheck.RULE_KEY)
public class PreferDirectTruthinessCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "PreferDirectTruthiness";
  private static final String MESSAGE =
      "Use the truthiness of the collection directly (e.g. 'if seq:' or 'if not seq:') instead of"
          + " comparing 'len(seq) > 0' or 'len(seq) == 0'.";

  private static final Set<String> ZERO_COMPARATORS = Set.of(">", "==", "!=", "<=");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.COMPARISON, this::checkComparison);
  }

  private void checkComparison(SubscriptionContext ctx) {
    BinaryExpression expr = (BinaryExpression) ctx.syntaxNode();
    String op = expr.operator().value();
    if (!ZERO_COMPARATORS.contains(op)) {
      return;
    }
    Expression left = expr.leftOperand();
    Expression right = expr.rightOperand();
    boolean leftIsLenZero = isLenCall(left) && isZeroLiteral(right);
    boolean rightIsLenZero = isLenCall(right) && isZeroLiteral(left);
    if (leftIsLenZero || rightIsLenZero) {
      ctx.addIssue(expr, MESSAGE);
    }
  }

  private static boolean isLenCall(Expression expr) {
    if (expr instanceof CallExpression call) {
      Expression callee = call.callee();
      if (callee instanceof Name name && "len".equals(name.name())) {
        return !call.arguments().isEmpty();
      }
    }
    return false;
  }

  private static boolean isZeroLiteral(Expression expr) {
    if (expr instanceof NumericLiteral num) {
      return "0".equals(num.valueAsString());
    }
    return false;
  }
}
