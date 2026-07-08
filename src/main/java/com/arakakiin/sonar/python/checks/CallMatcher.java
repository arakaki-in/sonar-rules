/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import java.util.Set;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.Tree;

public final class CallMatcher {

  private CallMatcher() {}

  /**
   * Returns the fully qualified name of the callee in the call expression. Resolves via Symbol
   * semantic FQN first, and falls back to AST-based qualified name.
   */
  public static String getCalleeFqn(CallExpression call) {
    Symbol symbol = call.calleeSymbol();
    if (symbol != null) {
      String fqn = symbol.fullyQualifiedName();
      if (fqn != null) {
        return fqn;
      }
    }
    return TreeInspections.resolveFullyQualifiedName(call.callee());
  }

  /** Checks if the callee's fully qualified name matches any of the target names. */
  public static boolean matches(CallExpression call, Set<String> targetNames) {
    String fqn = getCalleeFqn(call);
    return fqn != null && targetNames.contains(fqn);
  }

  /** Checks if the callee's fully qualified name matches a target name. */
  public static boolean matches(CallExpression call, String targetName) {
    String fqn = getCalleeFqn(call);
    return targetName.equals(fqn);
  }

  /** Checks if the callee's fully qualified name starts with any of the target prefixes. */
  public static boolean matchesPrefix(CallExpression call, Set<String> targetPrefixes) {
    String fqn = getCalleeFqn(call);
    if (fqn == null) {
      return false;
    }
    for (String prefix : targetPrefixes) {
      if (fqn.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }

  /** Checks if the callee's fully qualified name starts with a target prefix. */
  public static boolean matchesPrefix(CallExpression call, String targetPrefix) {
    String fqn = getCalleeFqn(call);
    return fqn != null && fqn.startsWith(targetPrefix);
  }

  /** Resolves callee's base qualifier name (e.g. for client.get(), returns "client"). */
  public static String getQualifierName(CallExpression call) {
    Expression callee = call.callee();
    if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      Expression qualifier = qualExpr.qualifier();
      if (qualifier.is(Tree.Kind.NAME)) {
        return ((Name) qualifier).name();
      }
    }
    return null;
  }

  /** Resolves callee's method name (e.g. for requests.get(), returns "get"). */
  public static String getMethodName(CallExpression call) {
    Expression callee = call.callee();
    if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
      QualifiedExpression qualExpr = (QualifiedExpression) callee;
      return qualExpr.name().name();
    } else if (callee.is(Tree.Kind.NAME)) {
      return ((Name) callee).name();
    }
    return null;
  }
}
