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
package com.ibm.vie.blackjack.casino.observer;

import java.util.List;

import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.exceptions.BlackjackRuleViolationException;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHandPayout;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;

/**
 * Observers of a blackjack game have their methods called at predefined intervals during the game.
 *
 *
 * @author ntl
 *
 */
public interface TableObserver {

  /**
   * Called after a decision has been made but before it has been applied to a hand. For example if
   * the player HITs, this is called BEFORE a card is drawn to be added to the current hand.
   *
   * @param gameInfo
   * @param decision
   * @param currentHand
   * @param dealerUpCard
   * @param allPlayerHands
   */
  public default void observeDecisionMade(final GameInfo gameInfo, final PlayerDecision decision,
      final ViePlayerHand currentHand, final VieCard dealerUpCard,
      final List<ViePlayerHand> allPlayerHands) {}


  /**
   * Called after a decision has been applied to a hand. For example if the player HITs, this is
   * called after a card has been drawn and added to the current hand.
   *
   * @param gameInfo
   * @param decision
   * @param currentHand
   * @param dealerUpCard
   * @param allPlayerHands
   */
  public default void observeDecisionOutcome(final GameInfo gameInfo, final PlayerDecision decision,
      final ViePlayerHand currentHand, final VieCard dealerUpCard,
      final List<ViePlayerHand> allPlayerHands) {}

  
  /**
   * Called just after the dealer flips over the down card
   * 
   * @param gameInfo
   * @param dealerUpCard
   * @param allPlayerHands
   */
  public default void observeDealerTurn(final GameInfo gameInfo,
      final VieDealerHand dealerHand,
      final List<ViePlayerHand> allPlayerHands) {}

  /**
   * Called when a round is over
   *
   * @param gameInfo
   * @param vieDealerHand
   * @param hands
   */
  public default void observeEndOfRound(final GameInfo gameInfo, final VieDealerHand vieDealerHand,
      final List<ViePlayerHandPayout> hands) {}

  /**
   * Called after the player makes a legal initial bet.
   *
   * @param gameInfo
   * @param betAmount
   */
  public default void observeInitialBet(final GameInfo gameInfo, final int betAmount) {};

  /**
   * Called before a player makes a decision on a hand.
   *
   * @param gameInfo
   * @param currentHand
   * @param dealerUpCard
   * @param allPlayerHands
   */
  public default void observePlayerTurn(final GameInfo gameInfo, final ViePlayerHand currentHand,
      final VieCard dealerUpCard, final List<ViePlayerHand> allPlayerHands) {};


  /**
   * Called when an exception is received from the strategy This method should decide how to present
   * the error. <BR/>
   *
   * Note that {@link BlackjackRuleViolationException} will be thrown from frame-work to frame-work,
   * so the stack trace will not include the strategy (and thus not be helpful in resolving the
   * problem). Other Exceptions (we hope) are thrown from the strategy, so for these a stack trace
   * will be more useful.
   *
   * @param gameInfo
   * @param allPlayerHands, may be null if the error occurred prior to hands being dealt
   * @param vieDealerHand, may be null if the error occurred prior to hands being dealt
   * @param e
   */

  public default void observeProgramError(final GameInfo gameInfo,
      final List<ViePlayerHand> allPlayerHands, final VieDealerHand vieDealerHand,
      final Exception e) {

  };
  
  
  /**
   * Called when the game ends for any reason
   * 
   * @param gameInfo
   */
  public default void observeGameIsOver(final GameInfo gameInfo) {}
    
}
