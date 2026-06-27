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
import java.util.List;

@Rule(key = NoGlobalMutableStateCheck.RULE_KEY)
public class NoGlobalMutableStateCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "NoGlobalMutableState";
  private static final String GLOBAL_KW_MESSAGE = "Avoid using the 'global' keyword. Global mutable state is not thread-safe.";
  private static final String MUTABLE_DECL_MESSAGE = "Avoid declaring mutable global state at the module level. Use immutable structures or local/thread-safe scopes instead.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.GLOBAL_STMT, this::checkGlobalStatement);
    context.registerSyntaxNodeConsumer(Tree.Kind.ASSIGNMENT_STMT, this::checkAssignmentStatement);
  }

  private void checkGlobalStatement(SubscriptionContext ctx) {
    ctx.addIssue(ctx.syntaxNode(), GLOBAL_KW_MESSAGE);
  }

  private void checkAssignmentStatement(SubscriptionContext ctx) {
    AssignmentStatement assignment = (AssignmentStatement) ctx.syntaxNode();
    if (isAtModuleLevel(assignment)) {
      Expression rhs = assignment.assignedValue();
      if (rhs != null && isMutableExpression(rhs)) {
        ctx.addIssue(assignment, MUTABLE_DECL_MESSAGE);
      }
    }
  }

  private static boolean isAtModuleLevel(Tree tree) {
    Tree parent = tree.parent();
    while (parent != null) {
      if (parent.is(Tree.Kind.FUNCDEF) || parent.is(Tree.Kind.CLASSDEF)) {
        return false;
      }
      parent = parent.parent();
    }
    return true;
  }

  private static boolean isMutableExpression(Expression expr) {
    if (expr.is(Tree.Kind.LIST_LITERAL) || 
        expr.is(Tree.Kind.DICTIONARY_LITERAL) || 
        expr.is(Tree.Kind.SET_LITERAL)) {
      return true;
    }
    if (expr.is(Tree.Kind.CALL_EXPR)) {
      CallExpression call = (CallExpression) expr;
      Expression callee = call.callee();
      
      String name = null;
      String fqn = null;
      Symbol symbol = call.calleeSymbol();
      if (symbol != null) {
        fqn = symbol.fullyQualifiedName();
      }
      
      if (callee.is(Tree.Kind.NAME)) {
        name = ((Name) callee).name();
      } else if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
        name = ((QualifiedExpression) callee).name().name();
      }
      
      if (fqn != null) {
        if (fqn.equals("contextvars.ContextVar") || fqn.equals("logging.getLogger")) {
          return false;
        }
      }
      if (name != null) {
        if ("ContextVar".equals(name) || "getLogger".equals(name)) {
          return false;
        }
        if ("list".equals(name) || "dict".equals(name) || "set".equals(name) || "defaultdict".equals(name)) {
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
