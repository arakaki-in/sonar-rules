/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.StringElement;
import org.sonar.plugins.python.api.tree.StringLiteral;
import org.sonar.plugins.python.api.tree.Tree;

public final class TreeInspections {

  private TreeInspections() {
    // utility class
  }

  /**
   * Returns true if the given tree node is inside a loop (for, while, list comprehension, or
   * generator expression).
   */
  public static boolean isInsideLoop(Tree tree) {
    Tree parent = tree.parent();
    while (parent != null) {
      if (parent.is(Tree.Kind.FOR_STMT)
          || parent.is(Tree.Kind.WHILE_STMT)
          || parent.is(Tree.Kind.LIST_COMPREHENSION)
          || parent.is(Tree.Kind.GENERATOR_EXPR)) {
        return true;
      }
      parent = parent.parent();
    }
    return false;
  }

  /**
   * Returns true if the given tree node is at module level (i.e., no parent is a function or class
   * definition).
   */
  public static boolean isAtModuleLevel(Tree tree) {
    Tree parent = tree.parent();
    while (parent != null) {
      if (parent.is(Tree.Kind.FUNCDEF) || parent.is(Tree.Kind.CLASSDEF)) {
        return false;
      }
      parent = parent.parent();
    }
    return true;
  }

  /**
   * Returns true if the expression is a None literal (either the None keyword or a Name with value
   * "None").
   */
  public static boolean isNoneLiteral(Expression expr) {
    if (expr.is(Tree.Kind.NONE)) {
      return true;
    }
    if (expr instanceof Name name) {
      return "None".equals(name.name());
    }
    return false;
  }

  /**
   * Resolves a dotted name from a QualifiedExpression chain, e.g. {@code requests.api.get} returns
   * "requests.api.get".
   */
  public static String resolveFullyQualifiedName(Expression expr) {
    if (expr instanceof Name name) {
      return name.name();
    } else if (expr instanceof QualifiedExpression qualified) {
      String qualifier = resolveFullyQualifiedName(qualified.qualifier());
      if (qualifier != null) {
        return qualifier + "." + qualified.name().name();
      }
    }
    return null;
  }

  /**
   * Extracts the trimmed string value from a {@link StringLiteral} AST node, concatenating all
   * string elements (including implicit concatenation).
   */
  public static String getStringValue(StringLiteral stringLiteral) {
    StringBuilder sb = new StringBuilder();
    for (Object el : stringLiteral.stringElements()) {
      if (el instanceof StringElement element) {
        sb.append(element.trimmedQuotesValue());
      }
    }
    return sb.toString();
  }
}
