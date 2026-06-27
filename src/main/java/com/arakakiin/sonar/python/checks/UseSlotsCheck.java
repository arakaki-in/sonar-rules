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

@Rule(key = UseSlotsCheck.RULE_KEY)
public class UseSlotsCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "UseSlots";
  private static final String MESSAGE =
      "Define '__slots__' for high-volume data classes to reduce memory usage and improve attribute"
          + " access speed. For dataclasses, use '@dataclass(slots=True)'.";

  private static final Set<String> HIGH_VOLUME_SUFFIXES =
      Set.of(
          "DTO", "Model", "Data", "Record", "Item", "Payload", "Result", "Entity", "Message",
          "Event");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CLASSDEF, this::checkClassDefinition);
  }

  private void checkClassDefinition(SubscriptionContext ctx) {
    ClassDef classDef = (ClassDef) ctx.syntaxNode();
    String className = classDef.name().name();

    boolean isHighVolume = false;
    for (String suffix : HIGH_VOLUME_SUFFIXES) {
      if (className.endsWith(suffix)) {
        isHighVolume = true;
        break;
      }
    }

    boolean hasDataclass = false;
    boolean hasSlotsTrue = false;

    for (Decorator decorator : classDef.decorators()) {
      Expression expr = decorator.expression();
      if (expr.is(Tree.Kind.NAME)) {
        if ("dataclass".equals(((Name) expr).name())) {
          hasDataclass = true;
        }
      } else if (expr.is(Tree.Kind.CALL_EXPR)) {
        CallExpression call = (CallExpression) expr;
        Expression callee = call.callee();
        if (callee.is(Tree.Kind.NAME) && "dataclass".equals(((Name) callee).name())) {
          hasDataclass = true;
          for (Argument arg : call.arguments()) {
            if (arg instanceof RegularArgument regArg) {
              Expression keyword = regArg.keywordArgument();
              if (keyword != null
                  && keyword.is(Tree.Kind.NAME)
                  && "slots".equals(((Name) keyword).name())) {
                Expression val = regArg.expression();
                if (val.is(Tree.Kind.NAME) && "True".equals(((Name) val).name())) {
                  hasSlotsTrue = true;
                }
              }
            }
          }
        }
      }
    }

    if (isHighVolume || hasDataclass) {
      if (hasSlotsTrue) {
        return;
      }
      if (!definesSlots(classDef)) {
        ctx.addIssue(classDef.name(), MESSAGE);
      }
    }
  }

  private static boolean definesSlots(ClassDef classDef) {
    StatementList body = classDef.body();
    if (body == null) {
      return false;
    }
    for (Statement stmt : body.statements()) {
      if (stmt instanceof AssignmentStatement assign) {
        for (Expression lhs : assign.lhsExpressions()) {
          if (isName(lhs, "__slots__")) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private static boolean isName(Expression expr, String target) {
    if (expr instanceof Name name) {
      return target.equals(name.name());
    }
    if (expr instanceof ExpressionList exprList) {
      for (Expression child : exprList.expressions()) {
        if (isName(child, target)) {
          return true;
        }
      }
    }
    return false;
  }
}
