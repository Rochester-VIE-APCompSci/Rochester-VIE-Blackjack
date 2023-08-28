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
import com.ibm.vie.blackjack.casino.CardManager;
import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.casino.observer.PrintToConsoleObserver;
import com.ibm.vie.blackjack.casino.observer.ScoreGraphObserver;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;
import com.ibm.vie.blackjack.player.TableRules;


/**
 * Define a player strategy to focus on doubling down This version includes a real time graphical
 * score calculator, and an advanced betting algorithm.
 *
 * @author ntl
 *
 */
public class TestRealTimeScoreGraph implements PlayerStrategy {

  private int winStreak = 0;

  
  
  /**
   * Uses a bet based on the number of previous
   * Consecutive wins.
   *
   * @see PlayerStrategy#placeInitialBet(GameInfo)
   */
  @Override
  public int placeInitialBet(final GameInfo gameInfo) {
    if (winStreak >= 0) {
    int idealBet = gameInfo.getMinBet() * (winStreak + 1);
    int maxRulesAllowed = Math.min(idealBet, gameInfo.getMaxBet());
    int maxCanBet = Math.min(maxRulesAllowed, gameInfo.getAvailableMoney());

    return maxCanBet;
    }
    else {
      return gameInfo.getMinBet();
    }

  }


  /**
   *
   * Double down if the dealer's card is less or equal to 10 and the cards in hand total to 9, 10 or
   * 11
   *
   * @see PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, Card)
   */
  @Override
  public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo, final PlayerHand currentHand,
      final List<PlayerHand> playerHands, final Card dealerUpCard) {

    PlayerDecision decision;

    final List<Card> cards = currentHand.getCards();

    if (cards.size() == 2 && gameInfo.getAvailableMoney() > currentHand.getBetPaid()
        && currentHand.getPointScore() >= 9 && currentHand.getPointScore() <= 11
        && dealerUpCard.getMaxScore() < 10) {
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
  public boolean decideToWalkAway(final GameInfo gameInfo, final List<PlayerPayoutHand> playerHands,
      final DealerHand dealerHand) {


    int payOut = 0;
    for (PlayerPayoutHand hand : playerHands) {
      payOut += hand.getPayout() - hand.getBetPaid();
    }
    
    if (payOut > 0 && this.winStreak < 4) {
      this.winStreak++;
    } else if (payOut < 0) {
      this.winStreak = (winStreak > 0) ? 0 : winStreak--;
    }
    
    /**
     * Play till they kick us out
     */
    return false;

  }




  
  /**
   * Main driver to play blackjack
   *
   * @param args - ignored
   */
  @SuppressWarnings("unused")
  public static void main(final String[] args) {

    final int seed_looks_initially_really_good = 1;
    final int seed_looks_initially_really_bad = 2;
    final int seed_looks_initially_really_great = 25;
    final int seed_causes_quick_gains_then_losses = 75;
    
    final TableRules rules = new TableRules(100, 10, 25, 500, 1);
    final CardManager decks = new CardManager(rules, 25);
    final Table table = new Table(new TestRealTimeScoreGraph(), decks, rules);
    table.addObserver(new PrintToConsoleObserver());
    table.addObserver(new ScoreGraphObserver());
    table.playManyRoundsOfBlackJack();
    System.out.println("\nFinished with " + table.getAvailableMoney() + " dollars");

  }


  @Override
  public String getStudentName() {
    return "Don Duck";
  }

}
