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
package com.ibm.vie.blackjack.casino.rules;

import com.ibm.vie.blackjack.player.CasinoRules;

/**
 * Ordinary blackjack rules.
 * These will only slightly favor the player, with correct basic strategy.
 * 
 * <p>
 * These are NOT the rules used in the IBM VIE competition, they are a 
 * more advanced set of house rules that students or developers can play
 * with if they want to see how their algorithm might perform in a real
 * casino.
 * </p>
 * <p>
 * Note that real casinos often have more rules than what is covered
 * here, so this is still somewhat favorable to the player....but
 * it is more ordinary than what we use in class. 
 * </p>
 * @author ntl
 *
 */
public class OrdinaryBlackjackRules implements CasinoRules {

 
  @Override
  public double getBlackJackPayOut() {
    return 3.0 / 2.0;
  }

 
  @Override
  public double getPayoutForPush() {
    return 0;
  }

 
  @Override
  public boolean getDealerHitsOnSoft17() {
    return true;
  }

 

  @Override
  public int getDeckPenetration() {
    return 75;
  }


  @Override
  public boolean getUseRealCasinoRulesWhenOutOfCards() {
    return true;
  }


  @Override
  public String getDescription() {
    return "Ordinary (but generous) blackjack payout values. The deck is shuffled only when necessary";
  }

}
