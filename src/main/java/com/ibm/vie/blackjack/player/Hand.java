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
 * Represents information about a hand. This can be used to determine cards in a hand and also the
 * score of a hand.
 *
 * This object is immutable and will be constructed by the framework.
 *
 * @author ntl
 *
 */
public class Hand {
  private final List<Card> cardsInHand;
  private final int scoreAceAs1;
  private final int pointScore;


  /**
   * Constructs information about a hand
   *
   * @param scoreAceAs1 - the absolute lowest score for the hand (Aces will be counted as 1)
   * @param pointScore - the absolute best score for the hand (Aces may be counted as 11 if 11 does
   *        not cause the score to exceed 21)
   * @param cardsInHand - the cards in the hand
   */
  public Hand(final int scoreAceAs1, final int pointScore, final List<Card> cardsInHand) {
    this.cardsInHand = cardsInHand;
    this.scoreAceAs1 = scoreAceAs1;
    this.pointScore = pointScore;
  }


  /**
   * Retrieve an unmodifiable list of cards in the hand
   *
   * @return a list of information about cards in the hand
   */
  public List<Card> getCards() {
    return cardsInHand;
  }


  /**
   * This score always counts aces as 1. For example the hand (A, TEN) would have a score of 11, and
   * (TEN, TEN) would have a score of 20.
   *
   * @return the minimum score
   */
  public int getScoreAceAs1() {
    return scoreAceAs1;
  }


  /**

   * <p>Returns the top/best score of the hand.
   * This score counts aces as 11, but only if the ace does not the total score of the hand  exceed 21.
   * </p>

   *
   * Examples:
   * <ul>
   * <li>(A, TEN)                == 21</li>
   * <li>(A, A, TEN)             == 12</li>
   * <li>(A, FIVE, A)            == 17</li>
   * <li>(TWO, TWO, TWO)         == 6</li>
   * <li>(TEN, EIGHT, ACE, FOUR) == 23 (A bust)</li>
   * </ul>
   *
   * @return the point score of a hand
   */
  public int getPointScore() {
    return pointScore;
  }

}
