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
 * The TableRules defines the rules for the table.
 *
 * @author ntl
 *
 */
public class TableRules {
  final CasinoRules competitionRules;
  final int initialMoney;
  final int maxBet;
  final int maxNumRounds;
  final int minBet;
  final int numberOfDecks;

  /**
   * Creates a default set of rules
   */
  public TableRules() {
    this(com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.INITIAL_MONEY,
        com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.MIN_BET,
        com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.MAX_BET,
        com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.MAX_NUM_ROUNDS,
        com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.NUMBER_OF_DECKS);
  }


  /**
   * Creates a default set of rules for the table, but sets the casino rules as specified.
   *
   * @param houseRules - the competition rules that are to be used. These will be returned by
   *        {@link #getCompetitionRules()}
   */
  public TableRules(final CasinoRules houseRules) {
    this(com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.INITIAL_MONEY,
        com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.MIN_BET,
        com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.MAX_BET,
        com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.MAX_NUM_ROUNDS,
        com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.NUMBER_OF_DECKS, houseRules);
  }

  /**
   * Creates the configurable rules for the table, using Rochester MN blackjack rules as the casino
   * rules.
   * <p>
   * This is used by the framework to construct the rules at the start of a game.
   * </p>
   *
   * @param initialMoney amount of money that the player starts with
   * @param minBet minimum amount that a player must bet each round
   * @param maxBet maximum amount that the player is allowed to bet
   * @param maxNumRounds maximum number of rounds that can be played
   * @param numberOfDecks number of decks in the game
   *
   * @throws IllegalArgumentException if any of the input parameters are invalid
   */
  public TableRules(final int initialMoney, final int minBet, final int maxBet,
      final int maxNumRounds, final int numberOfDecks) throws IllegalArgumentException {
    this(initialMoney, minBet, maxBet, maxNumRounds, numberOfDecks,
        com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults.ROCHESTER_MN_RULES);
  }

  /**
   * Creates the configurable rules for the table.
   * <p>
   * This is used by the framework to construct the rules at the start of a game.
   * </p>
   *
   * @param initialMoney amount of money that the player starts with
   * @param minBet minimum amount that a player must bet each round
   * @param maxBet maximum amount that the player is allowed to bet
   * @param maxNumRounds maximum number of rounds that can be played
   * @param numberOfDecks number of decks in the game
   * @param casinoRules the house rules to play with
   *
   * @throws IllegalArgumentException if any of the input parameters are invalid
   */
  public TableRules(final int initialMoney, final int minBet, final int maxBet,
      final int maxNumRounds, final int numberOfDecks, final CasinoRules casinoRules)
      throws IllegalArgumentException {

    /* setup the object */
    this.initialMoney = initialMoney;
    this.minBet = minBet;
    this.maxBet = maxBet;
    this.maxNumRounds = maxNumRounds;
    this.numberOfDecks = numberOfDecks;
    competitionRules = casinoRules;

    checkThatRulesAreConsistent();

  }


  /**
   * Enforce that this object has been created with rules that make sense. This is intended to be
   * called from the constructor of the object.
   *
   * @throws IllegalArgumentException
   */
  private final void checkThatRulesAreConsistent() throws IllegalArgumentException {
    if (initialMoney <= 0) {
      throw new IllegalArgumentException("initial Money must be > 0");
    }

    if (minBet > maxBet || minBet <= 0) {
      throw new IllegalArgumentException(
          "Invalid minimum bet, cannot be greater than max bet and must be > 0");
    }

    if (maxNumRounds <= 0) {
      throw new IllegalArgumentException("Invalid number of rounds");
    }

    if (numberOfDecks <= 0) {
      throw new IllegalArgumentException("Invalid number of decks");
    }
  }


  /**
   * Returns the rules that are effective for the competition.
   *
   * <p>
   * These rules are constant for the entire competition. Changing them changes the terms of the
   * contest and can be assumed not to happen. However, this allows the rules that the simulator
   * uses to be changed, so that we can model blackjack in a real Casino, and compare the outcome to
   * our game.
   * </p>
   * 
   * @return the rules that are defined for the competition.
   */
  public CasinoRules getCompetitionRules() {
    return competitionRules;
  }


  /**
   * returns the amount of money that the player initially has for the game
   *
   * @return the initial amount of money available to a player
   */
  public int getInitialMoney() {
    return initialMoney;
  }

  /**
   * returns the maximum amount of money that the player is allowed to bet
   *
   * @return the maximum bet allowed for a single hand at this table
   */
  public int getMaxBet() {
    return maxBet;
  }

  /**
   *
   * @return the maximum number of rounds that can be played at the table
   */
  public int getMaxNumRounds() {
    return maxNumRounds;
  }


  /**
   * returns the minimum amount that must be bet
   *
   * @return the minimum bet allowed at this table
   */
  public int getMinBet() {
    return minBet;
  }

  /**
   * returns the number of decks in use for the game
   *
   * <p>
   * More decks makes card counting more difficult. Typical casinos use between 4 and 8 decks.
   * </p>
   *
   * @return the number of decks that the dealer uses.
   *
   */
  public int getNumberOfDecks() {
    return numberOfDecks;
  }

}
