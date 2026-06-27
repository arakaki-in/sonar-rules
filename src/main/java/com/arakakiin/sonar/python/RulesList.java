/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python;

import com.arakakiin.sonar.python.checks.AvoidEagerRegexCompilationCheck;
import com.arakakiin.sonar.python.checks.AvoidFileOpenWithoutWithCheck;
import com.arakakiin.sonar.python.checks.AvoidSelectStarCheck;
import com.arakakiin.sonar.python.checks.AvoidTryExceptControlFlowCheck;
import com.arakakiin.sonar.python.checks.BatchOperationsRequiredCheck;
import com.arakakiin.sonar.python.checks.DbLevelAggregationCheck;
import com.arakakiin.sonar.python.checks.EfficientStringConcatenationCheck;
import com.arakakiin.sonar.python.checks.EnforceConnectionPoolingCheck;
import com.arakakiin.sonar.python.checks.FastJsonParsingCheck;
import com.arakakiin.sonar.python.checks.GeneratorsOverListsCheck;
import com.arakakiin.sonar.python.checks.ImmutableDataTransferCheck;
import com.arakakiin.sonar.python.checks.MandatoryTimeoutsCheck;
import com.arakakiin.sonar.python.checks.NoGlobalMutableStateCheck;
import com.arakakiin.sonar.python.checks.ThreadLocalUsageCheck;
import com.arakakiin.sonar.python.checks.UseSlotsCheck;
import com.arakakiin.sonar.python.checks.ZeroNPlusOneQueriesCheck;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.sonar.plugins.python.api.PythonCheck;

public final class RulesList {

  private RulesList() {}

  public static List<Class<? extends PythonCheck>> getChecks() {
    return new ArrayList<>(
        Stream.concat(getPythonChecks().stream(), getPythonTestChecks().stream()).toList());
  }

  /** These rules are going to target MAIN code only */
  public static List<Class<? extends PythonCheck>> getPythonChecks() {
    return new ArrayList<>(
        List.of(
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
            AvoidTryExceptControlFlowCheck.class));
  }

  /** These rules are going to target TEST code only */
  public static List<Class<? extends PythonCheck>> getPythonTestChecks() {
    return new ArrayList<>(List.of());
  }
}
