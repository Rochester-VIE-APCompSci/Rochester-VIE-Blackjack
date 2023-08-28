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
import java.util.stream.Collectors;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.exceptions.BlackjackRuleViolationException;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHandPayout;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;

/**
 * This observer prints information to console
 *
 * @author ntl
 *
 */
public class PrintToConsoleObserver implements TableObserver {

  /**
   * Print the state of the table after a player decision has taken effect. For example if the
   * Player hits, the additional card will be included in the output
   */
  @Override
  public void observeDecisionOutcome(final GameInfo gameInfo, final PlayerDecision decision,
      final ViePlayerHand currentHand, final VieCard dealerUpCard,
      final List<ViePlayerHand> allPlayerHands) {
    System.out.println("\nDecision=" + decision.toString() + "\n" + gameInfo.toString()
        + "\nDealer Up Card: " + dealerUpCard.toString() + "\nPlayer Hands:\n"
        + prettyPrintPlayerHands(allPlayerHands));

  }

  /**
   * Print the state of the table when the round is over to the console.
   */
  @Override
  public void observeEndOfRound(final GameInfo gameInfo, final VieDealerHand vieDealerHand,
      final List<ViePlayerHandPayout> hands) {
    System.out.println("\n\nEND OF ROUND");
    System.out.println(gameInfo.toString() + "\n" + "Dealer Hand: " + vieDealerHand.toString()
        + "\nPlayer Hands:\n" + prettyPrintPlayerHands(hands));
  }

  @Override
  public void observeInitialBet(final GameInfo gameInfo, final int betAmount) {
    System.out.println("\nINITIAL BET: " + betAmount);
  }

  /**
   * Print the state of the table when a player has a decision to make. The hand requiring a
   * decision will be marked with "**"
   */
  @Override
  public void observePlayerTurn(final GameInfo gameInfo, final ViePlayerHand currentHand,
      final VieCard dealerUpCard, final List<ViePlayerHand> allPlayerHands) {
    System.out.println("\nDecision to make:\n" + gameInfo.toString() + "\nDealer Up Card: "
        + dealerUpCard.toString() + "\nPlayer Hands:\n"
        + prettyPrintPlayerHands(allPlayerHands, currentHand));
  }

  @Override
  public void observeProgramError(final GameInfo gameInfo, final List<ViePlayerHand> allPlayerHands,
      final VieDealerHand vieDealerHand, final Exception e) {
    System.err.println("\n\nERROR ERROR ERROR ERROR\n");

    if (vieDealerHand != null && allPlayerHands != null) {
      System.err.println(gameInfo.toString() + "\n" + "Dealer Hand: " + vieDealerHand.toString()
          + "\nPlayer Hands:\n" + prettyPrintPlayerHands(allPlayerHands) + "\n");
    }

    if (e.getMessage() != null) {
      System.err.println(e.getMessage());
    }

    // rule violations are detected and thrown from frame work to framework, so only include a
    // stack trace if the error appears to be from the student's code
    if (!(e instanceof BlackjackRuleViolationException)) {
      e.printStackTrace();
    }
    System.err.flush();
  }

  /**
   * Convert a player's hands to a string
   *
   * @param hands
   * @return string representation of the list of hands
   */
  protected String prettyPrintPlayerHands(final List<? extends ViePlayerHand> hands) {
    return hands.stream().map(Object::toString).collect(Collectors.joining("\n"));
  }

  /**
   * Convert a player's hands to a string, marking the current hand with '**'
   *
   * @param hands list of hands for the player
   * @param activeHand the current hand for which decisions are to be made for
   * @return string representation of the list of hands
   */
  protected String prettyPrintPlayerHands(final List<? extends ViePlayerHand> hands,
      final ViePlayerHand activeHand) {
    return hands.stream().map((h) -> (h == activeHand) ? "**" + h.toString() : h.toString())
        .collect(Collectors.joining("\n"));
  }

}
