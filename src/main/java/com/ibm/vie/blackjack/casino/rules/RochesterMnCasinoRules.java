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
 * In Rochester, MN, we play blackjack a little different.
 * 
 * <ul>
 * <li>A tie (push) wins 1/2 the bet as profit for the player (instead of the usual 0)</li>
 * <li>Blackjack pays 2 to 1 (instead of the usual 3 to 2)</li>
 * </ul>
 * 
 * These rules do not vary and are used for the ENTIRE competition.
 * @author ntl
 *
 */
public class RochesterMnCasinoRules implements CasinoRules {

  @Override
  public double getBlackJackPayOut() {
    return 2.0;
  }

  @Override
  public double getPayoutForPush() {
    return .5;
  }

  @Override
  public boolean getDealerHitsOnSoft17() {
    return false;
  }

  @Override
  public int getDeckPenetration() {
    return 100;
  }

  @Override
  public boolean getUseRealCasinoRulesWhenOutOfCards() {
    return false;
  }

  @Override
  public String getDescription() {
    return "IBM VIE Basic Rules";
  }

 

}
