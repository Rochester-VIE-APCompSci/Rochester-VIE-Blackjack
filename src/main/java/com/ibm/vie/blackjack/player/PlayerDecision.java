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
 * Define constants for player decisions.
 *
 * @author ntl
 *
 */


public enum PlayerDecision {
  /**
   * Deciding to hit means that the current hand will receive another card. A hit is always a legal
   * decision for any hand that allows the player to make a decision.
   */
  HIT,

  /**
   * Deciding to stand means that the current hand will receive no more cards. No more decisions
   * will be allowed for this hand. A stand is always a legal decision for any hand that allows the
   * player to make a decision.
   */
  STAND,

  /**
   * <p>
   * A double down decision means that the player will pay the amount of the initial bet (so the
   * hand has 2x the initial bet paid for it) and receive one and only one additional card.
   * </p>
   * <p>
   * A double down decision is legal only for the first decision of a hand (when the hand has two
   * cards in it).
   * </p>
   */
  DOUBLE_DOWN,

  /**
   * A split decision means that the player splits a hand into two unique hands, at the price of the
   * initial bet. (Each hand in the split now has the initial bet paid for it). Each hand competes
   * against the dealer's hand, with the opportunity to win. Each hand is scored as a unique hand,
   * with a blackjack counting as a blackjack if one occurs.
   *
   * A split is legal if:
   * <ul>
   * <li>There are exactly two cards in the hand and each card is of the same rank</li>
   * <li>The player has at least the initial bet amount of available money at the time of the
   * split</li>
   * </ul>
   * 
   * <p>
   * There is no limit to the number of splits that can occur. For example if a player has a hand
   * (ACE, ACE) and the player decides to split, and the new hands are (ACE, ACE) and (ACE, NINE),
   * the player is allowed to split the (ACE, ACE) hand again, creating three hands.
   * </p>
   */
  SPLIT;
}

