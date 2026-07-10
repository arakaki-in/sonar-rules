/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class TreeInspectionsTest {

  @Test
  void utilityClassHasPrivateConstructor() throws Exception {
    Constructor<TreeInspections> constructor = TreeInspections.class.getDeclaredConstructor();
    assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  void utilityClassHasExpectedMethods() {
    Method[] methods =
        java.util.Arrays.stream(TreeInspections.class.getDeclaredMethods())
            .filter(m -> !m.isSynthetic())
            .toArray(Method[]::new);
    assertThat(methods).hasSize(5);

    assertThat(
            java.util.Arrays.stream(methods)
                .map(Method::getName)
                .filter(
                    n ->
                        n.equals("isInsideLoop")
                            || n.equals("isAtModuleLevel")
                            || n.equals("isNoneLiteral")
                            || n.equals("resolveFullyQualifiedName")
                            || n.equals("getStringValue")))
        .hasSize(5);
  }

  @Test
  void allMethodsArePublicStatic() {
    for (Method method : TreeInspections.class.getDeclaredMethods()) {
      if (method.isSynthetic()) {
        continue;
      }
      assertThat(Modifier.isPublic(method.getModifiers())).isTrue();
      assertThat(Modifier.isStatic(method.getModifiers())).isTrue();
    }
  }
}
