/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python;

import com.arakakiin.sonar.python.checks.*;
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
            EnforceConnectionPoolingCheck.class,
            MandatoryTimeoutsCheck.class,
            ZeroNPlusOneQueriesCheck.class,
            AvoidSelectStarCheck.class,
            BatchOperationsRequiredCheck.class,
            DbLevelAggregationCheck.class,
            GeneratorsOverListsCheck.class,
            EfficientStringConcatenationCheck.class,
            AvoidSyncIoInAsyncCheck.class,
            AvoidPandasIterrowsCheck.class,
            AvoidDictKeysIterationCheck.class,
            DequeOverListInsertCheck.class,
            NoneComparisonStyleCheck.class,
            AvoidStarImportCheck.class,
            PreferFStringOverFormatCheck.class,
            AvoidMapLambdaCheck.class,
            PreferSetMembershipCheck.class,
            AvoidRangeLenIterationCheck.class));
  }

  /** These rules are going to target TEST code only */
  public static List<Class<? extends PythonCheck>> getPythonTestChecks() {
    return new ArrayList<>(List.of());
  }
}
