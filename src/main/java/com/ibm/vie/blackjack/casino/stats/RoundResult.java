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

import java.util.Arrays;
import java.util.List;

/**
 * Represents the result of a specific round in a game
 *
 * @see GameResult
 *
 * @author ntl
 *
 */
public class RoundResult {
  private final HandResult[] hands;
  private final int initialBet;
  private final int moneyAfterRound;
  private final int splitOpportunities;

  /**
   * Construct the round result
   *
   * @param initialBet - the initial amount that was bet in a round
   * @param hands - array of hand outcomes for the round
   * @param splitOpportunities - count of the number of opportunities to split in the round
   * @param moneyAfterRound - the amount of money after the round completed
   */
  public RoundResult(final int initialBet, final HandResult[] hands, final int splitOpportunities,
      final int moneyAfterRound) {
    this.initialBet = initialBet;
    this.hands = hands;
    this.splitOpportunities = splitOpportunities;
    this.moneyAfterRound = moneyAfterRound;
  }

  /**
   * Returns the number of times the initial hand in the round was actually split.
   *
   * @return number of actual splits
   */
  public int getActualSplits() {
    return hands.length - 1;
  }

  /**
   *
   * @return the list of hand results for the round
   */
  public List<HandResult> getHandResults() {
    return Arrays.asList(hands);
  }

  /**
   * Returns the initial bet for the hand
   *
   * @return the initial bet for the hand
   */
  public int getInitialBet() {
    return initialBet;
  }

  /**
   *
   * @return the amount of money that was available after the end of the round
   */
  public int getMoneyAfterRound() {
    return moneyAfterRound;
  }



  /**
   *
   * @return number of opportunities to split in the round
   */
  public int getSplitOpportunities() {
    return splitOpportunities;
  }

}
