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
package com.ibm.vie.blackjack.casino;

import com.ibm.vie.blackjack.casino.exceptions.BlackjackRuleViolationException;
import com.ibm.vie.blackjack.casino.exceptions.InvalidBetException;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;

/**
 * Utility class that contains methods to verify blackjack operations are valid for a particular
 * table.
 * 
 * @author ntl
 *
 */
public class BlackjackRuleUtils {

  /**
   * Method to determine if an initial bet is valid.
   * 
   * @param Table
   * @param bet
   * 
   *        throws BlackjackRuleViolationException if the bet is invalid
   */
  public static void checkInitialBetIsLegal(Table table, int bet) throws InvalidBetException {
    if (bet <= 0) {
      throw new InvalidBetException("The Bet " + bet + " is less than or equal to 0");
    } else if (bet < table.getRules().getMinBet() || bet > table.getRules().getMaxBet()) {
      throw new InvalidBetException("Bet " + bet + " is outside the range of the rules");
    } else if (bet > table.getAvailableMoney()) {
      throw new InvalidBetException(
          "Bet " + bet + " is greater than available funds " + table.getAvailableMoney());
    } else {
    }
  }


  /**
   * Check that a hit is legal for a specific player hand
   * 
   * @param hand
   * @throws BlackjackRuleViolationException
   */
  public static void checkHitIsLegal(ViePlayerHand hand) throws BlackjackRuleViolationException {
    if (!hand.getRulesAllowHit()) {
      throw new BlackjackRuleViolationException("Illegal Hit for " + hand.toString());
    }
  }


  /**
   * Check that it is legal to double down
   * 
   * @param table
   * @param hand
   * @throws BlackjackRuleViolationException
   */
  public static void checkDoubleDownIsLegal(Table table, ViePlayerHand hand)
      throws BlackjackRuleViolationException {
    if (!hand.rulesAllowDoubleDown()) {
      throw new BlackjackRuleViolationException(
          "You are not allowed to Double Down on that hand!\n" + hand.toString());
    }

    if (table.getAvailableMoney() < hand.getBetPaid()) {
      throw new InvalidBetException("You have " + table.getAvailableMoney()
          + ", which is not enough money to double down on that hand!\n" + hand.toString());
    }
  }


  /**
   * 
   * Check that a split is legal
   * 
   * @param table
   * @param hand
   * @throws BlackjackRuleViolationException
   */
  public static void checkSpiltIsLegal(Table table, ViePlayerHand hand)
      throws BlackjackRuleViolationException {
    if (!hand.rulesAllowSplit()) {
      throw new BlackjackRuleViolationException("Blackjack rules do not allow a split of that hand!\n" + hand.toString());
    }

    if (table.getAvailableMoney() < hand.getBetPaid()) {
      throw new InvalidBetException("You have " + table.getAvailableMoney() + " which is not enough to split!\n" + hand.toString());
    }
  }


  /**
   * 
   * Check that a stand is legal
   * 
   * @param hand
   * @throws BlackjackRuleViolationException
   */
  public static void checkStandIsLegal(ViePlayerHand hand) throws BlackjackRuleViolationException {
    // A stand is always legal, unless it is not legal to hit
    if (!hand.getRulesAllowHit()) {
      throw new BlackjackRuleViolationException(
          "Illegal Stand request for hand " + hand.toString());
    }
  }


}
