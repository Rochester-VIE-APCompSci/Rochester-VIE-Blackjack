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
package com.ibm.vie.blackjack.casino.card;

/**
 * Represents the rank, or the face of a card
 * 
 * @author ntl
 *
 */
public enum Rank {
  ACE("A", 1, 11),
  TWO("2", 2),
  THREE("3", 3),
  FOUR("4", 4),
  FIVE("5", 5),
  SIX("6", 6),
  SEVEN("7", 7),
  EIGHT("8", 8),
  NINE("9", 9),
  TEN("10", 10),
  JACK("J", 10),
  QUEEN("Q",10),
  KING("K", 10);
  
  /**
   * The string that appears on the face of the card
   */
  private final String face;
  
  /**
   * The minimum score of a card of a rank in blackjack.
   * For example an Ace will have a value of 1, even though the rules of the
   * game may permit scoring an ace as 11, depending on the hand.
   */
  private final int rankMinScore;
  
  
  /**
   * The max score of a card of a specific rank in blackjack.
   * For example an Ace would return a value of 11, even though the rules of the
   * game may only permit scoring an ace as a 1 in a specific hand.
   */
  private final int rankMaxScore;
  
  /**
   * 
   * @return the string on the face of the card. For example "K" would be returned for a KING, and
   * "10" would be returned for a 10.
   */
  public String getFace() {
    return face;
  }
  
  /**
   * @return The minimum score of a card of a rank in blackjack.
   * For example an Ace will return a value of 1, even though the rules of the
   * game may permit scoring an ace as 11 when it is part of a particular hand.
   */
  public int getRankMinScore() {
    return this.rankMinScore;
  }
  
  
  /**
   * 
   * @return The max score of a hand of a rank in blackjack.
   * For example an Ace will return a value of 11, even though the rules of the 
   * game may permit only a value of 1 when part of a particular hand.
   */
  public int getRankMaxScore() {
    return this.rankMaxScore;
  }
  
  /**
   * Construct using a minimum score that is equal to the maximum score.
   * 
   * @param face
   * @param score
   */
  private Rank(String face, int score) {
    this.face = face;
    this.rankMinScore = score;
    this.rankMaxScore = score;
  }
  
  
  /**
   * Construct using a minimum score that is potentially
   * different than the maximum score.
   * 
   * Blackjack rules specify that this constructor
   * should only be used for Aces.
   * 
   * @param face
   * @param minScore
   */
  private Rank(String face, int minScore, int maxScore) {
    this.face = face;
    this.rankMinScore = minScore;
    this.rankMaxScore = maxScore;
  }
  
  
}
