/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python;

import java.util.ArrayList;
import java.util.List;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.python.api.PythonCustomRuleRepository;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

public class CustomPythonRuleRepository implements RulesDefinition, PythonCustomRuleRepository {
  public static final String RESOURCE_BASE_PATH = "/org/sonar/l10n/python/rules/python";
  public static final String REPOSITORY_KEY = "arakakiin-rules";
  public static final String REPOSITORY_NAME = "Arakakiin Custom Rules";

  private final SonarRuntime runtime;
  private final List<Class<?>> checkClasses;

  public CustomPythonRuleRepository(SonarRuntime runtime) {
    this.runtime = runtime;
    this.checkClasses = List.copyOf(RulesList.getChecks()); // immutable snapshot at construction
  }

  @Override
  public void define(Context context) {
    NewRepository repository =
        context.createRepository(REPOSITORY_KEY, "py").setName(REPOSITORY_NAME);
    RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(RESOURCE_BASE_PATH, runtime);
    ruleMetadataLoader.addRulesByAnnotatedClass(repository, new ArrayList<>(checkClasses));
    repository.done();
  }

  @Override
  public String repositoryKey() {
    return REPOSITORY_KEY;
  }

  @Override
  public List<Class<?>> checkClasses() {
    return checkClasses;
  }
}
