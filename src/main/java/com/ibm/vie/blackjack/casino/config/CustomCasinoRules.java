/*
 * 
 * Copyright (c) 2017, 2018 IBM Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package com.ibm.vie.blackjack.casino.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.vie.blackjack.player.CasinoRules;

public class CustomCasinoRules implements CasinoRules {
  private final String description;
  private final double blackJackPayout;
  private final boolean dealerHitsOnSoft17;
  private final int deckPenetration;
  private final boolean useRealCasinoRulesWhenOutOfCards;
  private final double payoutForPush;


  /**
   * Build custom casino rules
   * 
   * @param blackJackPayout
   * @param dealerHitsOnSoft17
   * @param deckPenetration
   * @param useRealCasinoRulesWhenOutOfCards
   * @param payoutForPush
   */
  @JsonCreator
  public CustomCasinoRules(@JsonProperty("blackJackPayOut") final int blackJackPayout,
      @JsonProperty("dealerHitsOnSoft17") final boolean dealerHitsOnSoft17,
      @JsonProperty("deckPenetration") final int deckPenetration,
      @JsonProperty("useRealCasinoRulesWhenOutOfCards") final boolean useRealCasinoRulesWhenOutOfCards,
      @JsonProperty("payoutForPush") final double payoutForPush,
      @JsonProperty("description") final String description) {

    this.description = description != null ? description : "";
    this.blackJackPayout = blackJackPayout;
    this.dealerHitsOnSoft17 = dealerHitsOnSoft17;
    this.deckPenetration = deckPenetration;
    this.useRealCasinoRulesWhenOutOfCards = useRealCasinoRulesWhenOutOfCards;
    this.payoutForPush = payoutForPush;

  }

  @Override
  public String getDescription() {
    return this.description;
  }
  
  @Override
  public double getBlackJackPayOut() {
    return this.blackJackPayout;
  }

  @Override
  public boolean getDealerHitsOnSoft17() {
    return this.dealerHitsOnSoft17;
  }

  @Override
  public int getDeckPenetration() {
    return this.deckPenetration;
  }

  @Override
  public boolean getUseRealCasinoRulesWhenOutOfCards() {
    return this.useRealCasinoRulesWhenOutOfCards;
  }

  @Override
  public double getPayoutForPush() {
    return this.payoutForPush;
  }

}
