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
 * 
 * <p>
 * Contains current information about a game of blackjack
 * </p>
 * <p>
 * A game of blackjack is defined as one or more rounds of play, where each round has one or more
 * hands of cards that are played against the dealer.
 * </p>
 *
 * @author ntl
 *
 *
 */
public class GameInfo {
  private final int availableMoney;
  private final TableRules rules;
  private final int roundNumber;
  private final List<Card> cardsInDiscardTray;

  /**
   * Constructs the game information
   *
   * @param availableMoney current amount of available money
   * @param rules the rules of the table
   * @param roundNumber the current or most recently completed round number
   * @param cardsInDiscardTray list of cards in the discard tray
   */
  public GameInfo(final int availableMoney, final TableRules rules, final int roundNumber,
      final List<Card> cardsInDiscardTray) {
    this.availableMoney = availableMoney;
    this.rules = rules;
    this.roundNumber = roundNumber;
    this.cardsInDiscardTray = cardsInDiscardTray;
  }


  /**
   * returns the available money that can be used for betting.
   *
   * @return available money
   */
  public int getAvailableMoney() {
    return availableMoney;
  }

  /**
   * Returns the rules for the table.
   * <p> We provided convenience methods in the {@link GameInfo} object
   * for the methods we though a strategy would be most likely to use.
   * This method allows the strategy to access information that is
   * constant for the competition, in case that is interesting.</p>
   *
   * @return the rules for the table
   */
  public TableRules getTableRules() {
    return rules;
  }

  /**
   * returns the number of the round that is currently being played.
   *
   * @return current round number
   */
  public int getRoundNumber() {
    return roundNumber;
  }

  /**
   * Cards in the discard tray are not available to be dealt until a shuffle occurs. Cards that are
   * currently in the dealer's hand or player's hand(s) are not in the discard tray. Cards that are
   * waiting to be dealt are not in the discard tray.
   *
   * This information can be used for card counting; especially when combined with the number of
   * decks used {@link GameInfo#getNumberOfDecks()}, and also the active cards on the table
   * (player's hand(s) and dealer's up card).
   *
   * @return the list of cards in the discard tray
   */
  public List<Card> getCardsInDiscardTray() {
    return cardsInDiscardTray;
  }

  /**
   * Convenience method to retrieve the minimum bet from the table rules
   * 
   * @see TableRules#getMinBet()
   * @return the minimum bet
   */
  public int getMinBet() {
    return this.getTableRules().getMinBet();
  }

  /**
   * Convenience method to retrieve the maximum bet from the table rules
   * 
   * @see TableRules#getMaxBet()
   * @return the minimum bet
   */
  public int getMaxBet() {
    return this.getTableRules().getMaxBet();
  }


  /**
   * Convenience method to retrieve the maximum number of rounds from the table rules
   * 
   * @see TableRules#getMaxNumRounds()
   * @return the maximum number of rounds
   */
  public int getMaxNumRounds() {
    return this.getTableRules().getMaxNumRounds();
  }


  /**
   * Convenience method to retrieve the number of decks from the table rules
   * 
   * @see TableRules#getNumberOfDecks()
   * 
   * @return number of decks
   */
  public int getNumberOfDecks() {
    return this.getTableRules().getNumberOfDecks();
  }

  /**
   * Returns the string representation of this object.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Available Money = " + this.availableMoney + "\n");
    sb.append("Round Number = " + this.getRoundNumber());
    return sb.toString();
  }

}
