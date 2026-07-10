/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidRangeLenIterationCheck.RULE_KEY)
public class AvoidRangeLenIterationCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidRangeLenIteration";
  private static final String MESSAGE =
      "Avoid 'for i in range(len(seq))'. Use 'for item in seq', 'enumerate(seq)', or 'zip(seq1,"
          + " seq2)' for better readability and performance.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.FOR_STMT, this::checkForStatement);
  }

  private void checkForStatement(SubscriptionContext ctx) {
    ForStatement forStmt = (ForStatement) ctx.syntaxNode();
    if (forStmt.testExpressions().isEmpty()) {
      return;
    }
    Expression iterable = forStmt.testExpressions().get(0);
    if (iterable instanceof CallExpression call && isRangeLenCall(call)) {
      ctx.addIssue(call, MESSAGE);
    }
  }

  private static boolean isRangeLenCall(CallExpression call) {
    if (!(call.callee() instanceof Name name) || !"range".equals(name.name())) {
      return false;
    }
    if (call.arguments().isEmpty()
        || !(call.arguments().get(0) instanceof RegularArgument regArg)) {
      return false;
    }
    if (!(regArg.expression() instanceof CallExpression innerCall)) {
      return false;
    }
    return innerCall.callee() instanceof Name innerName && "len".equals(innerName.name());
  }
}
