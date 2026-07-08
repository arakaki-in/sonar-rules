/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidSyncIoInAsyncCheck.RULE_KEY)
public class AvoidSyncIoInAsyncCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidSyncIoInAsync";
  private static final String MESSAGE =
      "Avoid blocking/synchronous operations inside async functions. Use async counterparts"
          + " instead.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.FUNCDEF, this::checkFunctionDefinition);
  }

  private void checkFunctionDefinition(SubscriptionContext ctx) {
    FunctionDef funcDef = (FunctionDef) ctx.syntaxNode();
    if (funcDef.asyncKeyword() != null) {
      funcDef.accept(new CallVisitor(ctx));
    }
  }

  private static class CallVisitor extends BaseTreeVisitor {
    private final SubscriptionContext ctx;

    CallVisitor(SubscriptionContext ctx) {
      this.ctx = ctx;
    }

    @Override
    public void visitCallExpression(CallExpression callExpression) {
      Expression callee = callExpression.callee();
      if (callee instanceof Name name) {
        String nameStr = name.name();
        if ("open".equals(nameStr)) {
          ctx.addIssue(callExpression, MESSAGE);
        }
      } else if (callee instanceof QualifiedExpression qualifiedExpression) {
        String fullName = getQualifiedName(qualifiedExpression);
        if ("time.sleep".equals(fullName)
            || "requests.get".equals(fullName)
            || "requests.post".equals(fullName)
            || "requests.put".equals(fullName)
            || "requests.delete".equals(fullName)
            || "requests.patch".equals(fullName)
            || "requests.head".equals(fullName)
            || "requests.options".equals(fullName)
            || "requests.request".equals(fullName)
            || fullName.startsWith("urllib.request.")) {
          ctx.addIssue(callExpression, MESSAGE);
        }
      }
      super.visitCallExpression(callExpression);
    }

    private String getQualifiedName(QualifiedExpression qualifiedExpression) {
      return TreeInspections.resolveFullyQualifiedName(qualifiedExpression);
    }
  }
}
