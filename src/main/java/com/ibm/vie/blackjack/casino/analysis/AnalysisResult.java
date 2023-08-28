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
package com.ibm.vie.blackjack.casino.analysis;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import com.ibm.vie.blackjack.casino.config.TableConfig;
import com.ibm.vie.blackjack.casino.stats.HandResultCategory;
import com.ibm.vie.blackjack.player.CasinoRules;
import com.ibm.vie.blackjack.player.PlayerStrategy;

/**
 * Immutable class that contains the results of an analysis
 *
 * @author ntl
 *
 */
public class AnalysisResult {
  /**
   * Headers that are used for printing a CSV file
   * 
   */
  public static final String[] SUMMARY_CSV_HEADERS = {"Strategy", "House Rules", "Table description", "Number of trials",
      "Min Earnings", "Max Earnings", "Mean Earnings", "Mean Earnings 95% Low",
      "Mean Earnings 95% High", "Hands Played", "Player Blackjack", "Player Win (No Blackjack)",
      "Push", "Player Lose Dealer Blackjack", "Player Lose (No Bust)", "Player Lose (Bust)"};
  private final DescriptiveStatistics earningsStats;
  private final Frequency gameStats;
  private final CasinoRules houseRules;

  private final Class<? extends PlayerStrategy> strategy;

  private final TableConfig tableConfig;


  /**
   * Constructor
   *
   * @param gameStats
   * @param earningsStats
   * @param strategy
   * @param houseRules
   * @param tableConfig
   */
  public AnalysisResult(final Frequency gameStats, final DescriptiveStatistics earningsStats,
      final Class<? extends PlayerStrategy> strategy, final CasinoRules houseRules,
      final TableConfig tableConfig) {
    this.gameStats = gameStats;
    this.earningsStats = earningsStats;
    this.strategy = strategy;
    this.houseRules = houseRules;
    this.tableConfig = tableConfig;
  }

  /**
   * Statistics from games. This is the aggregate of all games played
   *
   * @return stats from a game that is representative of the analysis.
   */
  public Frequency gameStats() {
    return gameStats;
  }


  /**
   * returns descriptive statistics for the earnings of the games under analysis
   * 
   * @return earnings statistics
   */
  public DescriptiveStatistics getEarningsStats() {
    return earningsStats;
  }


  /**
   * 
   * @return the config the table that was analyzed
   */
  public TableConfig getTableConfig() {
    return this.tableConfig;
  }

  /**
   * Prints the summary result to a CSV file
   * 
   * @param printer
   * @throws IOException
   */
  public void printSummaryCsvRecord(CSVPrinter printer) throws IOException {
    final double cIMean = AnalysisUtil.calcMeanCI(earningsStats, .95);
    printer.printRecord(this.strategy.getSimpleName(), 
        this.houseRules.getDescription(), 
        this.tableConfig.getName(),
        this.getEarningsStats().getN(), this.getEarningsStats().getMin(),
        this.getEarningsStats().getMax(), this.getEarningsStats().getMean(),
        this.getEarningsStats().getMean() - cIMean, this.getEarningsStats().getMean() + cIMean,
        this.gameStats.getSumFreq(), this.gameStats.getCount(HandResultCategory.PLAYER_BLACKJACK),
        this.gameStats.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK),
        this.gameStats.getCount(HandResultCategory.PUSH),
        this.gameStats.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK),
        this.gameStats.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST),
        this.gameStats.getCount(HandResultCategory.PLAYER_LOSE_BUST));
  }


  /**
   * Retrieve the 95% confidence interval of the mean
   * 
   * @return the value that should be added or subtracted from the mean to obtain the 95% confidence 
   * interval.
   */
  public double getMeanConfidenceInterval() {
    return AnalysisUtil.calcMeanCI(earningsStats, .95);
  }
  
  
  public static String [] getRawCsvHeaders(int numTrials) {
    String [] headers = new String[numTrials + 3];
    headers[0] = "Strategy Class";
    headers[1] = "Table";
    headers[2] = "Competition Rules";
    for (int i = 0; i < numTrials; i++) {
      headers[3 + i] = "" + (i + 1);
    }
    
    return headers;
  }
  
  /**
   * Prints a csv record with the earnings per trial
   * 
   * @param printer
   * @throws IOException
   */
  public void printRawCsvRecord(CSVPrinter printer) throws IOException {
    List<Object> columns = new LinkedList<Object>();
    columns.add(this.strategy.getSimpleName());
    columns.add(this.tableConfig.getName());
    columns.add(this.houseRules.getDescription());
    for (double earning : this.earningsStats.getValues()) {
      columns.add(earning);
    }
    printer.printRecord(columns);
  }
  
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    sb.append("*****************************************************************\n");
    sb.append("Analysis of Strategy " + strategy.getName() + " with house rules "
        + houseRules.getDescription() + " and table " + this.tableConfig.getName() + "\n");

    final double ci = AnalysisUtil.calcMeanCI(earningsStats, .95);
    sb.append(earningsStats + "\n");

    sb.append("95% confidence interval for the MEAN earnings is [" + (earningsStats.getMean() - ci)
        + "," + (earningsStats.getMean() + ci) + "]\n");

    sb.append("Hand results by category:\n");

    final String outcomeLineFormat = "%30.30s = % 10d (% 7.2f %% / % 7.2f %%)\n";
    for (final HandResultCategory possibleOutcome : HandResultCategory.values()) {
      sb.append(String.format(outcomeLineFormat, possibleOutcome.toString(),
          gameStats.getCount(possibleOutcome), (100 * gameStats.getPct(possibleOutcome)),
          (100 * gameStats.getCumPct(possibleOutcome))));
    }

    return sb.toString();
  }

}
