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
package com.ibm.vie.blackjack.casino.driver;

import java.util.List;
import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.casino.config.TableConfig;
import com.ibm.vie.blackjack.casino.observer.GameResultObserver;
import com.ibm.vie.blackjack.casino.rules.OrdinaryBlackjackRules;
import com.ibm.vie.blackjack.casino.stats.GameResultStatCalculator;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;

/**
 * Define a player strategy to focus on splitting
 *
 * @author ntl
 *
 */
public class TestBlackJackSplitOrDouble implements PlayerStrategy {



  /**
   * Always uses the minimum bet
   *
   * @see PlayerStrategy#placeInitialBet(GameInfo)
   */
  @Override
  public int placeInitialBet(final GameInfo gameInfo) {
    return gameInfo.getTableRules().getMinBet();
  }


  /**
   *
   * Split for Ace, 10, J,Q,K, double for 9,10,11, else stand on 17
   *
   * @see PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, Card)
   */
  @Override
  public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
      final PlayerHand currentHand, final List<PlayerHand> playerHands,
      final Card dealerUpCard) {

    PlayerDecision decision;

    final List<Card> cards = currentHand.getCards();

    if (currentHand.getCards().size() == 2
        && gameInfo.getAvailableMoney() > currentHand.getBetPaid()
        && cards.get(0).getRank().equals(cards.get(1).getRank()) && cards.get(1).getMaxScore() >= 10) {
      decision = PlayerDecision.SPLIT;
    } else if (
        gameInfo.getAvailableMoney() > currentHand.getBetPaid() &&
        currentHand.getCards().size() == 2 && 
        (currentHand.getScoreAceAs1() == 10 || currentHand.getScoreAceAs1() == 9 || currentHand.getPointScore() == 11)) {
      decision = PlayerDecision.DOUBLE_DOWN;
    } else {
      if (currentHand.getPointScore() >= 17) {
        decision = PlayerDecision.STAND;
      } else {
        decision = PlayerDecision.HIT;
      }
    }


    return decision;
  }

  /**
   * Never walks away
   *
   * @see PlayerStrategy#decideToWalkAway(GameInfo, List, DealerHand)
   */
  @Override
  public boolean decideToWalkAway(final GameInfo gameInfo,
      final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

    return false;
  }



  /**
   * Main driver to play blackjack
   *
   * @param args - ignored
   */
  public static void main(final String[] args) {

    final PlayerStrategy strategy = new TestBlackJackSplitOrDouble();
    final TableConfig config = new TableConfig(5000000, 1, 100, 1, 1000000, 99); 
    final Table table = config.getTable(strategy, new OrdinaryBlackjackRules());
    
    GameResultObserver statObserver = new GameResultObserver(strategy.getStudentName(), table.getRules());
    table.addObserver(statObserver);
    //table.addObserver(new PrintToConsoleObserver());
    long start = System.currentTimeMillis();
    table.playManyRoundsOfBlackJack();
    System.out.println("Finished with " + table.getAvailableMoney() + " dollars");
    GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), statObserver.getResult());
    System.out.println("Stats:\n" + stats.toString());
    System.out.println("Time = " + ((System.currentTimeMillis() - start) / 1000) + " seconds.\n");
    
  }


  @Override
  public String getStudentName() {
    return "Mickey Mouse";
  }

}
