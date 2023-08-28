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
package com.ibm.vie.blackjack.casino.analysis;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.casino.config.TableConfig;
import com.ibm.vie.blackjack.casino.observer.GameResultObserver;
import com.ibm.vie.blackjack.casino.rules.RochesterMnCasinoRules;
import com.ibm.vie.blackjack.casino.stats.GameResult;
import com.ibm.vie.blackjack.casino.stats.GameResultStatCalculator;
import com.ibm.vie.blackjack.player.CasinoRules;
import com.ibm.vie.blackjack.player.PlayerStrategy;

/**
 * Methods for analyzing solutions and possibly configuration or Casino rule changes
 *
 * @author ntl
 *
 */
public class AnalyzeSolution {

  /**
   * Analyze a strategy for a Table configuration
   *
   * <p>
   * This runs the game n times with the strategy and config. (The default house rules are applied).
   *
   * </p>
   *
   * @param strategy
   * @param config
   * @param n
   * @return results from analysis
   */
  public static AnalysisResult analyze(final Class<? extends PlayerStrategy> strategy,
      final TableConfig config, final int n) {
    return analyze(strategy, config, n, new RochesterMnCasinoRules());
  }

  /**
   * Analyze a strategy
   * 
   * <p> 
   * This is the same as {@link #analyze(Class, TableConfig, int, CasinoRules, int)} with the
   * exception that the initial deck seed is set from the table config.
   * </p>
   * 
   * @param strategy
   * @param tableConfig
   * @param n
   * @param houseRules
   * @return
   */
  public static AnalysisResult analyze(final Class<? extends PlayerStrategy> strategy,
      final TableConfig tableConfig, final int n, final CasinoRules houseRules) {
    return analyze(strategy, tableConfig, n, houseRules, tableConfig.getDeckNumber());
  }
  
  /**
   * Analyze a strategy for a Table configuration
   *
   * <p>
   * This runs the game n times with the strategy and config. 
   * This version allows a specific set of casino rules to be included in the analysis. Intended to
   * be used for game designers who want to compare one set of rules to another, give the same set
   * of algorithms.
   *
   * </p>
   *
   * @param strategy
   * @param tableConfig
   * @param n
   * @param initialDeckSeed deck seed to start with
   * @return results from analysis
   */
  public static AnalysisResult analyze(final Class<? extends PlayerStrategy> strategy,
      final TableConfig tableConfig, final int n, final CasinoRules houseRules, final int initialDeckSeed) {

    final List<GameResult> results =
        runGameManyTimes(strategy, tableConfig, n, tableConfig.getDeckNumber(), houseRules);

    final double[] earnings = results.stream()
        .mapToDouble(result -> result.getFinalMoney() - tableConfig.getInitialMoney()).toArray();

    final DescriptiveStatistics earningStats = new DescriptiveStatistics(earnings);


    Frequency aggFrequency = new Frequency();
    for (GameResult gResult : results) {
      final GameResultStatCalculator gameStats = new GameResultStatCalculator(tableConfig.getName(), gResult);
      aggFrequency.merge(gameStats.getHandCategoryFrequency());
    }

    return new AnalysisResult(aggFrequency, earningStats, strategy, houseRules, tableConfig);
  }


  /**
   * Run a game many times with different deck seeds
   * This suppresses standard output during the run so that algorithms like 
   * MyPlayer do not take forever because of console IO
   *
   * @param clazz
   * @param config
   * @param numberOfRuns
   * @param startingSeed
   * @param houseRules
   *
   * @return the list of game results
   */
  private static List<GameResult> runGameManyTimes(final Class<? extends PlayerStrategy> clazz,
      final TableConfig config, final int numberOfRuns, final int startingSeed,
      final CasinoRules houseRules) {
    final List<GameResult> gameResults = new LinkedList<>();

    StdOutputRerouter.reroute();
    
    
    try {
      for (int n = 0; n < numberOfRuns; n++) {
        final TableConfig lconfig = new TableConfig(config.getInitialMoney(), config.getMinBet(),
            config.getMaxBet(), config.getNumDecks(), config.getNumRounds(), n + startingSeed);
        final PlayerStrategy strategy = clazz.newInstance();
        final Table table1 = lconfig.getTable(strategy, houseRules);
        final GameResultObserver tracker1 = new GameResultObserver(strategy.getStudentName(), table1.getRules());
        table1.addObserver(tracker1);
        table1.playManyRoundsOfBlackJack();
        gameResults.add(tracker1.getResult());
      }
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      StdOutputRerouter.restore();
    }

    return gameResults;

  }

}

