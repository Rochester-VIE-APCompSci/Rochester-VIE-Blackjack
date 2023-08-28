/*
 * Copyright (c) 2018,2018 IBM Corporation
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.vie.blackjack.casino.stats;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import com.ibm.vie.blackjack.casino.exceptions.BlackjackRuleViolationException;
import com.ibm.vie.blackjack.player.PlayerStrategy;

/**
 *
 * Converts a game result into statistics for that game
 *
 * @author ntl
 *
 */
public class GameResultStatCalculator {
  /**
   * Headers for CSV output of the statistics
   *
   */
  public static final String[] CSV_HEADERS =
  //@formatter:off
      {
       "Student Jar",
       "Student Name",
       "Table Name",
       "rules used",
       "Earnings",
       "Rounds Played",
       "Hands Played",
       "Game ending error message",

       "Player Blackjack",
       "Player Win (No Blackjack)",
       "Push",
       "Player Lose Dealer Blackjack",
       "Player Lose (No Bust)",
       "Player Lose (Bust)",

       "Split Opportunities",
       "Number of Splits",
       "Earnings from Splits",
       "Split -> Player Blackjack",
       "Split -> Player Win (No Blackjack)",
       "Split -> Push",
       "Split -> Player Lose Dealer Blackjack",
       "Split -> Player Lose (No Bust)",
       "Split -> Player Lose (Bust)",

       "Number of Double Downs",
       "Earnings from double down",
       "DD -> Player Blackjack",
       "DD -> Player Win (No Blackjack)",
       "DD -> Push",
       "DD -> Player Lose Dealer Blackjack",
       "DD -> Player Lose (No Bust)",
       "DD -> Player Lose (Bust)",

       "Times the min bet was made",
       "Times the max bet was made",
       "Mean Bet",
       "Max Available Money",
       "Min Available Money"
       };
  
// @formatter:on

  final String studentJarPath;
  /**
   * Returns a csv printer for the writer
   *
   * @param writer
   * @param addHeaders if appending this should be false so the header don't get generated
   *
   * @return a new CSVPrinter
   *
   * @throws IOException
   */
  public static CSVPrinter getCSVPrinter(final FileWriter writer, final boolean addHeaders)
      throws IOException {
    if (addHeaders) {
      return new CSVPrinter(writer,
          CSVFormat.DEFAULT.withHeader(GameResultStatCalculator.CSV_HEADERS));
    } else {
      return new CSVPrinter(writer, CSVFormat.DEFAULT);
    }
  }

  private final GameResult gameResult;
  private final String nameOfTable;

  private final List<HandResult> hands;



  private final List<RoundResult> rounds;

  public static String getPathToJar(Class<? extends PlayerStrategy> strategy) {
    String path = null;
    try {
      path = strategy.getProtectionDomain().getCodeSource().getLocation().getFile().toString();
    } catch (Exception e) {
    }
    
    return (path != null ? path : "unknown");
  }
  
  /**
   * Calculate stats from a game result
   *
   * @param studentJarPath path to student's jar
   * @param nameOfTable the name of the table
   * @param gameResult the game to generate statistics from
   */
  public GameResultStatCalculator(final String studentJarPath, final String nameOfTable, final GameResult gameResult) {
    this.gameResult = gameResult;
    this.nameOfTable = nameOfTable;
    rounds = gameResult.getRoundResults();
    hands = rounds.stream().flatMap(round -> round.getHandResults().stream())
        .collect(Collectors.toList());
    
    this.studentJarPath = studentJarPath; 
       
  }

  /**
   * Calculate stats from a game result
   *
   * @param studentJarPath path to student's jar
   * @param nameOfTable the name of the table
   * @param gameResult the game to generate statistics from
   */
  public GameResultStatCalculator(final String nameOfTable, final GameResult gameResult) {
    this.gameResult = gameResult;
    this.nameOfTable = nameOfTable;
    rounds = gameResult.getRoundResults();
    hands = rounds.stream().flatMap(round -> round.getHandResults().stream())
        .collect(Collectors.toList());
    
    this.studentJarPath = ""; 
       
  }
  
  
  /**
   *
   * @return descriptive stats about betting
   */
  public DescriptiveStatistics getBetStats() {
    return new DescriptiveStatistics(
        rounds.stream().mapToDouble(round -> round.getInitialBet()).toArray());
  }

  /**
   * returns the sum of payout - betPaid for hands in hands where a double down was done
   *
   * @return earnings
   */
  public int getDoubleDownEarnings() {
    return hands.stream().filter(hand -> hand.getWasDoubleDown())
        .mapToInt(hand -> hand.getEarnings()).sum();
  }

  public boolean gameEndedInError() {
    return gameResult.getGameEndedInError();
  }

  /**
   * Very similar to {@link #getHandCategoryFrequency()}, except that only hands that involved a
   * {@value com.ibm.vie.blackjack.player.PlayerDecision#DOUBLE_DOWN} decision are considered.
   *
   * @see #getHandCategoryFrequency()
   *
   * @return a {@link Frequency} object, where the elements are of type {@link HandResultCategory}
   */
  public Frequency getDoubleDownHandCategoryFrequency() {
    final Frequency freq = new Frequency(HandResultCategory.descendingOutcome);
    hands.stream().filter(hand -> hand.getWasDoubleDown())
        .forEach(hand -> freq.addValue(hand.getResultStat()));
    return freq;
  }

  /**
   *
   * @return the final available money
   */
  public int getFinalAvailableMoney() {
    return gameResult.getFinalMoney();
  }

  /**
   * Very similar to {@link #getHandCategoryFrequency()}, except that only hands from that rounds
   * where at least one hand was had a {@value com.ibm.vie.blackjack.player.PlayerDecision#SPLIT}
   * decision are considered.
   *
   * @see #getHandCategoryFrequency()
   *
   * @return a {@link Frequency} object, where the elements are of type {@link HandResultCategory}
   */
  public Frequency getFreqFromRoundsWithSplit() {
    final Frequency freq = new Frequency(HandResultCategory.descendingOutcome);
    rounds.stream().filter(round -> round.getActualSplits() > 0)
        .flatMap(round -> round.getHandResults().stream())
        .forEach(hand -> freq.addValue(hand.getResultStat()));

    return freq;
  }



  /**
   * Calculate the frequency for each type of {@link HandResultCategory} across all hands
   * <p>
   * Cumulative count and probabilty are in the order defined by
   * {@link HandResultCategory#descendingOutcome}
   * </p>
   *
   * @return a {@link Frequency} object, where the elements are of type {@link HandResultCategory}
   */
  public Frequency getHandCategoryFrequency() {
    final Frequency freq = new Frequency(HandResultCategory.descendingOutcome);
    hands.forEach(hand -> freq.addValue(hand.getResultStat()));
    return freq;
  }


  /**
   * Returns the max available money after a specific round. The player could have a smaller amount
   * of money at the end of the game if he/she has lost money after that point.
   *
   * @return max amount of available money
   */
  public int getMaxAvailableMoney() {
    return rounds.stream().mapToInt(round -> round.getMoneyAfterRound()).max().orElse(-1);
  }


  /**
   * Returns the min available money after a specific round. The player could have a large amount of
   * money at the end of the game if he/she has won money after that point.
   *
   * @return min amount of available money
   */
  public int getMinAvailableMoney() {
    return rounds.stream().mapToInt(round -> round.getMoneyAfterRound()).min().orElse(-1);
  }

  /**
   * The number of times the player had the opportunity to make a
   * {@value com.ibm.vie.blackjack.player.PlayerDecision#SPLIT} decision
   *
   * @return number of potential splits
   */
  public int getNumSpiltHandOpportunities() {
    return rounds.stream().mapToInt(round -> round.getSplitOpportunities()).sum();
  }


  /**
   *
   * @return number of times the initial bet was the maximum
   */
  public int getNumTimesInitialBetIsMax() {
    return (int) rounds.stream()
        .filter(round -> round.getInitialBet() == gameResult.getRules().getMaxBet()).count();
  }


  /**
   *
   * @return number of times the initial bet was the minimum
   */
  public int getNumTimesInitialBetIsMin() {
    return (int) rounds.stream()
        .filter(round -> round.getInitialBet() == gameResult.getRules().getMinBet()).count();
  }


  /**
   * The number of times the player actually split
   *
   * @return number of splits
   */
  public int getNumTimesSplit() {
    return rounds.stream().mapToInt(round -> round.getActualSplits()).sum();
  }

  /**
   * returns the sum of payout - betPaid for hands in rounds where there was at least one split
   *
   * @return earnings
   */
  public int getSplitEarnings() {
    return rounds.stream().filter(round -> round.getActualSplits() > 0)
        .flatMap(round -> round.getHandResults().stream()).mapToInt(hand -> hand.getEarnings())
        .sum();
  }



  /**
   * Returns the student's first name from the game result. Also handles the possibility that the
   * name is null.
   *
   * @param gResult
   *
   * @return the student's first name
   */
  private String getStudentFirstName(final GameResult gResult) {
    final String name = gResult.getStudentName();
    if (name == null) {
      return "**null student name**";
    }

    final int endOfFirstNameIdx = gameResult.getStudentName().lastIndexOf(" ");
    final String firstName =
        name.substring(0, endOfFirstNameIdx > 0 ? endOfFirstNameIdx : name.length());

    return firstName;
  }

  /**
   * returns the sum of payout - betPaid for all hands
   *
   * @return total earnings
   */
  public int getTotalEarnings() {
    return hands.stream().mapToInt(hand -> hand.getEarnings()).sum();
  }

  /**
   *
   * @return number of hands played across all rounds
   */
  public int getTotalNumberOfHandsPlayed() {
    return hands.size();
  }


  /**
   *
   * @return number of rounds played
   */
  public int getTotalNumberOfRoundsPlayed() {
    return rounds.size();
  }


  /**
   * Prints the result to a CSV file
   *
   * @param printer
   * @throws IOException
   */
  public void printCSVRecord(final CSVPrinter printer, final Class<? extends PlayerStrategy> clazz) throws IOException {
    final Frequency overall_freq = this.getHandCategoryFrequency();
    final Frequency split_freq = this.getFreqFromRoundsWithSplit();
    final Frequency dd_freq = this.getDoubleDownHandCategoryFrequency();

    
    printer.printRecord(
        studentJarPath, 
        gameResult.getStudentName(), //
        nameOfTable, //
        this.gameResult.getRules().getCompetitionRules().getDescription(), //
        this.getTotalEarnings(), //
        this.getTotalNumberOfRoundsPlayed(), //
        this.getTotalNumberOfHandsPlayed(), //
        gameResult.getErrorMessage(), // 
        overall_freq.getCount(HandResultCategory.PLAYER_BLACKJACK), //
        overall_freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK), //
        overall_freq.getCount(HandResultCategory.PUSH), //
        overall_freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK), //
        overall_freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST), //
        overall_freq.getCount(HandResultCategory.PLAYER_LOSE_BUST), //

        this.getNumSpiltHandOpportunities(), this.getNumTimesSplit(), this.getSplitEarnings(), //
        split_freq.getCount(HandResultCategory.PLAYER_BLACKJACK), //
        split_freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK), //
        split_freq.getCount(HandResultCategory.PUSH), //
        split_freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK), //
        split_freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST), //
        split_freq.getCount(HandResultCategory.PLAYER_LOSE_BUST), //

        dd_freq.getSumFreq(), //
        this.getDoubleDownEarnings(), //
        dd_freq.getCount(HandResultCategory.PLAYER_BLACKJACK), //
        dd_freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK), //
        dd_freq.getCount(HandResultCategory.PUSH), //
        dd_freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK), //
        dd_freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST), //
        dd_freq.getCount(HandResultCategory.PLAYER_LOSE_BUST), //

        this.getNumTimesInitialBetIsMin(), //
        this.getNumTimesInitialBetIsMax(), //
        this.getBetStats().getMean(), //
        this.getMaxAvailableMoney(), //
        this.getMinAvailableMoney());
  }

  /**
   * String representation of the object
   */
  @Override
  public String toString() {
    final String outcomeLineFormat = "%30.30s = % 10d (% 7.2f %% / % 7.2f %%)\n";



    final StringBuilder sb = new StringBuilder();
    if (!gameResult.getGameEndedInError()) {
      sb.append("This strategy did not make any illegal decisions in this game. Good Job "
          + getStudentFirstName(gameResult) + "!!\n\n");
    } else {
      sb.append(getStudentFirstName(gameResult)
          + ", this strategy has some problems that need to be fixed.....\n\n");

      if (gameResult.getGameEndingException() instanceof BlackjackRuleViolationException) {
        sb.append(gameResult.getGameEndingException().getMessage() + "\n\n");
      } else {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        gameResult.getGameEndingException().printStackTrace(pw);
        sb.append(sw.toString() + "\n\n");
      }
    }



    sb.append("Final Money                                   = \t" + this.getFinalAvailableMoney()
        + "\n");
    sb.append(
              "Total Earnings (Profit)                       = \t" + this.getTotalEarnings() + "\n");
    sb.append("Number of Rounds Played                       = \t"
        + this.getTotalNumberOfRoundsPlayed() + "\n");
    sb.append("Number of Hands  Played                       = \t"
        + this.getTotalNumberOfHandsPlayed() + "\n");
    sb.append("Initial Money                                 = \t" + this.gameResult.getRules().getInitialMoney() + "\n");
    sb.append(
        "Maximum amount of money at the end of a round = \t" + this.getMaxAvailableMoney() + "\n");
    sb.append(
        "Minimum amount of money at the end of a round = \t" + this.getMinAvailableMoney() + "\n");
    sb.append("\n\n");

    sb.append("OVERALL RESULT CATEGORIES\n" + "-------------------------\n");
    final Frequency handOutcomes = this.getHandCategoryFrequency();
    for (final HandResultCategory possibleOutcome : HandResultCategory.values()) {
      sb.append(String.format(outcomeLineFormat, possibleOutcome.toString(),
          handOutcomes.getCount(possibleOutcome), (100 * handOutcomes.getPct(possibleOutcome)),
          (100 * handOutcomes.getCumPct(possibleOutcome))));
    }


    sb.append("\nYou had \t\t\t" + this.getNumSpiltHandOpportunities() + " opportunities to split ("
        + (int) Math.round(
            this.getNumSpiltHandOpportunities() / (double) this.getTotalNumberOfHandsPlayed() * 100)
        + "% of all hands) \n");
    sb.append("You actually split \t\t" + this.getNumTimesSplit() + " ("
        + (int) Math
            .round(this.getNumTimesSplit() / (double) this.getNumSpiltHandOpportunities() * 100)
        + "% of opportunities)\n");
    sb.append("Split Earnings     \t\t" + this.getSplitEarnings() + "\n\n");

    final Frequency splitOutcomes = this.getFreqFromRoundsWithSplit();

    sb.append("RESULTS FOR ROUNDS WITH A SPLIT:\n");
    sb.append("--------------------------------\n");
    for (final HandResultCategory possibleOutcome : HandResultCategory.values()) {
      sb.append(String.format(outcomeLineFormat, possibleOutcome.toString(),
          splitOutcomes.getCount(possibleOutcome), (100 * splitOutcomes.getPct(possibleOutcome)),
          (100 * splitOutcomes.getCumPct(possibleOutcome))));
    }

    final Frequency doubleOutcomes = this.getDoubleDownHandCategoryFrequency();

    sb.append("\nYou doubled down \t\t" + doubleOutcomes.getSumFreq() + " times ("
        + (int) (doubleOutcomes.getSumFreq() / (double) this.getTotalNumberOfHandsPlayed() * 100)
        + "%)\n");
    sb.append("Double Down Earnings\t\t" + this.getDoubleDownEarnings() + "\n\n");


    sb.append("RESULTS FOR DOUBLE DOWN:\n");
    sb.append("------------------------\n");
    for (final HandResultCategory possibleOutcome : HandResultCategory.values()) {
      sb.append(String.format(outcomeLineFormat, possibleOutcome.toString(),
          doubleOutcomes.getCount(possibleOutcome), (100 * doubleOutcomes.getPct(possibleOutcome)),
          (100 * doubleOutcomes.getCumPct(possibleOutcome))));
    }

    sb.append("\nBetting Stats\n");
    sb.append("-------------\n");
    sb.append("You Bet the max amount " + this.getNumTimesInitialBetIsMax() + " times ("
        + (this.getNumTimesInitialBetIsMax() / (double) this.getTotalNumberOfRoundsPlayed() * 100)
        + "%)\n");

    sb.append("You Bet the min amount " + this.getNumTimesInitialBetIsMin() + " times ("
        + (this.getNumTimesInitialBetIsMin() / (double) this.getTotalNumberOfRoundsPlayed() * 100)
        + "%)\n");


    sb.append(this.getBetStats().toString());


    return sb.toString();
  }

}
