/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = FastJsonParsingCheck.RULE_KEY)
public class FastJsonParsingCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "FastJsonParsing";
  private static final String MESSAGE = "Use faster JSON parsing libraries like 'orjson' or 'ujson' instead of the standard 'json' library for high-performance JSON operations.";

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
      if ("loads".equals(methodName) || "dumps".equals(methodName)) {
        Expression qualifier = qualExpr.qualifier();
        if (qualifier.is(Tree.Kind.NAME)) {
          String qualName = ((Name) qualifier).name();
          if ("json".equals(qualName)) {
            ctx.addIssue(callExpression, MESSAGE);
          }
        }
      }
    }
  }
}
