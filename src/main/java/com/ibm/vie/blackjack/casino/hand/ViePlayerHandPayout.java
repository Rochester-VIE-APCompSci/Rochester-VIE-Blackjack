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
package com.ibm.vie.blackjack.casino.hand;

import java.util.LinkedList;
import java.util.List;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;

/**
 * A player's hand with outcome after comparing to the dealer's hand.
 *
 * @author ntl
 *
 */
public class ViePlayerHandPayout extends ViePlayerHand {
  final HandOutcome outcome;
  final int payout;

  /**
   * Constructor
   *
   * @param betPaid bet paid to the dealer
   * @param outcome outcome of the hand
   * @param payout the amount that the dealer payed to the player
   * @param cards list of cards in the hand
   */
  public ViePlayerHandPayout(final int betPaid, final HandOutcome outcome, final int payout,
      final VieCard[] cards) {
    super(betPaid, false, cards);
    this.outcome = outcome;
    this.payout = payout;
  }


  /**
   * Converts a PlayerHand into a PlayerHandWithPayout
   *
   * @param payout the amount that the dealer payed to the player
   * @param outcome the outcome of the hand
   * @param hand the hand for which this outcome was associated with
   */
  public ViePlayerHandPayout(final int payout, final HandOutcome outcome, final ViePlayerHand hand) {
    super(hand);
    this.payout = payout;
    this.outcome = outcome;
  }

  /**
   * Note: The player has already payed the initial bet to the dealer. So the initial bet is
   * included in the amount payed to the player for any win/push, and 0 is payed to the player in
   * the event of a loss.
   *
   *
   * @return the amount that the dealer paid back to the player.
   *
   */
  public int getPayout() {
    return payout;
  }


  /**
   *
   * @return the outcome of the hand
   */
  public HandOutcome getOutcome() {
    return outcome;
  }

  /**
   * Player accessible Hand
   *
   * @return Information about the hand to be used by the player's solution
   */
  public PlayerPayoutHand toPlayerHandPayout() {
    return new PlayerPayoutHand(super.getBetPaid(), this.getScoreAceAs1(), super.getScore(),
        VieCard.toCardList(super.getCards()), outcome.toString().toLowerCase(), payout);
  }

  /**
   * List of player accessible hands from a list of PlayerHand
   *
   * @param hands the list of hands to convert
   */

  public static List<PlayerPayoutHand> toPlayerHandPayoutList(
      final List<ViePlayerHandPayout> hands) {
    final List<PlayerPayoutHand> result = new LinkedList<>();
    for (final ViePlayerHandPayout hand : hands) {
      result.add(hand.toPlayerHandPayout());
    }
    return result;
  }


  /**
   * Represent this object as a string
   */
  public String toString() {
    return "outcome=" + this.outcome + "; payout=" + this.payout + "; " + super.toString();
  }
  
}
