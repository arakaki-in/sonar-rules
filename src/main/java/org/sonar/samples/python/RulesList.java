/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package org.sonar.samples.python;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.sonar.plugins.python.api.PythonCheck;
import org.sonar.samples.python.checks.CustomPythonSubscriptionCheck;
import org.sonar.samples.python.checks.CustomPythonVisitorCheck;
import org.sonar.samples.python.checks.AvoidFileOpenWithoutWithCheck;
import org.sonar.samples.python.checks.NoGlobalMutableStateCheck;
import org.sonar.samples.python.checks.ThreadLocalUsageCheck;
import org.sonar.samples.python.checks.ImmutableDataTransferCheck;
import org.sonar.samples.python.checks.EnforceConnectionPoolingCheck;
import org.sonar.samples.python.checks.MandatoryTimeoutsCheck;
import org.sonar.samples.python.checks.ZeroNPlusOneQueriesCheck;
import org.sonar.samples.python.checks.AvoidSelectStarCheck;
import org.sonar.samples.python.checks.BatchOperationsRequiredCheck;
import org.sonar.samples.python.checks.DbLevelAggregationCheck;
import org.sonar.samples.python.checks.AvoidEagerRegexCompilationCheck;
import org.sonar.samples.python.checks.GeneratorsOverListsCheck;
import org.sonar.samples.python.checks.EfficientStringConcatenationCheck;
import org.sonar.samples.python.checks.UseSlotsCheck;
import org.sonar.samples.python.checks.FastJsonParsingCheck;
import org.sonar.samples.python.checks.AvoidTryExceptControlFlowCheck;

public final class RulesList {

  private RulesList() {
  }

  public static List<Class<? extends PythonCheck>> getChecks() {
    return new ArrayList<>(Stream.concat(
      getPythonChecks().stream(),
      getPythonTestChecks().stream()
    ).toList());
  }

  /**
   * These rules are going to target MAIN code only
   */
  public static List<Class<? extends PythonCheck>> getPythonChecks() {
    return new ArrayList<>(List.of(
      CustomPythonSubscriptionCheck.class,
      AvoidFileOpenWithoutWithCheck.class,
      NoGlobalMutableStateCheck.class,
      ThreadLocalUsageCheck.class,
      ImmutableDataTransferCheck.class,
      EnforceConnectionPoolingCheck.class,
      MandatoryTimeoutsCheck.class,
      ZeroNPlusOneQueriesCheck.class,
      AvoidSelectStarCheck.class,
      BatchOperationsRequiredCheck.class,
      DbLevelAggregationCheck.class,
      AvoidEagerRegexCompilationCheck.class,
      GeneratorsOverListsCheck.class,
      EfficientStringConcatenationCheck.class,
      UseSlotsCheck.class,
      FastJsonParsingCheck.class,
      AvoidTryExceptControlFlowCheck.class
    ));
  }

  /**
   * These rules are going to target TEST code only
   */
  public static List<Class<? extends PythonCheck>> getPythonTestChecks() {
    return new ArrayList<>(List.of(
      CustomPythonVisitorCheck.class
    ));
  }
}
