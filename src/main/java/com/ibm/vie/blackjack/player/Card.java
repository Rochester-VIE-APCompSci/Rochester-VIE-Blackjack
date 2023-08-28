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

/**
 * Represents a playing card. The structure of this class is intended to be similar to other classes
 * used in the lesson plan and text book.
 *
 * This object is immutable, meaning it's value cannot be changed once constructed.
 *
 * @author ntl
 *
 */
public class Card {
  private final String suit;
  private final String rank;
  private final int minScore;
  private final int maxScore;


  /**
   * Create information about a card
   *
   * @param rank lower case string representing the rank ("ace", "king", "two", ...)
   * @param suit lower case string representing the suit ("hearts, "spades", ...)
   * @param minScore the lowest score of the card. This point value does not consider that
   *        aces can be counted as 11 in some contexts.
   * @param maxScore is the highest score of the card. This point value considers an ace to be
   *        an 11, even in hands where an ace can't be counted as 11.
   */
  public Card(final String rank, final String suit, final int minScore, final int maxScore) {
    this.suit = suit;
    this.rank = rank;
    this.minScore = minScore;
    this.maxScore = maxScore;
  }

  /**
   * Returns the rank of the card as a string in lower case. Examples: "ace", "king", "two", "three"
   *
   *
   * @return the rank
   */
  public String getRank() {
    return rank;
  }

  /**
   * Returns the suit of the card as a string in lower case. Examples: "heats", "spades",
   * "diamonds", "clubs"
   *
   * @return the suit
   */
  public String getSuit() {
    return suit;
  }


  /**
   * Returns the minimum point value of a card. For an Ace this will return a value of 1, even
   * though it could be legal to use an Ace as an 11 in a hand. Examples: ace = 1 five = 5 jack = 10
   * king = 10
   * 
   * @return the minimum point value for the card
   */
  public int getMinScore() {
    return minScore;
  }


  /**
   * Returns the maximum point value of a card. For an Ace this will return a value of 11, even if the
   * card is part of a hand where it is illegal for an ace to count as 11.
   * Examples: ace = 11, five = 5, jack = 10, king = 10
   * 
   * @return max point value of the card
   */
  public int getMaxScore() {
    return maxScore;
  }
  
  /**
   *
   * @param otherCardInfo another card to compare this card against
   *
   * @return true if both cards are of the same rank, suit, and point value.
   */
  public boolean matches(final Card otherCardInfo) {
    return rank.equals(otherCardInfo.rank) && suit.equals(otherCardInfo.suit)
        && (minScore == otherCardInfo.minScore);
  }

  /**
   * String representation of a card
   * 
   */
  public String toString() {
    return rank + " of " + suit;
  }
  
}
