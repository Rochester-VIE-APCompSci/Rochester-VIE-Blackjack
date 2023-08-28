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
 * Represents information about a Dealer's Hand. Objects of this class represent the dealer's entire hand,
 * and are only available when the dealer's down card has been turned face up.
 *
 * Dealers do not have the ability to place a bet on their hand, split, or double down.
 *
 * @author ntl
 *
 */
public class DealerHand extends Hand {


  /**
   * Creates a hand for the deal. Information is provided only for the visible cards.
   *
   * @param scoreAceAs1 score with aces counted as 1
   * @param pointScore  score with aces possibly counted as 11
   * @param cards list of cards in the hand
   */
  public DealerHand(final int scoreAceAs1, final int pointScore,
      final List<Card> cards) {
    super(scoreAceAs1, pointScore, cards);

  }



  /**
   * @return string representation of a dealer hand
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    sb.append("hand = [");
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
