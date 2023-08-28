/*
 * Copyright (c) 2018,2018 IBM Corporation Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.vie.blackjack.player;


/**
 * Defines the casino rules for blackjack
 *
 * <p>
 * These rules are defined once in the competition documentation, and never change. The fame work
 * supports using different sets of casino rules so that different rule configurations can be
 * simulated. Competitions will always use the same set of values.
 * </p>
 *
 * @author ntl
 *
 */
public interface CasinoRules {

  /**
   * returns the multiplier for a black jack
   * <p>
   * Blackjack pays more than ordinary wins. How much more is expressed as a ratio, such as 2/1 or
   * 3/2.
   * <p>
   * Under the competition rules, a 2.0 value is used, meaning that if you bet $2 and you win, you
   * will be paid $6 ($2 for the initial bet and $4 for the blackjack) for a profit of $4.
   * </p>
   *
   *
   * @return the multiple that a blackjack pays in relation to the hand's bet
   */
  double getBlackJackPayOut();

  /**
   * returns whether the dealer is allowed to hit on a soft 17.
   *
   * <p>
   * Controls whether or not the dealer will hit on a soft 17. A soft 17 is a point score of exactly
   * 17 where an ace is used as an 11.
   * </p>
   * <p>
   * Allowing the dealer to hit is typically an advantage to the house. Seventeen is a week score,
   * and another card will not make the dealer bust.
   * </p>
   * <p>
   * Under the competition rules, the dealer is not allowed to hit on a soft 17.
   * </p>
   * 
   * @return whether or not the dealer is allowed to hit on soft 17
   */
  boolean getDealerHitsOnSoft17();


  /**
   * returns a percentage of the deck must be dealt before deciding to shuffle
   *
   * <p>
   * Deck Penetration is the percentage (as an integer) of cards dealt after which the dealer
   * shuffles the deck at the end of a round.
   *
   * For example if this value is 50, and a single deck is used then the dealer will shuffle the
   * deck if fewer than 26 undealt cards are in the deck at the end of a round.
   *
   * A high value makes card counting easier and more effective, while a low value causes more
   * shuffling and makes card counting less effective.
   *
   * The dealer will ALWAYS shuffle the deck if a card needs to be drawn and no cards are available
   * in the deck. When this shuffle occurs, the behavior is dependent on the value return by
   * {@link #getUseRealCasinoRulesWhenOutOfCards()}.
   *
   * A value of 100 or higher implies that the deck is only shuffled when absolutely necessary.
   * 
   * </p>
   *
   * <p>
   * Under the competition rules, the deck is only shuffled when absolutely necessary.
   * </p>
   * 
   * @return integer indicating how often to shuffle the deck.
   */
  int getDeckPenetration();


  /**
   * If deck is shuffled mid-round, will it be shuffled again at the end of the round?
   * 
   * <p>
   * In a real Casino, if there are no more cards to be dealt:
   * </p>
   * <OL>
   * 
   * <LI>the cards in the discard tray are shuffled and placed in the shoe</LI>
   * <LI>a card is removed (burned) from the shoe and discarded (hidden)</LI>
   * <LI>The rest of the round is played</LI>
   * <LI>At the end of the round, the entire deck (including the removed card) is shuffled again, so
   * that it is complete.</LI>
   * </OL>
   * 
   * <p>
   * This process almost never happens in practice, because {@link #getDeckPenetration()} will be
   * small enough that the dealer will not run out of cards. But in our game, the penetration may be
   * high enough where this happens much more often at the easier levels.
   * <p>
   * This Casino behavior is documented at:
   * https://wizardofvegas.com/forum/gambling/blackjack/21759-what-would-happen-if-a-shoe-ran-out-of-cards/
   * </p>
   * 
   * 
   * When this value is false:
   * <OL>
   * <li>A card is NOT discarded from the deck after the mid round shuffle</li>
   * <li>The deck is NOT shuffled again at the end of the round</LI>
   * </OL>
   * 
   * When this value is true:
   * <OL>
   * <li>A card IS discarded from the deck after the mid round shuffle. The player does not see
   * which card is discarded, and so the card is not included in the cards returned from
   * {@link GameInfo#getCardsInDiscardTray()}</li>
   * <li>The deck IS shuffled again at the end of the round (it will now have all cards in the deck)
   * </LI>
   * </OL>
   * 
   * <p>
   * A value of true makes card counting less effective, because the next round has more undealt
   * cards in the deck, and there is a hidden card that will never be played in the current round.
   * <p>
   * 
   * @return true if the real casino rules are used. false if the easy rules are used
   */
  boolean getUseRealCasinoRulesWhenOutOfCards();

  /**
   * Returns the rate at which a player should be payed for a PUSH
   * <p>
   * A push normally has a rate of 0, meaning a tie is a tie - the player neither wins or loses
   * money. This configuration allows it to be changed to a rate of -1 meaning a tie goes to the
   * dealer, or +1, meaning a tie goes to the player.
   *
   * Partial in-between values are allowed, for example a value of .5 means that the player is payed
   * their bet + 1/2 of their bet for a push.
   * </p>
   *
   * <p>
   * For the competition, a .5 value is used. This value makes it significantly easier for the
   * player to walk away with a profit.
   * </p>
   *
   * @return the rate to use for payout's on a push.
   */
  double getPayoutForPush();

  /**
   * Each casino rule definition is required to have a human readable description of what those
   * rules are.
   * 
   * <p>
   * An example might be "Rules that have a high blackjack payout and high deck penetration so that
   * solutions that utilize card counting are likely to win"
   * </p>
   * 
   * @return Some descriptive text about the intent of the casino rules.
   */
  String getDescription();

}
