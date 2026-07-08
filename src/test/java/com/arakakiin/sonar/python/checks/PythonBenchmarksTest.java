/*
 * Copyright (C) 2026 Everton Arakaki
 * SPDX-License-Identifier: MIT
 */
package com.arakakiin.sonar.python.checks;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("benchmark")
class PythonBenchmarksTest {

  private static final String BENCHMARK_PYTHON_VERSION = "3.15";
  private static final int MAX_RETRIES = 3;

  @Test
  void testPythonBenchmarks() {
    System.out.println(
        "Checking availability of Python " + BENCHMARK_PYTHON_VERSION + " via uv...");
    if (!isPythonVersionAvailable(BENCHMARK_PYTHON_VERSION)) {
      System.err.println(
          "Warning: Python "
              + BENCHMARK_PYTHON_VERSION
              + " is not available via uv. Skipping benchmarks.");
      return;
    }

    System.out.println("Running benchmarks on Python " + BENCHMARK_PYTHON_VERSION + "...");
    boolean success = runPytestForVersion(BENCHMARK_PYTHON_VERSION);
    if (!success) {
      fail("Python benchmarks failed on version " + BENCHMARK_PYTHON_VERSION);
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
          new ProcessBuilder(
              "uv", "run", "--python", version, "pytest", "python_benchmarks/", "-q", "--tb=short");
      pb.directory(new File("."));
      Process process = pb.start();

      // Capture stderr for diagnostics on failure
      String stderr = new String(process.getErrorStream().readAllBytes());
      int exitCode = process.waitFor();

      if (exitCode == 0) {
        return true;
      }

      // Print last 20 lines of stderr on failure
      if (!stderr.isEmpty()) {
        String[] lines = stderr.split("\n");
        int start = Math.max(0, lines.length - 20);
        System.err.println(
            "Pytest stderr (last " + (lines.length - start) + " of " + lines.length + " lines):");
        for (int i = start; i < lines.length; i++) {
          System.err.println(lines[i]);
        }
      }

      if (attempt < MAX_RETRIES - 1) {
        long sleepMs = 2000L * (1 << attempt);
        System.out.println(
            "Benchmarks failed on Python "
                + version
                + " (attempt "
                + (attempt + 1)
                + "/"
                + MAX_RETRIES
                + "), retrying in "
                + (sleepMs / 1000)
                + "s...");
        Thread.sleep(sleepMs);
        return runPytestForVersion(version, attempt + 1);
      }

      System.err.println(
          "Benchmarks failed after " + MAX_RETRIES + " attempts on Python " + version);
      return false;
    } catch (IOException | InterruptedException e) {
      System.err.println("Error executing pytest for version " + version + ": " + e.getMessage());
      if (attempt < MAX_RETRIES - 1) {
        return runPytestForVersion(version, attempt + 1);
      }
      return false;
    }
  }
}
