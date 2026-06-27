/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import java.util.regex.Pattern;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = AvoidSelectStarCheck.RULE_KEY)
public class AvoidSelectStarCheck extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "AvoidSelectStar";
  private static final String MESSAGE =
      "Avoid using 'SELECT *' in SQL queries. Explicitly project only the columns required to"
          + " minimize network payload and database overhead.";

  private static final Pattern SELECT_STAR_PATTERN =
      Pattern.compile("(?i)\\bselect\\s+\\*\\s+from\\b");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.STRING_LITERAL, this::checkStringLiteral);
  }

  private void checkStringLiteral(SubscriptionContext ctx) {
    StringLiteral stringLiteral = (StringLiteral) ctx.syntaxNode();
    String value = getStringValue(stringLiteral);
    if (value != null && SELECT_STAR_PATTERN.matcher(value).find()) {
      ctx.addIssue(stringLiteral, MESSAGE);
    }
  }

  private static String getStringValue(StringLiteral stringLiteral) {
    StringBuilder sb = new StringBuilder();
    for (Object el : stringLiteral.stringElements()) {
      if (el instanceof StringElement element) {
        sb.append(element.trimmedQuotesValue());
      }
    }
    return sb.toString();
  }
}
