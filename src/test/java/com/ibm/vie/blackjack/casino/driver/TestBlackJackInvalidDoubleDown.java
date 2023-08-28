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
import com.ibm.vie.blackjack.casino.observer.PrintToConsoleObserver;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;

public class TestBlackJackInvalidDoubleDown implements PlayerStrategy {



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
   * Makes an illegal double down the first round that 3 cards are drawn
   *
   * @see PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, Card)
   */
  @Override
  public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
      final PlayerHand currentHand, final List<PlayerHand> playerHands,
      final Card dealerUpCard) {

    PlayerDecision decision;

    if (currentHand.getCards().size() == 3) {
      decision = PlayerDecision.DOUBLE_DOWN; // this illegal
    } else if (currentHand.getPointScore() >= 17) {
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
     * Play at most 20 hands
     */
    if (gameInfo.getRoundNumber() >= 20) {
      return true;
    } else {
      return false;
    }
  }



  /**
   * Main driver to play blackjack
   *
   * @param args - ignored
   */
  public static void main(final String[] args) {

    final Table table = new Table(new TestBlackJackInvalidDoubleDown());
    table.addObserver(new PrintToConsoleObserver());
    table.playManyRoundsOfBlackJack();
    System.out.println("Finished with " + table.getAvailableMoney() + " dollars");
  }


  @Override
  public String getStudentName() {
    return "John Smith";
  }

}
