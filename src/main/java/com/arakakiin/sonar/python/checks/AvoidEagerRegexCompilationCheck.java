/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidEagerRegexCompilationCheck.RULE_KEY)
public class AvoidEagerRegexCompilationCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidEagerRegexCompilation";
  private static final String MESSAGE = "Avoid eager compilation of regular expressions using 're.compile()'. Use 're' module functions directly to leverage the built-in lazy compilation and caching, or defer compilation until needed.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    Expression callee = callExpression.callee();
    if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      String methodName = qualExpr.name().name();
      if ("compile".equals(methodName)) {
        Expression qualifier = qualExpr.qualifier();
        if (qualifier.is(Tree.Kind.NAME)) {
          String qualName = ((Name) qualifier).name();
          if ("re".equals(qualName)) {
            ctx.addIssue(callExpression, MESSAGE);
          }
        }
      }
    }
  }
}
