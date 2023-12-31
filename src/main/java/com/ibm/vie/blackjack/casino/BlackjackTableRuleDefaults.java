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

import com.ibm.vie.blackjack.casino.rules.RochesterMnCasinoRules;
import com.ibm.vie.blackjack.player.CasinoRules;

/**
 * 
 * Default values to use for table rules, if no more
 * specific values are available.
 * 
 * 
 * @author ntl
 *
 */
public class BlackjackTableRuleDefaults {

  public static final int NUMBER_OF_DECKS = 1;
  public static final int INITIAL_MONEY = 100;
  public static final int MIN_BET = 1;
  public static final int MAX_BET = 25;
  public static final int MAX_NUM_ROUNDS = 5000;
  public static final double BLACKJACK_PAYOUT = 3.0 / 2.0;

  public static final CasinoRules ROCHESTER_MN_RULES = new RochesterMnCasinoRules();
  
}
