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
import com.ibm.vie.blackjack.casino.CardManager;
import com.ibm.vie.blackjack.casino.card.Rank;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.player.TableRules;

/**
 * Specific type of {@link CardManager} that registers a @{link CardSwitchCardChooser}
 * {@link CardSwitchCardChooser#chooseCard(CardManagerViewer)} is called each time a
 * card is drawn from the deck. The called method can then decide which card should be
 * drawn from the available cards, and has the option of shuffling the deck if necessary.
 * 
 * This type of manager is designed make it easy to easy to create specific game scenarios,
 * in order to test the function of a strategy or user interface. 
 * 
 * @author ntl
 *
 */
public class CardSwitchCardManager extends CardManager implements CardManagerViewer {
  private final CardSwitchCardChooser chooser;

  /**
   * 
   * @param rules    the rules that are in effect for this table
   * @param seed     seed for generation of the order of the cards drawn
   * @param chooser  a mechanism for intercepting the next card drawn and
   *                 choosing a different card
   */
  public CardSwitchCardManager(final TableRules rules, final int seed,
      final CardSwitchCardChooser chooser) {
    super(rules, seed);
    this.chooser = chooser;
  }


  /**
   * Builds a map of the available cards remaining in the deck, by rank of cards.
   * It is possible that all ranks may not be included in the map if all cards for a
   * particular rank are in play or in the discard list.
   * 
   * A shuffle of the deck will make cards previously in the discard list included in
   * the map generated by the next call to this method.
   * 
   * Cards that are active will never be included in this map.
   * 
   * @return map of rank -> list of available cards of that rank.
   * 
   */
  public Map<Rank, Collection<VieCard>> getRankToCardsMap() {
    return super.getRankToCardsMap();
  }
  
  
  /**
   * 
   * @return the next card that will be drawn if no intervention occurs. 
   */
  public VieCard peek() {
    return super.peek();
  }

  
  /**
   * Chooses the next card to draw by calling {@link CardSwitchCardChooser#chooseCard(CardManagerViewer)}
   */
  @Override
  protected VieCard chooseCardToDraw() {
    final VieCard choice = chooser.chooseCard(this);
    if (choice == null) {
      return super.chooseCardToDraw();
    } else {
      return choice;
    }
  }

}
