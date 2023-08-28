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
package com.ibm.vie.blackjack.casino.crooked;

import java.util.Collection;
import java.util.Map;
import com.ibm.vie.blackjack.casino.card.Rank;
import com.ibm.vie.blackjack.casino.card.VieCard;

/**
 * This provides a view of the cards that are available to be dealt
 * 
 * @author ntl
 *
 */
public interface CardManagerViewer {
  /**
   * 
   * @return map of cards available to be drawn from the deck, by rank
   */
  public Map<Rank, Collection<VieCard>> getRankToCardsMap();
  
  /**
   * 
   * @return the next card that will be drawn if there is no intervention
   */
  public VieCard peek();
  
  /**
   * Shuffle the deck. This may change the values returned by
   * {@link #peek()} and {@link #getRankToCardsMap()}
   */
  public void shuffle();
}
