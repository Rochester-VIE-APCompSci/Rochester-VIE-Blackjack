/*
 * Copyright (c) 2018,2018 IBM Corporation Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.vie.blackjack.casino;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.player.TableRules;
import com.ibm.vie.blackjack.casino.card.Rank;
import com.ibm.vie.blackjack.casino.card.Suit;

/**
 * Represents a mechanism for dealing and shuffling one or more decks of cards. <BR/>
 *
 * Cards in the {@link CardManager} an be thought of as being in one of three places:
 * <OL>
 * <LI>The shoe, or location where cards are drawn from as part of the deal</LI>
 * <LI>Live Hands, or cards that are on the table</LI>
 * <LI>The discard tray, where live cards go at the end of a round</LI>
 * </OL>
 *
 * <P>
 * A shuffle of the deck happens automatically when a call to drawCard is made, and there are no
 * more cards in the shoe. The processing for shuffling is: 
 * <ul>
 * <li> all cards in the discard tray are  added to the shoe (if there are any), </li>
 * <li> Any cards that are part of live hands remain part of live hands. </li>
 * <li> All cards in the shoe are shuffled. </li>
 * </ul>
 * </P>
 * <P>
 * In real a casino, the shoe will have a special marker card in it (usually at about 25% away
 * from the last card that will be drawn). When drawn, the marker indicates that a re-shuffle will
 * occur after the completion of the hand. This approach makes card counting much more difficult,
 * because cards from the entire deck are not drawn. Implementation of this feature is not built in,
 * but can be accomplished by calling {@link #shuffle()} explicitly, when
 * {@link #getRemainingCardsInShoe()} reaches some minimum value.
 * </P>
 *
 * <p>
 * When a shuffle happens because a draw occurred and there were no more cards to draw, the
 * {@link com.ibm.vie.blackjack.player.CasinoRules#getUseRealCasinoRulesWhenOutOfCards()} setting
 * needs to be considered. When that feature is on, the deck's {@link #getLastShuffleDueToEmptyDeck()}
 * method is available to indicate that a full shuffle should happen at the end of the round.
 * Additionally, one card is is removed from the shoe as a 'burn' card, which does not appear
 * in the discard list. The card will be returned to the shoe during the next shuffle.
 * </p>
 * 
 * 
 * 
 * @author ntl
 *
 */
public class CardManager {
  private final List<VieCard> shoe = new LinkedList<VieCard>();
  private final List<VieCard> live = new LinkedList<VieCard>();
  private final List<VieCard> discard = new LinkedList<VieCard>();
  private VieCard burnCard = null;
  private final Random rnd;
  private final TableRules rules;
  private boolean lastShuffleWasDueToEmptyDeck = false;

  /**
   * Construct using the specified number of decks
   *
   * @param rules - the table rules that are in effect
   * @param seed seed value to use for the random number generator
   */
  public CardManager(TableRules rules, int seed) {
    this.rules = rules;
    rnd = new Random(seed);
    initializeDecks();
  }


  /**
   * Construct using the specified number of decks
   *
   * @param rules - the table rules that are in effect
   */
  public CardManager(TableRules rules) {
    this.rules = rules;
    rnd = new Random();
    initializeDecks();
  }


  /**
   * Initializes and shuffles the cards in the multi-deck
   *
   */
  private final void initializeDecks() {
    shoe.clear();
    live.clear();
    discard.clear();

    for (int deckNum = 0; deckNum < rules.getNumberOfDecks(); deckNum++) {
      for (final Suit suit : Suit.values()) {
        for (final Rank rank : Rank.values()) {
          shoe.add(new VieCard(rank, suit));
        }
      }
    }

    shuffle();
  }



  /**
   * Shuffle the deck
   *
   * <p>
   * Shuffle all cards in the deck. Must be called when there are no 'live' cards, 
   * e.g. at the end of a round.
   * <p>
   *
   */
  public final void shuffle() {
    shuffle(false);
  }



  /**
   * Shuffle the deck
   * 
   * <p>
   * If the deck is being shuffled because a card was drawn and the deck is empty, some
   * special casino rules may be applied depending on the rules that are in effect.
   * <p>
   * 
   * <OL>In the event that a shuffle must happen because the deck is out of cards a real casino
   * will:
   * <LI> shuffle the discard tray </LI>
   * <LI> discard (burn) one card not seen by the player
   *      (This card will NOT appear in the discard list available to the player) </LI>
   * <LI> indicate that the deck should be fully shuffled at the end of the round </LI>
   * </OL>
   * 
   * 
   * 
   * @param emptyDeck is this an automatic shuffle because the deck is empty?
   * 
   * @see #shuffle()
   * @see com.ibm.vie.blackjack.player.CasinoRules#getUseRealCasinoRulesWhenOutOfCards()
   */
  private final void shuffle(boolean emptyDeck) {
    lastShuffleWasDueToEmptyDeck = emptyDeck;
    shoe.addAll(discard);
    discard.clear();
    if (burnCard != null) {
      shoe.add(burnCard);
      burnCard = null;
    }
    Collections.shuffle(shoe, rnd);

    if (emptyDeck && rules.getCompetitionRules().getUseRealCasinoRulesWhenOutOfCards()) {
      burnCard = shoe.remove(0);
    }
    
  }

  /**
   * Indicates whether the most recent shuffle was because a card was drawn when
   * the deck was empty. This means that the current deck was initially shuffled
   * with cards on the table.
   * 
   * @return true if the deck was shuffled because the deck was empty.
   */
  public boolean getLastShuffleDueToEmptyDeck() {
    return this.lastShuffleWasDueToEmptyDeck;
  }

  /**
   * Orders the deck so that cards will be drawn of the specified ranks in the order specified If
   * this list is shorter than the entire deck, the remaining cards are in an arbitrary non-random
   * order.
   * 
   * @param ranks - ranks for the first n cards
   */
  public void orderDeckByRank(Rank... ranks) {
    Map<Rank, Deque<VieCard>> cardMap = new HashMap<>();
    for (Rank r : Rank.values()) {
      cardMap.put(r, new ArrayDeque<VieCard>());
    }


    for (VieCard c : shoe) {
      cardMap.get(c.getRank()).push(c);
    }

    List<VieCard> deckInOrder = new LinkedList<>();
    for (Rank r : ranks) {
      if (cardMap.get(r).isEmpty()) {
        throw new RuntimeException(
            "There are not enough cards in the deck to produce the order that was requested. "
                + r.toString());
      }
      deckInOrder.add(cardMap.get(r).pop());
    }

    // add the other cards
    for (Rank r : Rank.values()) {
      for (VieCard c : cardMap.get(r)) {
        deckInOrder.add(c);
      }
    }

    shoe.clear();
    shoe.addAll(deckInOrder);

  }



  /**
   * Returns an unmodifiable view over the cards in the discard tray. Because this is a view, This
   * list could change as the deck is updated.
   * 
   * @return an unmodifiable list of cards in the discard tray.
   */
  public List<VieCard> getCardsInDiscardTray() {
    return Collections.unmodifiableList(this.discard);
  }

  /**
   * Collects All live cards and moves them to the discard try
   */
  public void collectLiveCards() {
    discard.addAll(live);
    live.clear();
  }

  /**
   * Draws a card from the deck The card will be traced as live card until it is collected.
   *
   * If no card is available in the shoe {@link shuffle} will be called to fill the shoe. If the
   * shoe is still empty after shuffling, a {@link RuntimeException} will be thrown. An empty shoe
   * indicates a program error in the framework.
   *
   * @return the card that is being drawn
   */
  public VieCard drawCard() {
    if (shoe.isEmpty()) {
      shuffle(true);
      
      if (shoe.isEmpty()) {
        // this is an error, the framework should never allow so many cards to be in play that
        // there are not enough cards in the deck.
        throw new RuntimeException("A card needs to be drawn but the shoe is empty");
      }
    }

    final VieCard drawnCard = chooseCardToDraw();

    if (!shoe.remove(drawnCard)) {
      // the chosen card is not actually in the shoe
      throw new RuntimeException("A card was chosen but the card is not in the shoe");
    }

    live.add(drawnCard);
    return drawnCard;
  }



  /**
   * Choose a card to drawn from the cards in the shoe, this can be overridden by a CardManager that
   * allows the order of the cards drawn to be modified in real time.
   * 
   * @return the card that should be drawn next.
   */
  protected VieCard chooseCardToDraw() {
    return shoe.get(0);
  }


  /**
   * Peek at the top card in the shoe and return that card.
   * 
   * 
   * @return the next card that will be drawn
   */
  protected VieCard peek() {
    if (shoe.isEmpty()) {
      return null;
    } else {
      return shoe.get(0);
    }
  }

  /**
   * Provides a way for a descendant class to obtain all remaining cards in the shoe by rank.
   * 
   * This can be used within {@link #chooseCardToDraw()} to select a remaining card of a particular
   * rank.
   * 
   * @return map of rank to remaining cards in the shoe
   */
  protected Map<Rank, Collection<VieCard>> getRankToCardsMap() {
    Map<Rank, Collection<VieCard>> rankToCardsMap = new HashMap<>();
    for (VieCard card : shoe) {
      if (!rankToCardsMap.containsKey(card.getRank())) {
        rankToCardsMap.put(card.getRank(), new LinkedList<VieCard>());
      }

      Collection<VieCard> cardsAtRank = rankToCardsMap.get(card.getRank());
      cardsAtRank.add(card);
    }

    return rankToCardsMap;
  }

  /**
   * 
   * @return the number of decks in the card manager
   */
  public int getNumberOfDecks() {
    return rules.getNumberOfDecks();
  }

  /**
   * Provides a way to simulate a marker card in the shoe at a casino. Casinos use a marker card to
   * indicate when a shuffle should be done after a hand. Leaving a large number of cards undealt in
   * the shoe prior to a shuffle makes it much harder for a player to card count.
   *
   * For example a casino may require that if a hand ends with < 25% of the deck in the shoe, the
   * deck should be shuffled. Different casinos have different rules for what value to use.
   *
   * This method is intended to allow an implementation to check this value and call
   * {@link #shuffle()} if necessary.
   *
   * @return percentage (0 -100) of cards that in in the shoe.
   */
  public int getPercentCardsInShoe() {
    return (int) Math.ceil(100 * ((double) shoe.size() / this.getTotalNumCards()));
  }

  /**
   *
   * @return the total number of cards in all phases of play
   *
   */
  public int getTotalNumCards() {
    return rules.getNumberOfDecks() * Rank.values().length * Suit.values().length;
  }


  /**
   *
   * @return the total number of cards in the shoe
   *
   */
  public int getRemainingCardsInShoe() {
    return shoe.size();
  }



}
