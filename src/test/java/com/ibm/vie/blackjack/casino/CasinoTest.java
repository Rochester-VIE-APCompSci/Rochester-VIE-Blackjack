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
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ibm.vie.blackjack.casino.stats.GameResult;
import com.ibm.vie.blackjack.casino.stats.GameResultStatCalculator;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;

public class CasinoTest {
  public static class SimpleStrategyHold17 implements PlayerStrategy {



    /**
     * Always uses the minimum bet
     *
     * @see PlayerStrategy#placeInitialBet(GameInfo)
     */
    @Override
    public int placeInitialBet(final GameInfo gameInfo) {
      return gameInfo.getMinBet();
    }


    /**
     *
     * Stands on 17, even if the 17 involves an ace that is counted as 11 (soft)
     *
     * @see PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, Card)
     */
    @Override
    public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo, final PlayerHand currentHand,
        final List<PlayerHand> playerHands, final Card dealerUpCard) {

      PlayerDecision decision;

      if (currentHand.getPointScore() >= 17) {
        decision = PlayerDecision.STAND;
      } else {
        decision = PlayerDecision.HIT;
      }


      return decision;
    }

    /**
     * Walks away after 20 rounds
     *
     * @see PlayerStrategy#decideToWalkAway(GameInfo, List, DealerHand)
     */
    @Override
    public boolean decideToWalkAway(final GameInfo gameInfo,
        final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {


      /**
       * Play at most 20 rounds
       */
      if (gameInfo.getRoundNumber() >= 20) {
        return true;
      } else {
        return false;
      }
    }


    @Override
    public String getStudentName() {
      return "Luke Skywalker";
    }


  }



  public static class SimpleStrategyHold21 implements PlayerStrategy {



    /**
     * Always uses the minimum bet
     *
     * @see PlayerStrategy#placeInitialBet(GameInfo)
     */
    @Override
    public int placeInitialBet(final GameInfo gameInfo) {
      return gameInfo.getMinBet();
    }


    /**
     *
     * Stands on 17, even if the 17 involves an ace that is counted as 11 (soft)
     *
     * @see PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, Card)
     */
    @Override
    public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo, final PlayerHand currentHand,
        final List<PlayerHand> playerHands, final Card dealerUpCard) {

      PlayerDecision decision;

      if (currentHand.getPointScore() >= 21) {
        decision = PlayerDecision.STAND;
      } else {
        decision = PlayerDecision.HIT;
      }


      return decision;
    }

    /**
     * Walks away after 20 rounds
     *
     * @see PlayerStrategy#decideToWalkAway(GameInfo, List, DealerHand)
     */
    @Override
    public boolean decideToWalkAway(final GameInfo gameInfo,
        final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {


      /**
       * Play at most 20 rounds
       */
      if (gameInfo.getRoundNumber() >= 20) {
        return true;
      } else {
        return false;
      }
    }


    @Override
    public String getStudentName() {
      return "Obi Wan";
    }


  }


  public static class BadStrategy implements PlayerStrategy {



    /**
     * Always uses an illegal bet
     *
     * @see PlayerStrategy#placeInitialBet(GameInfo)
     */
    @Override
    public int placeInitialBet(final GameInfo gameInfo) {
      return gameInfo.getMaxBet() + 1;
    }


    /**
     *
     * Stands on 17, even if the 17 involves an ace that is counted as 11 (soft)
     *
     * @see PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, Card)
     */
    @Override
    public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo, final PlayerHand currentHand,
        final List<PlayerHand> playerHands, final Card dealerUpCard) {

      PlayerDecision decision;

      if (currentHand.getPointScore() >= 17) {
        decision = PlayerDecision.STAND;
      } else {
        decision = PlayerDecision.HIT;
      }


      return decision;
    }

    /**
     * Walks away after 20 rounds
     *
     * @see PlayerStrategy#decideToWalkAway(GameInfo, List, DealerHand)
     */
    @Override
    public boolean decideToWalkAway(final GameInfo gameInfo,
        final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {


      /**
       * Play at most 20 rounds
       */
      if (gameInfo.getRoundNumber() >= 20) {
        return true;
      } else {
        return false;
      }
    }


    @Override
    public String getStudentName() {
      return "Bad Boy";
    }


  }



  /**
   * Loads a sample configuration, runs a basic hold at 17 strategy, and verifies the right number
   * of rounds and final score is correct.
   *
   * @throws JsonProcessingException
   */
  @Test
  public void when_play_blackjack_then_final_score_and_rounds_correct()
      throws JsonProcessingException {


    final Map<String, GameResult> gameResults = Casino.playBlackjack(
        SimpleStrategyHold17.class.getResourceAsStream("gameConfigForTest.json"),
        SimpleStrategyHold17.class, Collections.emptyList());



    // This version will include console output
    // List<Result> results = Casino.playBlackjack(
    // SimpleStrategyHold17.class.getResourceAsStream("gameConfigForTest.json"),
    // SimpleStrategyHold17.class, Collections.singletonList(new PrintToConsoleObserver()));
    //


    // uncomment this to print results
    // ObjectMapper mapper = new ObjectMapper();
    // for (Result result : results) {
    // System.out.println("\n\n\n");
    // System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    // }

    // assert correct overall results
    Assert.assertTrue(gameResults.get("table704").getFinalMoney() == 100);
    Assert.assertTrue("num rounds played was " + gameResults.get("table704").getRoundsPlayed(),
        gameResults.get("table704").getRoundsPlayed() == 2);
    Assert.assertTrue(gameResults.get("table s38").getFinalMoney() == 200);
    Assert.assertTrue(gameResults.get("table s38").getRoundsPlayed() == 3);
    Assert.assertTrue(gameResults.size() == 2);
    Assert.assertTrue(300 == gameResults.values().stream().mapToInt(r -> r.getFinalMoney()).sum());

  }

  /**
   * Loads a sample configuration, runs a basic hold at 17 strategy, writes a csv with results
   * 
   * @throws IOException
   */
  @Test
  public void when_play_blackjack_in_batch_then_csv_is_generated() throws IOException {

    final File tmpFile = File.createTempFile("blackjack", ".csv");
    // final File tmpFile = new File("/home/ntl/blackjack1.csv");


    Casino.playBlackjackBatchCsv(SimpleStrategyHold17.class,
        this.getClass().getResourceAsStream("gameConfigForTest.json"), tmpFile.getAbsolutePath());

    CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(GameResultStatCalculator.CSV_HEADERS);
    try (FileReader fileReader = new FileReader(tmpFile);
        CSVParser parser = new CSVParser(fileReader, csvFileFormat)) {
      List<CSVRecord> csvRecords = parser.getRecords();
      Assert.assertTrue("Expected 3 records by got" + csvRecords.size(), csvRecords.size() == 3);
      Assert.assertTrue(
          "Expected " + GameResultStatCalculator.CSV_HEADERS.length + " columns but got "
              + csvRecords.get(1).size(),
          GameResultStatCalculator.CSV_HEADERS.length == csvRecords.get(1).size());
      Assert.assertTrue("1 -> Earnings was " + Integer.parseInt(csvRecords.get(1).get(4)),
          Integer.parseInt(csvRecords.get(1).get(4)) == 0);
      Assert.assertTrue("2 -> Earnings was " + Integer.parseInt(csvRecords.get(2).get(4)),
          Integer.parseInt(csvRecords.get(2).get(4)) == 0);
      Assert.assertTrue("Error Condition should be OK",
          csvRecords.get(1).get(7).equals("OK"));
      Assert.assertTrue("Error Condition should be OK",
          csvRecords.get(2).get(7).equals("OK"));

    }

    Casino.playBlackjackBatchCsv(SimpleStrategyHold21.class,
        this.getClass().getResourceAsStream("gameConfigForTest.json"), tmpFile.getAbsolutePath());


    try (FileReader fileReader = new FileReader(tmpFile);
        CSVParser parser = new CSVParser(fileReader, csvFileFormat)) {
      List<CSVRecord> csvRecords = parser.getRecords();
      Assert.assertTrue("Expected 5 records by got" + csvRecords.size(), csvRecords.size() == 5);
    }


    Casino.playBlackjackBatchCsv(BadStrategy.class,
        this.getClass().getResourceAsStream("gameConfigForTest.json"), tmpFile.getAbsolutePath());


    try (FileReader fileReader = new FileReader(tmpFile);
        CSVParser parser = new CSVParser(fileReader, csvFileFormat)) {
      List<CSVRecord> csvRecords = parser.getRecords();
      Assert.assertTrue("Expected 7 records by got" + csvRecords.size(), csvRecords.size() == 7);
      Assert.assertTrue("Name is not Bad Boy", csvRecords.get(5).get(1).equals("Bad Boy"));
      Assert.assertTrue("Name is not Bad Boy", csvRecords.get(6).get(1).equals("Bad Boy"));
      Assert.assertTrue("Earnings should be 0 but is " + csvRecords.get(5).get(4),
          Integer.parseInt(csvRecords.get(5).get(4)) == 0);
      Assert.assertTrue("Earnings should be 0 but is " + csvRecords.get(6).get(4),
          Integer.parseInt(csvRecords.get(6).get(4)) == 0);
      Assert.assertFalse("Error Condition should not be NO ERROR",
          csvRecords.get(5).get(7).equals("NO ERROR"));
      Assert.assertFalse("Error Condition should not be NO ERROR",
          csvRecords.get(6).get(7).equals("NO ERROR"));
    }


  }


}
