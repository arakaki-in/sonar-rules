/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = PreferFStringOverFormatCheck.RULE_KEY)
public class PreferFStringOverFormatCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "PreferFStringOverFormat";
  private static final String MESSAGE =
      "Prefer f-strings over '.format()' or '%' string formatting for readability and performance.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
    context.registerSyntaxNodeConsumer(Tree.Kind.MODULO, this::checkModulo);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression call = (CallExpression) ctx.syntaxNode();
    Expression callee = call.callee();
    if (callee instanceof QualifiedExpression qualifiedExpression) {
      if ("format".equals(qualifiedExpression.name().name())) {
        Expression receiver = qualifiedExpression.qualifier();
        if (receiver instanceof StringLiteral) {
          ctx.addIssue(call, MESSAGE);
        }
      }
    }
  }

  private void checkModulo(SubscriptionContext ctx) {
    BinaryExpression modulo = (BinaryExpression) ctx.syntaxNode();
    if (modulo.leftOperand() instanceof StringLiteral) {
      ctx.addIssue(modulo, MESSAGE);
    }
  }
}
