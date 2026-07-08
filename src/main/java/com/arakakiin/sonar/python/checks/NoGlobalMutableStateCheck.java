/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = NoGlobalMutableStateCheck.RULE_KEY)
public class NoGlobalMutableStateCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "NoGlobalMutableState";
  private static final String GLOBAL_KW_MESSAGE =
      "Avoid using the 'global' keyword. Global mutable state is not thread-safe.";
  private static final String MUTABLE_DECL_MESSAGE =
      "Avoid declaring mutable global state at the module level. Use immutable structures or"
          + " local/thread-safe scopes instead.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.GLOBAL_STMT, this::checkGlobalStatement);
    context.registerSyntaxNodeConsumer(Tree.Kind.ASSIGNMENT_STMT, this::checkAssignmentStatement);
    context.registerSyntaxNodeConsumer(
        Tree.Kind.ANNOTATED_ASSIGNMENT, this::checkAnnotatedAssignment);
  }

  private void checkGlobalStatement(SubscriptionContext ctx) {
    ctx.addIssue(ctx.syntaxNode(), GLOBAL_KW_MESSAGE);
  }

  private void checkAssignmentStatement(SubscriptionContext ctx) {
    AssignmentStatement assignment = (AssignmentStatement) ctx.syntaxNode();
    if (TreeInspections.isAtModuleLevel(assignment)) {
      Expression rhs = assignment.assignedValue();
      if (rhs != null && isMutableExpression(rhs)) {
        ctx.addIssue(assignment, MUTABLE_DECL_MESSAGE);
      }
    }
  }

  private void checkAnnotatedAssignment(SubscriptionContext ctx) {
    AnnotatedAssignment assignment = (AnnotatedAssignment) ctx.syntaxNode();
    if (TreeInspections.isAtModuleLevel(assignment)) {
      Expression rhs = assignment.assignedValue();
      if (rhs != null && isMutableExpression(rhs)) {
        ctx.addIssue(assignment, MUTABLE_DECL_MESSAGE);
      }
    }
  }

  private static boolean isMutableExpression(Expression expr) {
    if (expr.is(Tree.Kind.LIST_LITERAL)
        || expr.is(Tree.Kind.DICTIONARY_LITERAL)
        || expr.is(Tree.Kind.SET_LITERAL)) {
      return true;
    }
    if (expr.is(Tree.Kind.TUPLE)) {
      Tuple tuple = (Tuple) expr;
      for (Expression element : tuple.elements()) {
        if (isMutableExpression(element)) {
          return true;
        }
      }
      return false;
    }
    if (expr.is(Tree.Kind.CALL_EXPR)) {
      CallExpression call = (CallExpression) expr;
      String fqn = CallMatcher.getCalleeFqn(call);
      Symbol symbol = call.calleeSymbol();
      if (symbol != null && symbol.is(Symbol.Kind.CLASS)) {
        return true;
      }

      String name = CallMatcher.getMethodName(call);

      if (fqn != null) {
        if ("contextvars.ContextVar".equals(fqn) || "logging.getLogger".equals(fqn)) {
          return false;
        }
      }
      if (name != null) {
        if ("ContextVar".equals(name) || "getLogger".equals(name)) {
          return false;
        }
        if ("list".equals(name)
            || "dict".equals(name)
            || "set".equals(name)
            || "defaultdict".equals(name)) {
          return true;
        }
        if (!name.isEmpty() && Character.isUpperCase(name.charAt(0))) {
          return true;
        }
      }
      return false;
    }
    return false;
  }
}
