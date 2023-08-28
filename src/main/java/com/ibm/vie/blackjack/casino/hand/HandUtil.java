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
package com.ibm.vie.blackjack.casino.hand;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.ibm.vie.blackjack.player.TableRules;

/**
 * Utility class for processing the dealer and player hands
 *
 *
 * @author ntl
 *
 */
public class HandUtil {

  /**
   * Method to determine the winner of a hand
   *
   * @param dealer - dealer's hand
   * @param player - player's hand
   * @return
   */
  public static HandOutcome compareDealerHandWithPlayer(final VieDealerHand dealer,
      final ViePlayerHand player) {
    if (player.isBusted()) {
      /* The dealer doesn't even have to play if the player busts */
      return HandOutcome.DEALER_WIN;
    }

    /* Handle Blackjack */
    if (player.isBlackJack() || dealer.isBlackJack()) {
      if (player.isBlackJack() && dealer.isBlackJack()) {
        return HandOutcome.PUSH;
      } else if (!player.isBlackJack()) {
        return HandOutcome.DEALER_WIN;
      } else {
        return HandOutcome.PLAYER_WIN_W_BLACKJACK;
      }
    }

    // dealer busted and player not busted
    if (dealer.isBusted()) {
      return HandOutcome.PLAYER_WIN;
    }


    /* Compare best scores of the hand */
    if (player.getScore() > dealer.getScore()) {
      return HandOutcome.PLAYER_WIN;
    } else if (player.getScore() < dealer.getScore()) {
      return HandOutcome.DEALER_WIN;
    } else {
      return HandOutcome.PUSH;
    }

  }


  /**
   * Returns a list of player's hands with outcomes and payout after comparing to the dealer's hand
   *
   * @param viePlayerHands the hands for the player (without outcome information)
   * @param vieDealerHand dealer's hand
   * @param rules rules for the table
   *
   * @return unmodifiable list of hands with payout and outcome information
   */
  public static List<ViePlayerHandPayout> calculatePayouts(final List<ViePlayerHand> viePlayerHands,
      final VieDealerHand vieDealerHand, final TableRules rules) {
    final List<ViePlayerHandPayout> payouts = new LinkedList<>();

    for (final ViePlayerHand hand : viePlayerHands) {
      int payout = 0;
      final HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(vieDealerHand, hand);
      switch (outcome) {
        case DEALER_WIN:
          // don't pay anything back to the player
          break;
        case PLAYER_WIN:
          payout = hand.getBetPaid() * 2;
          break;
        case PLAYER_WIN_W_BLACKJACK:
          payout = hand.getBetPaid()
              + (int) Math.ceil((hand.getBetPaid()) * rules.getCompetitionRules().getBlackJackPayOut());
          break;
        case PUSH:
          // return the bet, plus the rate to pay for a push
          payout = hand.getBetPaid() + (int) Math.ceil(hand.getBetPaid() * rules.getCompetitionRules().getPayoutForPush());
          break;
        default:
          break;
      }
      payouts.add(new ViePlayerHandPayout(payout, outcome, hand));
    }

    return Collections.unmodifiableList(payouts);
  }


  /**
   * Check to see a list of player hands has at least one hand without a bust.
   *
   * @param viePlayerHands list of player's hands
   * @return true if at least one hand has not busted.
   */
  public static boolean playerHasAtLeastOneHandWithoutBust(final List<ViePlayerHand> viePlayerHands) {
    for (final ViePlayerHand hand : viePlayerHands) {
      if (!hand.isBusted()) {
        return true;
      }
    }
    return false;
  }


  /**
   * Searches for the first hand in a list of hands that a decision can be made on. Since any time a
   * decision can be made, a hit is a valid one, this will search for the ability to hit.
   *
   * @return a PlayerHand or null if there is no current hand to make a decision on.
   */
  public static ViePlayerHand findHandForPlayerTurn(final List<ViePlayerHand> viePlayerHands) {
    for (final ViePlayerHand hand : viePlayerHands) {
      if (hand.getRulesAllowHit()) {
        return hand;
      }
    }
    return null;
  }

}
