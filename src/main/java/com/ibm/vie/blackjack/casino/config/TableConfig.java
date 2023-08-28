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
package com.ibm.vie.blackjack.casino.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.vie.blackjack.casino.CardManager;
import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.casino.crooked.CardSwitchCardChooser;
import com.ibm.vie.blackjack.casino.crooked.CardSwitchCardManager;
import com.ibm.vie.blackjack.casino.rules.RochesterMnCasinoRules;
import com.ibm.vie.blackjack.player.CasinoRules;
import com.ibm.vie.blackjack.player.PlayerStrategy;
import com.ibm.vie.blackjack.player.TableRules;

/**
 * 
 * Configuration for a particular table in blackjack.
 * The configuration defines everything needed to construct the game rules and card deck.
 * 
 * @author ntl
 *
 */
public class TableConfig {
  public static final ObjectMapper mapper = new ObjectMapper();
  private final int initialMoney;
  private final int minBet;
  private final int maxBet;
  private final int numDecks;
  private final int deckNumber;
  private final int numRounds;
  private final String name;
  
  private static final String DEFAULT_TABLE_NAME = "Table with no name";
  
  /**
   * Constructor intended to be used to construct the config from a json 
   * 
   * @param initialMoney
   * @param minBet
   * @param maxBet
   * @param numDecks
   * @param numRounds
   * @param deckNumber
   */
  @JsonCreator
  public TableConfig(
		  @JsonProperty("name") final String name,
		  @JsonProperty("initialMoney") final int initialMoney, 
		  @JsonProperty("minBet") final int minBet, 
		  @JsonProperty("maxBet") final int maxBet, 
		  @JsonProperty("numDecks") final int numDecks,
		  @JsonProperty("numRounds") final int numRounds,
		  @JsonProperty("deckNumber") final int deckNumber) {
    this.name = (name == null || name.isEmpty()) ? DEFAULT_TABLE_NAME : name;
    this.initialMoney = initialMoney;
    this.minBet = minBet;
    this.maxBet = maxBet;
    this.numDecks = numDecks;
    this.numRounds = numRounds;
    this.deckNumber = deckNumber;
  }
  
  /**
   * Constructor intended to be used to construct the config from a json 
   * 
   * @param initialMoney
   * @param minBet
   * @param maxBet
   * @param numDecks
   * @param numRounds
   * @param deckNumber
   */
  public TableConfig(final int initialMoney, 
                    final int minBet, 
                    final int maxBet, 
                    final int numDecks,
                    final int numRounds,
                    final int deckNumber) {
    this.initialMoney = initialMoney;
    this.minBet = minBet;
    this.maxBet = maxBet;
    this.numDecks = numDecks;
    this.numRounds = numRounds;
    this.deckNumber = deckNumber;
    this.name = DEFAULT_TABLE_NAME;
  }
  
  
  /**
   * 
   * @return the initial money that the player will have when starting at the table.
   */
  public int getInitialMoney() {
    return initialMoney;
  }
  
  /**
   * 
   * @return minimum bet allowed
   */
  public int getMinBet() {
    return minBet;
  }
  
  /**
   * 
   * @return maximum bet allowed
   */
  public int getMaxBet() {
    return maxBet;
  }
  
  /**
   * 
   * @return number of decks used by the card manager
   */
  public int getNumDecks() {
    return numDecks;
  }

  /**
   * 
   * @return max number of rounds the player is allowed to play.
   */
  public int getNumRounds() {
    return numRounds;
  }
  
  
  /**
   * 
   * @return seed to use for initial shuffle of the deck
   */
  public int getDeckNumber() {
    return deckNumber;
  }
  
  
  /**
   * Returns a table to play blackjack on, given the rules in the
   * config and the strategy.
   * 
   * @param strategy - the strategy that the player will use with the table
   * @param houseRules - custom house rules for the competition
   * 
   * @return the table to play blackjack games on
   */
  @JsonIgnore
  public Table getTable(PlayerStrategy strategy, CasinoRules houseRules) {
    TableRules  rules = new TableRules(initialMoney, minBet, maxBet, numRounds, numDecks, houseRules);
    CardManager deck = new CardManager(rules, deckNumber);
    
    return new Table(strategy, deck, rules);
  }
  
  
  /**
   * Returns the name of the table. This could be something descriptive, or could be a 
   * just for fun name that is used to describe the table.
   * 
   * @return name of the table
   */
  public String getName() {
    return name;
  }
  
  /**
   * Returns a table to play blackjack on, given the rules in the
   * config and the strategy.
   * 
   * @param strategy - the strategy that the player will use with the table
   * 
   * @return the table to play blackjack games on
   */
  @JsonIgnore
  public Table getTable(PlayerStrategy strategy) {
    TableRules  rules = new TableRules(initialMoney, minBet, maxBet, numRounds, numDecks, new RochesterMnCasinoRules());
    CardManager deck = new CardManager(rules, deckNumber);
    
    return new Table(strategy, deck, rules);
  }
  
  
  /**
   * Returns a table to play blackjack on, given the rules in the
   * config and the strategy.
   * 
   * @param strategy - the strategy that the player will use with the table
   * @param cardSelector - implementation to choose which cards will be drawn
   * @param houseRules - custom rules of the casino
   * 
   * @return the table to play blackjack games on
   */
  @JsonIgnore
  public Table getTable(PlayerStrategy strategy, CardSwitchCardChooser cardSelector, CasinoRules houseRules) {
    TableRules  rules = new TableRules(initialMoney, minBet, maxBet, numRounds, numDecks, houseRules);
    CardManager deck = new CardSwitchCardManager(rules, deckNumber, cardSelector);    
    return new Table(strategy, deck, rules);
  }
  
  
  /**
   * Returns a table to play blackjack on, given the rules in the
   * config and the strategy.
   * 
   * @param strategy - the strategy that the player will use with the table
   * @param cardSelector - implementation to choose which cards will be drawn
   * 
   * @return the table to play blackjack games on
   */
  @JsonIgnore
  public Table getTable(PlayerStrategy strategy, CardSwitchCardChooser cardSelector) {
    TableRules  rules = new TableRules(initialMoney, minBet, maxBet, numRounds, numDecks, new RochesterMnCasinoRules());
    CardManager deck = new CardSwitchCardManager(rules, deckNumber, cardSelector);
    
    return new Table(strategy, deck, rules);
  }
  
  /**
   * Returns a string to represent the TableConfig, which is the name of the TableConfig.
   * 
   * @return The name of the TableConfig
   */
  public String toString() {
	  return name;
  }
  
}
