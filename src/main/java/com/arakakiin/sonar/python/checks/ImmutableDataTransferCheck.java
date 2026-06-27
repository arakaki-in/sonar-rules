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

@Rule(key = ImmutableDataTransferCheck.RULE_KEY)
public class ImmutableDataTransferCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "ImmutableDataTransfer";
  private static final String MESSAGE = "Ensure data sent across thread or process boundaries is immutable (avoid sending mutable lists, dicts, or sets).";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
  }

  private void checkCallExpression(SubscriptionContext ctx) {
    CallExpression callExpression = (CallExpression) ctx.syntaxNode();
    
    if (isThreadOrProcessInit(callExpression)) {
      checkThreadOrProcessArgs(ctx, callExpression);
    }
    
    if (isQueuePutCall(callExpression)) {
      checkQueuePutArgs(ctx, callExpression);
    }
  }

  private static boolean isThreadOrProcessInit(CallExpression callExpression) {
    Symbol symbol = callExpression.calleeSymbol();
    if (symbol != null) {
      String fqn = symbol.fullyQualifiedName();
      if ("threading.Thread".equals(fqn) || "multiprocessing.Process".equals(fqn)) {
        return true;
      }
    }
    Expression callee = callExpression.callee();
    if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      String name = qualExpr.name().name();
      if ("Thread".equals(name) || "Process".equals(name)) {
        Expression qualifier = qualExpr.qualifier();
        if (qualifier.is(Tree.Kind.NAME)) {
          String qualName = ((Name) qualifier).name();
          if ("threading".equals(qualName) || "multiprocessing".equals(qualName)) {
            return true;
          }
        }
      }
    } else if (callee.is(Tree.Kind.NAME)) {
      String name = ((Name) callee).name();
      return "Thread".equals(name) || "Process".equals(name);
    }
    return false;
  }

  private void checkThreadOrProcessArgs(SubscriptionContext ctx, CallExpression callExpression) {
    for (RegularArgument argument : getRegularArguments(callExpression)) {
      Expression nameExpr = argument.keywordArgument();
      if (nameExpr != null && nameExpr.is(Tree.Kind.NAME)) {
        String argName = ((Name) nameExpr).name();
        if ("args".equals(argName) || "kwargs".equals(argName)) {
          Expression value = argument.expression();
          if (containsMutableLiteral(value)) {
            ctx.addIssue(value, MESSAGE);
          }
        }
      }
    }
  }

  private static boolean isQueuePutCall(CallExpression callExpression) {
    Expression callee = callExpression.callee();
    if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      String name = qualExpr.name().name();
      return "put".equals(name) || "put_nowait".equals(name);
    }
    return false;
  }

  private void checkQueuePutArgs(SubscriptionContext ctx, CallExpression callExpression) {
    List<RegularArgument> args = getRegularArguments(callExpression);
    if (!args.isEmpty()) {
      Expression itemExpr = args.get(0).expression();
      if (containsMutableLiteral(itemExpr)) {
        ctx.addIssue(itemExpr, MESSAGE);
      }
    }
  }

  private static List<RegularArgument> getRegularArguments(CallExpression callExpression) {
    return callExpression.arguments().stream()
        .filter(arg -> arg instanceof RegularArgument)
        .map(arg -> (RegularArgument) arg)
        .toList();
  }

  private static boolean containsMutableLiteral(Expression expr) {
    if (expr.is(Tree.Kind.LIST_LITERAL) || 
        expr.is(Tree.Kind.DICTIONARY_LITERAL) || 
        expr.is(Tree.Kind.SET_LITERAL)) {
      return true;
    }
    if (expr.is(Tree.Kind.TUPLE)) {
      Tuple tuple = (Tuple) expr;
      for (Expression element : tuple.elements()) {
        if (containsMutableLiteral(element)) {
          return true;
        }
      }
    }
    return false;
  }
}
