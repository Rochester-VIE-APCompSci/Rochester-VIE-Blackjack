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
 * The strategy that the student will implement. The "starter" solution,
 * {@link student.player.MyPlayer MyPlayer} implements this interface.
 *
 *
 * @author ntl
 *
 * @see com.ibm.vie.blackjack.player
 *
 */
public interface PlayerStrategy {

  /**
   * returns a name for the student that has created the strategy.
   * 
   * <p>
   * This is what is used to ensure that the correct student gets credit for the submission.
   * </p>
   * 
   * @return the name of the student that created the strategy in "firstname lastname" format.
   */
  String getStudentName();

  /**
   * returns the starting bet for a hand
   * 
   * <p>
   * This method is called before a hand begins, so that the player can place a bet. No cards have
   * been dealt yet, and therefore the method does not supply card or hand information.
   * </p>
   * <p>
   * The effective {@link TableRules} should be consulted to determine the minimum and maximum bet
   * amounts. See {@link GameInfo#getTableRules()}. {@link GameInfo#getAvailableMoney()} should be
   * consulted to determine the amount of money that is available to bet.
   * </p>
   *
   * @param gameInfo information about the game.
   *
   * @return the amount to bet.
   */

  int placeInitialBet(GameInfo gameInfo);



  /**
   * makes a decision for the current hand
   * 
   * <p>
   * This method makes decisions on how to play blackjack. It will be called once for each decision
   * that needs to be made.
   * </p>
   *
   * <p>
   * The current hand is always the hand that the dealer needs a hit/stand/double/split decision
   * for.
   * </p>
   *
   * <p>
   * A player may have more than one hand on the table (if a hand has been split). All of the
   * player's hands that are on the table are included as a list in the playerHands parameter. These
   * hands are ordered from dealer's left to dealer's right. The current hand is included in the
   * list as the same object (not a copy). Hands in the list that are prior to the current hand have
   * no more decisions to make. Hands after the current hand will become the current hand when all
   * decisions for prior hands are made.
   * </p>
   *
   *
   * @param gameInfo information about the game.
   * @param currentHand the hand that a hit/stand/double-down/split decision must be made for.
   * @param playerHands list of information about the player's hands that are on the table. This
   *        list will include the current hand object.
   * @param dealerUpCard information about the dealer's up card.
   *
   * @return the decision for what to do next.
   */
  PlayerDecision decideHowToPlayHand(GameInfo gameInfo, PlayerHand currentHand,
      List<PlayerHand> playerHands, Card dealerUpCard);


  /**
   * decides whether or not to stop playing the game at the end of a round
   * 
   * <p>
   * This method gets called after the dealer's turn has completed and a round is over. Returning a
   * true value from this function will result in walking away from the table. When a player walks
   * away, there will be no more bets, and the available money will be the player's final score.
   * </p>
   *
   *
   * @param gameInfo information about the game.
   * @param playerHands all completed hands on the table
   * @param dealerHand the dealer's hand including the card that was previously hidden
   *
   * @return true to request that the game ends, false to request another round of betting.
   *
   */
  boolean decideToWalkAway(GameInfo gameInfo, List<PlayerPayoutHand> playerHands,
      DealerHand dealerHand);

}
