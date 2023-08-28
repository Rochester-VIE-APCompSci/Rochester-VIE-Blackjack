/*
 *
 * Copyright (c) 2017, 2018 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.ibm.vie.blackjack.casino.evaluate;

import java.io.File;
import java.io.PrintWriter;
import com.ibm.vie.blackjack.casino.Casino;
import com.ibm.vie.blackjack.player.PlayerStrategy;

/**
 * Driver class to evaluate a directory of solution jars and append results to a csv file
 *
 * @author ntl
 *
 */
public class Evaluator {

  /**
   * Evaluates a solution in a jar and appends to csv
   *
   * @param jarFile
   * @param configFile
   * @param resultCsvFile
   */
  private static void evaluateJar(final File jarFile, final File configFile,
      final File resultCsvFile) {
    try {
      final Class<? extends PlayerStrategy> studentClass =
          StudentClassLoader.getMyPlayerStrategyFromJar(jarFile, "student.player.MyPlayer");
      Casino.playBlackjackBatchCsv(studentClass, configFile.getAbsolutePath(),
          resultCsvFile.getAbsolutePath());
    } catch (final Exception e) {
      throw new RuntimeException("Error evaluating jar " + jarFile.getAbsolutePath(), e);
    }
    System.err.flush();
    System.out.flush();
    System.out.println("Evaluated " + jarFile.getAbsolutePath());
  }


  /**
   * Evaluates all jar files in a directory
   *
   * @param directory
   * @param configFile
   * @param resultCsvFile
   */
  public static void evaluateDirectory(final File directory, final File configFile,
      final File resultCsvFile) {
    for (final File f : directory.listFiles(f -> f.getName().endsWith(".jar") || f.isDirectory())) {
      if (f.isDirectory()) {
        evaluateDirectory(f, configFile, resultCsvFile);
      } else {
        try {
          evaluateJar(f, configFile, resultCsvFile);
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    }
  }


  /**
   * Main entry point
   *
   * @param args
   */
  public static void main(final String[] args) {
    final Parser parser = new Parser(args);
    try {
      final ParseResult lineOptions = parser.parse();

      evaluateDirectory(lineOptions.getDirectoryOfJars(), lineOptions.getConfigFile(),
          lineOptions.getResultCsv());

    } catch (final Exception e) {
      if (e instanceof RuntimeException) {
        e.printStackTrace();
      }
      System.err.println(e.getMessage());
      final PrintWriter helpWriter = new PrintWriter(System.err);
      parser.printUsage(helpWriter, 100, Evaluator.class.getSimpleName());
      helpWriter.flush();
    }

  }

}
