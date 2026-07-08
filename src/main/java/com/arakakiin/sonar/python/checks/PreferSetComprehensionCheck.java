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

@Rule(key = PreferSetComprehensionCheck.RULE_KEY)
public class PreferSetComprehensionCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "PreferSetComprehension";
  private static final String MESSAGE =
      "Use a set comprehension ('{...}') or dict comprehension ('{k: v ...}') instead of passing a"
          + " list comprehension to 'set()' or 'dict()'. This avoids creating an unnecessary"
          + " intermediate list.";

  private static final Set<String> COMPREHENSION_CONSTRUCTORS =
      Set.of("set", "dict", "tuple", "frozenset");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression call = (CallExpression) ctx.syntaxNode();
    Expression callee = call.callee();
    if (callee instanceof Name name
        && COMPREHENSION_CONSTRUCTORS.contains(name.name())
        && !call.arguments().isEmpty()
        && call.arguments().get(0) instanceof RegularArgument regArg) {
      Expression argExpr = regArg.expression();
      if (argExpr.is(Tree.Kind.LIST_COMPREHENSION)) {
        ctx.addIssue(call, MESSAGE);
      }
    }
  }
}
