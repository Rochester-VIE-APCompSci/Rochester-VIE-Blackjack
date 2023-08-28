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
 * Extends information about a hand with details specific to a Player
 *
 * @author ntl
 *
 */
public class PlayerHand extends Hand {
  private final int betPaid;

  /**
   * Creates information about a player's hand
   *
   * @param betPaid - the bet that has been paid for this player this could be higher than the
   *        initial bet if the player has doubled down on this hand.
   * @param scoreAceAs1 the score of this hand with aces counting as 1
   * @param pointScore the score of this hand with aces counting as 11, if counting aces as 11 does
   *        cause the score to exceed 21
   * @param cardsInHand list of information about cards that are in the hand
   */
  public PlayerHand(final int betPaid, final int scoreAceAs1, final int pointScore,
      final List<Card> cardsInHand) {
    super(scoreAceAs1, pointScore, cardsInHand);
    this.betPaid = betPaid;
  }


  /**
   *
   * @return the bet amount that was paid for this hand, this amount may be higher than the initial
   *         bet, if the player has doubled down
   */
  public int getBetPaid() {
    return betPaid;
  }


  /**
   * @return string representation of this object
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Bet = " + betPaid + "; hand = [");
    for (final Card card : super.getCards()) {
      sb.append("\"" + card.toString() + "\"");
      if (super.getCards().indexOf(card) < super.getCards().size() - 1) {
        sb.append(",");
      }
    }
    sb.append("]; score = ");

    if (getPointScore() != getScoreAceAs1()) {
      sb.append("" + getScoreAceAs1() + "/");
    }
    sb.append("" + getPointScore());
    return sb.toString();
  }

}
