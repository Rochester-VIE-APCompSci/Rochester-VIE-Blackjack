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

import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.observer.TableObserver;

/**
 * The {@link CardSwitchCardChooser} interface should be implemented by objects 
 * that wish to register themselves with a {@link CardSwitchCardManager}.
 * <BR/>
 * When the framework needs to draw a card {@link #chooseCard(CardManagerViewer)} is
 * called to select the card to draw.
 * <BR/>
 * Users that need to understand the context of the request to draw a card should
 * also implement the {@link TableObserver} interface and register the object with
 * the {@link Table} using {@link Table#addObserver(TableObserver)}. <BR/> 
 * 
 * @author ntl
 *
 */
public interface CardSwitchCardChooser {
  
  /**
   * Called to choose the next card. <BR/>
   * The {@link VieCard} that is returned from this method must be an object that is included
   * in the objects returned by {@link CardManagerViewer#getRankToCardsMap()}. A {@link RuntimeException}
   * will be thrown if this is not the case. <BR/>
   * 
   * The method has the option of shuffling the deck by invoking {@link CardManagerViewer#shuffle()}.
   * This will cause cards in the discard list to be included in the cards returned by
   * {@link CardManagerViewer#getRankToCardsMap()} <BR/>
   * 
   * @param viewer view of the current deck
   * @return the card that should be drawn
   */
  public VieCard chooseCard(CardManagerViewer viewer); 
}
