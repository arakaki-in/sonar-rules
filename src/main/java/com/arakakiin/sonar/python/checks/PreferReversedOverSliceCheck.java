/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = PreferReversedOverSliceCheck.RULE_KEY)
public class PreferReversedOverSliceCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "PreferReversedOverSlice";
  private static final String MESSAGE =
      "Use 'reversed(seq)' instead of 'seq[::-1]' for reverse iteration. reversed() returns an"
          + " iterator without creating a copy of the sequence.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.SLICE_EXPR, this::checkSliceExpression);
  }

  private void checkSliceExpression(SubscriptionContext ctx) {
    SliceExpression slice = (SliceExpression) ctx.syntaxNode();
    SliceList sliceList = slice.sliceList();
    if (sliceList == null || sliceList.slices().isEmpty()) {
      return;
    }
    Tree item = sliceList.slices().get(0);
    if (item instanceof SliceItem sliceItem) {
      Expression lower = sliceItem.lowerBound();
      Expression upper = sliceItem.upperBound();
      Expression step = sliceItem.stride();
      if (lower == null && upper == null && step instanceof UnaryExpression unary) {
        String op = unary.operator().value();
        if ("-".equals(op)
            && unary.expression() instanceof NumericLiteral num
            && "1".equals(num.valueAsString())) {
          ctx.addIssue(slice, MESSAGE);
        }
      }
    }
  }
}
