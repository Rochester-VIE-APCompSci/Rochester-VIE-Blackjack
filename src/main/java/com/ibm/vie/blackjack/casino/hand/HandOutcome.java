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

/**
 * Represents the outcome of the comparison between a player's hand and the dealer's hand
 * 
 * @author ntl
 *
 */
public enum HandOutcome {
  PLAYER_WIN, DEALER_WIN, PLAYER_WIN_W_BLACKJACK, PUSH
}
