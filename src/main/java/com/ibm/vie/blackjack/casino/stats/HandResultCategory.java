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
package com.ibm.vie.blackjack.casino.stats;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * The category of the result of a hand
 * 
 * <p>
 * This is a little more specific than {@link HandOutcome}, which is only concerned with the outcome
 * in terms of what is payed back to the player. This stat provides separation (for example) between
 * a loss due to a "BUST" and a loss due to a Dealer Blackjack.
 * </p>
 * 
 * @author ntl
 *
 */
public enum HandResultCategory {
  PLAYER_BLACKJACK, PLAYER_WIN_NO_BLACKJACK, PUSH, PLAYER_LOSE_DEALER_BLACKJACK, PLAYER_LOSE_NO_BUST, PLAYER_LOSE_BUST;


  /**
   * This comparator can be used to order Blackjack categories.
   * The "Best" results are higher up in the order, although what is "best" is slightly arbitrary.
   * 
   */
  public static final Comparator<HandResultCategory> descendingOutcome =
      new Comparator<HandResultCategory>() {
        private List<HandResultCategory> order = Arrays.asList(HandResultCategory.PLAYER_BLACKJACK,
            HandResultCategory.PLAYER_WIN_NO_BLACKJACK, HandResultCategory.PUSH,
            HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK, HandResultCategory.PLAYER_LOSE_NO_BUST,
            HandResultCategory.PLAYER_LOSE_BUST);

        @Override
        public int compare(HandResultCategory arg0, HandResultCategory arg1) {
          return Integer.compare(order.indexOf(arg0), order.indexOf(arg1));
        }

      };

}
