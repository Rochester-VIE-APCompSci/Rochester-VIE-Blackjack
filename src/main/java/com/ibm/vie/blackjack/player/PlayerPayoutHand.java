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
package com.ibm.vie.blackjack.player;

import java.util.List;

/**
 * Represents a player's hand after having been compared to the dealer's hand.
 *
 * @author ntl
 *
 */
public class PlayerPayoutHand extends PlayerHand {

  final String outcome;
  final int payout;

  /**
   *
   * Constructs a player's hand with payout information
   *
   * @param betPaid bet initially paid
   * @param scoreAceAs1 score with ace as 1
   * @param pointScore point score
   * @param cardsInHand list of cards in the player's hand
   * @param outcome the outcome of the compare with the dealer
   * @param payout the amount that the dealer played back to the player
   */
  public PlayerPayoutHand(final int betPaid, final int scoreAceAs1, final int pointScore,
      final List<Card> cardsInHand, final String outcome, final int payout) {
    super(betPaid, scoreAceAs1, pointScore, cardsInHand);
    this.outcome = outcome;
    this.payout = payout;
  }


  /**
   * Returns the outcome of the hand as a string Examples: "player_win", "dealer_win",
   * "player_win_w_blackjack", "push"
   *
   * @return the outcome
   */
  public String getOutcome() {
    return outcome;
  }

  /**
   * The amount of money the dealer paid the player at the end of the round
   * <p>
   * Note: The player has already payed the initial bet to the dealer. So the initial bet is
   * included in the amount payed to the player for any win/push, and 0 is payed to the player in
   * the event of a loss.
   * </p>
   *
   * @return the amount that the dealer paid back to the player.
   *
   */
  public int getPayout() {
    return payout;
  }


  /**
   * Returns a string representation of the object
   * 
   * @return hand as a string
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Outcome = \"" + outcome + "\"; Payout = " + payout + "; " + super.toString());
    return sb.toString();
  }

}
