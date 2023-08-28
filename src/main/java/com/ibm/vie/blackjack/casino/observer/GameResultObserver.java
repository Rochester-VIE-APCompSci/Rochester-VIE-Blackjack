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

import java.util.LinkedList;
import java.util.List;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.config.TableConfig;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHandPayout;
import com.ibm.vie.blackjack.casino.stats.GameResult;
import com.ibm.vie.blackjack.casino.stats.HandResult;
import com.ibm.vie.blackjack.casino.stats.HandResultCategory;
import com.ibm.vie.blackjack.casino.stats.RoundResult;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.TableRules;

/**
 * Tracks the results of an entire game.
 *
 * This observer tracks all information necessary to build a {@link GameResult} object when
 * requested after a game of Blackjack has completed.
 *
 * @see GameResult
 *
 * @author ntl
 *
 */
public class GameResultObserver implements TableObserver {


  private int chancesToSplit = 0;
  private final TableRules rules;
  private Exception gameEndingError = null;
  private GameResult gameResult;
  private int roundInitialBet;
  private final List<RoundResult> roundResults = new LinkedList<>();
  private final String studentName;

  /**
   * Constructs the observer and associates it with a specific config.
   *
   * @param studentName name of the student
   * @param rules - rules of the table
   */
  public GameResultObserver(final String studentName, final TableRules rules) {
    this.rules = rules;
    this.studentName = studentName;
  }

  /**
   * Retrieves a game result
   *
   * Calling this method is valid only after the Blackgame has been played (with the observer being
   * registered for the game). Calling the method prior to the completion of the game will result in
   * the null value being returned.
   *
   * @return the result of the game
   */
  public GameResult getResult() {
    return gameResult;
  }

  /**
   * Create a round result at the end of the round.
   * <p>
   * This involves iterating through the hands and determining results for each
   * </p>
   */
  @Override
  public void observeEndOfRound(final GameInfo gameInfo, final VieDealerHand vieDealerHand,
      final List<ViePlayerHandPayout> hands) {

    final List<HandResult> currentRoundHandResults = new LinkedList<>();


    for (final ViePlayerHandPayout hand : hands) {
      final HandResult result = new HandResult();

      result.setEarnings(hand.getPayout() - hand.getBetPaid());

      // If the bet for the hand is twice the initial bet, it must have
      // been a double down. I wanted to track the decisions to hands
      // to determine this, but that got complicated for hands that split.
      result.setWasDoubleDown(hand.getBetPaid() == 2 * roundInitialBet);

      switch (hand.getOutcome()) {
        case DEALER_WIN:

          if (vieDealerHand.isBlackJack()) {
            result.setOutcomeStat(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK);
          } else {
            result.setOutcomeStat(hand.isBusted() ? HandResultCategory.PLAYER_LOSE_BUST
                : HandResultCategory.PLAYER_LOSE_NO_BUST);
          }
          break;
        case PLAYER_WIN:
          result.setOutcomeStat(HandResultCategory.PLAYER_WIN_NO_BLACKJACK);
          break;
        case PLAYER_WIN_W_BLACKJACK:
          result.setOutcomeStat(HandResultCategory.PLAYER_BLACKJACK);
          break;
        case PUSH:
          result.setOutcomeStat(HandResultCategory.PUSH);
          break;
      }

      currentRoundHandResults.add(result);
    }

    // create the round result
    final RoundResult result =
        new RoundResult(roundInitialBet, currentRoundHandResults.toArray(new HandResult[0]),
            chancesToSplit, gameInfo.getAvailableMoney());
    roundResults.add(result);
  }

  /**
   * Create the game result object at the end of the game
   *
   */
  @Override
  public void observeGameIsOver(final GameInfo gameInfo) {
    gameResult = new GameResult(gameInfo.getAvailableMoney(), gameInfo.getRoundNumber(),
        roundResults, rules, gameEndingError, studentName);
  }


  /**
   * Cache the initial bet for the round, and reset {@link #chancesToSplit}
   */
  @Override
  public void observeInitialBet(final GameInfo gameInfo, final int betAmount) {
    roundInitialBet = betAmount;
    chancesToSplit = 0;
  }



  /**
   * Book keeping for the player's turn
   *
   * <p>
   * Currently we only keep track of how often a player had the opportunity to split. We have to be
   * a little careful here because there will be more calls to the method than hands on the table.
   * </p>
   *
   * For example:
   * <ul>
   * <li>Player's hand = {EIGHT, EIGHT} first opportunity</LI>
   * <li>Split produces hands = {EIGHT, NINE}, {EIGHT, EIGHT} (no opportunity and opportunity for
   * second hand)</li>
   * </ul>
   *
   * <p>
   * This works out OK for tracking chances to split, but we ever try to map a hand provided to this
   * function to end of round processing, we need to account for the fact that the initial {EIGHT,
   * EIGHT} hand no longer exists!
   * </p>
   */
  @Override
  public void observePlayerTurn(final GameInfo gameInfo, final ViePlayerHand currentHand,
      final VieCard dealerUpCard, final List<ViePlayerHand> allPlayerHands) {

    if (currentHand.getCards().size() == 2
        && currentHand.getCards().get(0).getRank().equals(currentHand.getCards().get(1).getRank())
        && gameInfo.getAvailableMoney() > currentHand.getBetPaid()) {
      chancesToSplit++;
    }

  }


  /**
   * Records a program error that ended the game
   *
   */
  @Override
  public void observeProgramError(final GameInfo gameInfo, final List<ViePlayerHand> allPlayerHands,
      final VieDealerHand vieDealerHand, final Exception e) {
    gameEndingError = e;
  }

}
