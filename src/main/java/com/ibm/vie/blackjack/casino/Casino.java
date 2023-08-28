/*
 * Copyright (c) 2018,2018 IBM Corporation Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.vie.blackjack.casino;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.io.FileInputStream;
import org.apache.commons.csv.CSVPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.vie.blackjack.casino.config.CompetitionConfig;
import com.ibm.vie.blackjack.casino.config.TableConfig;
import com.ibm.vie.blackjack.casino.observer.GameResultObserver;
import com.ibm.vie.blackjack.casino.observer.TableObserver;
import com.ibm.vie.blackjack.casino.stats.GameResult;
import com.ibm.vie.blackjack.casino.stats.GameResultStatCalculator;
import com.ibm.vie.blackjack.player.CasinoRules;
import com.ibm.vie.blackjack.player.PlayerStrategy;
import com.ibm.vie.blackjack.gui.Blackjack;

/**
 * Main entry functions to play blackjack.
 *
 * @author ntl
 *
 */
public class Casino {
  private static ObjectMapper mapper = new ObjectMapper();

  /**
   * Play blackjack with the given configuration, strategy, and observers Each table in the
   * configuration is played independently, the results of one table do not affect play for the
   * next.
   *
   * @param configAsJson an input stream containing the configuration to use (as json)
   * @param clazz the class for the player strategy, an instance of this class will be created for
   *        EACH table defined by the configuration
   * @param observers additional observers that should be added to the table, such as UI.
   *
   * @return Map of table name -> Game Result
   *         If the name of the table is empty or not distinct, a unique name will be generated
   */
  public static Map<String, GameResult> playBlackjack(final InputStream configAsJson,
      final Class<? extends PlayerStrategy> clazz, final List<TableObserver> observers) {

    final Map<String, GameResult> gameResults = new HashMap<>();

    try {
      final CompetitionConfig competitionConfig =
          mapper.readValue(configAsJson, CompetitionConfig.class);
      final CasinoRules houseRules = competitionConfig.getCasinoRules();

      for (final TableConfig tableConfig : competitionConfig.getTableConfigs()) {
        final PlayerStrategy strategy = clazz.newInstance();

        final Table table = tableConfig.getTable(strategy, houseRules);
        final GameResultObserver tracker =
            new GameResultObserver(strategy.getStudentName(), table.getRules());

        observers.forEach(o -> table.addObserver(o));
        table.addObserver(tracker);

        table.playManyRoundsOfBlackJack();

        String tableName = tableConfig.getName();
        if (tableName == null || tableName.trim().isEmpty()) {
          tableName = UUID.randomUUID().toString();
        } else if (gameResults.containsKey(tableName)) {
          tableName = tableName + "_" + tableConfig.getDeckNumber() + "_" + UUID.randomUUID().toString();
        }

        gameResults.put(tableName, tracker.getResult());

      }
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }

    return gameResults;

  }

  public static void playBlackjackWithUI(final Class<? extends PlayerStrategy> clazz) {
    // try {
    checkPlayerStrategy(clazz);
    Blackjack.run(clazz);
    // PlayerStrategy strategy = clazz.newInstance();
    // Blackjack.run(strategy);
    // } catch (InstantiationException | IllegalAccessException e) {
    // e.printStackTrace();
    // }
  }


  /**
   * Default method to play blackjack in batch competition. The result of the competition is
   * appended to the specified csv file
   *
   * Console output is suppressed, and the result is a game that is appended to a csv file.
   *
   * @param clazz
   * @param competitionConfigFileName
   * @param outputCsvFileName
   *
   * @throws IOException
   */
  public static void playBlackjackBatchCsv(final Class<? extends PlayerStrategy> clazz,
      final String competitionConfigFileName, final String outputCsvFileName) throws IOException {
    playBlackjackBatchCsv(clazz, new FileInputStream(competitionConfigFileName), outputCsvFileName);

  }


  /**
   * Default method to play blackjack in batch competition. The result of the competition is
   * appended to the specified csv file
   *
   * Console output is suppressed, and the result is a game that is appended to a csv file.
   *
   * @param clazz
   * @param competitionConfigFile Input Stream
   * @param outputCsvFileName
   *
   * @throws IOException
   */
  public static void playBlackjackBatchCsv(final Class<? extends PlayerStrategy> clazz,
      final InputStream competitionConfigFileInputStream, final String outputCsvFileName)
      throws IOException {


    PrintStream originalStdOut = System.out;

    final Map<String, GameResult> gameResults;
    try {
      System.setOut(new PrintStream(new OutputStream() {
        @Override
        public void write(int b) {
          // DO NOTHING
        }
      }));

      gameResults =
          Casino.playBlackjack(competitionConfigFileInputStream, clazz, Collections.emptyList());
    } finally {
      System.setOut(originalStdOut);
    }

    File outputCsvFile = new File(outputCsvFileName);
    try (FileWriter writer = new FileWriter(outputCsvFile, true);
        CSVPrinter printer =
            GameResultStatCalculator.getCSVPrinter(writer, outputCsvFile.length() == 0)) {

      for (final Entry<String, GameResult> gameResult : gameResults.entrySet()) {

        final GameResultStatCalculator stats = new GameResultStatCalculator(GameResultStatCalculator.getPathToJar(clazz), gameResult.getKey(), gameResult.getValue());
        stats.printCSVRecord(printer, clazz);
      }
    }
  }


  /**
   * Default method to play interactive blackjack. This is intended to be the method called by the
   * template player that is provided to the students by IBM.
   *
   * Eventually this will need to be updated to be more connected to the UI.
   *
   * For now, the output is dumped to the console, and the sum of the results is also dumped.
   *
   * @param clazz
   */
  public static void playBlackjackInteractive(final Class<? extends PlayerStrategy> clazz) {
    String doWeGrade = System.getProperty("GRADE_BLACKJACK");

    if (doWeGrade != null && doWeGrade.equals("true")) {
      try {
        Casino.playBlackjackBatchCsv(clazz,
            "/homes/hny7/pgerver/vie/Rochester-VIE-Blackjack/TableConfigs/allCompetitionTables.json",
            "BATCH_CSV_OUTPUT.csv");
      } catch (IOException e) {
        System.err.println("Failed to run playBlackjackBatchCsv: " + e.getMessage());
        e.printStackTrace();
      }
    } else {
      Casino.playBlackjackWithUI(clazz);
    }

    // final List<GameResult> gameResults = Casino.playBlackjack(
    // clazz.getResourceAsStream("defaultConfig.json"), clazz, Collections.emptyList());
    //
    // for (final GameResult gameResult : gameResults) {
    // final GameResultStatCalculator stats = new GameResultStatCalculator(gameResult);
    // System.out.println("Game Stats:\n" + stats.toString() + "\n\n\n");
    // }

  }

  private static void checkPlayerStrategy(Class<? extends PlayerStrategy> clazz) {
    // Unfortunate to instantiate the student's class an extra time, but within
    // GameThread its too
    // late to stop the game.
    try {
      PlayerStrategy ps = clazz.newInstance();
      String studentName = ps.getStudentName();

      if (studentName == null || studentName.isEmpty() || studentName == "FirstName LastName") {
        throw new InstantiationException(
            "You must set your first name and last name in your player's getStudentName method");
      }
    } catch (Exception ex) {
      // Throw as an error so the studnet implementation does not have to declare this
      // as thrown.
      throw new Error(ex.getMessage(), ex);
    }
  }

}
