/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("benchmark")
class PythonBenchmarksTest {

  private static final String[] PYTHON_VERSIONS = {"3.10", "3.11", "3.12", "3.13"};

  @Test
  void testPythonBenchmarks() {
    boolean runAny = false;
    List<String> failedVersions = new ArrayList<>();
    List<String> skippedVersions = new ArrayList<>();
    List<String> passedVersions = new ArrayList<>();

    for (String version : PYTHON_VERSIONS) {
      System.out.println("Checking availability of Python " + version + " via uv...");
      if (isPythonVersionAvailable(version)) {
        System.out.println("Running benchmarks on Python " + version + "...");
        boolean success = runPytestForVersion(version);
        if (success) {
          passedVersions.add(version);
        } else {
          failedVersions.add(version);
        }
        runAny = true;
      } else {
        System.out.println("Python " + version + " is not available. Skipping.");
        skippedVersions.add(version);
      }
    }

    System.out.println("--- Python Benchmark Execution Summary ---");
    System.out.println("Passed versions: " + passedVersions);
    System.out.println("Failed versions: " + failedVersions);
    System.out.println("Skipped versions: " + skippedVersions);

    if (!runAny) {
      System.err.println("Warning: No requested Python version (3.10-3.13) was available via uv.");
    }

    if (!failedVersions.isEmpty()) {
      fail("Python benchmarks failed on the following Python versions: " + failedVersions);
    }
  }

  private boolean isPythonVersionAvailable(String version) {
    try {
      ProcessBuilder pb =
          new ProcessBuilder("uv", "run", "--python", version, "python", "--version");
      pb.directory(new File("."));
      Process process = pb.start();
      int exitCode = process.waitFor();
      return exitCode == 0;
    } catch (IOException | InterruptedException e) {
      return false;
    }
  }

  private boolean runPytestForVersion(String version) {
    return runPytestForVersion(version, 0);
  }

  private boolean runPytestForVersion(String version, int attempt) {
    try {
      ProcessBuilder pb =
          new ProcessBuilder("uv", "run", "--python", version, "pytest", "python_benchmarks/");
      pb.directory(new File("."));
      pb.inheritIO();
      Process process = pb.start();
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        return true;
      }
      if (attempt < 1) {
        System.out.println(
            "Benchmarks failed on Python "
                + version
                + " (attempt "
                + (attempt + 1)
                + "), retrying...");
        Thread.sleep(2000);
        return runPytestForVersion(version, attempt + 1);
      }
      return false;
    } catch (IOException | InterruptedException e) {
      System.err.println("Error executing pytest for version " + version + ": " + e.getMessage());
      if (attempt < 1) {
        return runPytestForVersion(version, attempt + 1);
      }
      return false;
    }
  }
}
