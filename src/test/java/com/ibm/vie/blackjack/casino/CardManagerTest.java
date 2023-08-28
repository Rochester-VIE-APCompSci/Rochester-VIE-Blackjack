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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import com.ibm.vie.blackjack.casino.card.Rank;
import com.ibm.vie.blackjack.casino.card.Suit;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.player.CasinoRules;
import com.ibm.vie.blackjack.player.TableRules;

public class CardManagerTest {

  /**
   * Returns a set of rules, setting the appropiate number of decks into the rules and using
   * defaults for other parameters.
   * 
   * @param numDecks - number of decks in the rules
   * 
   * @return a table rules
   */
  private static TableRules getTableRulesForNumDecks(final int numDecks) {
    return new TableRules(BlackjackTableRuleDefaults.INITIAL_MONEY,
        BlackjackTableRuleDefaults.MIN_BET, BlackjackTableRuleDefaults.MAX_BET,
        BlackjackTableRuleDefaults.MAX_NUM_ROUNDS, numDecks);
  }


  private static TableRules getAdvancedCasinoRules(int numDecks) {

    CasinoRules casinoRules = new CasinoRules() {

      @Override
      public double getBlackJackPayOut() {
        return 1.5;
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
        return true;
      }

      @Override
      public double getPayoutForPush() {
        return 0;
      }

      @Override
      public String getDescription() {
        return "";
      }

    };

    return new TableRules(BlackjackTableRuleDefaults.INITIAL_MONEY,
        BlackjackTableRuleDefaults.MIN_BET, BlackjackTableRuleDefaults.MAX_BET,
        BlackjackTableRuleDefaults.MAX_NUM_ROUNDS, numDecks, casinoRules);
  }

  @Test
  public void when_shuffle_at_end_of_round_then_card_burned() {
    TableRules rules = getAdvancedCasinoRules(1);
    CardManager deck = new CardManager(rules);

    Assert.assertFalse(deck.getLastShuffleDueToEmptyDeck());
    Set<VieCard> drawnCards = new HashSet<>();
    // discard 51 cards
    for (int i = 0; i < 51; i++) {
      drawnCards.add(deck.drawCard());
    }

    // all cards except 1 should be in discard
    deck.collectLiveCards();

    // every card in the discard was drawn
    deck.getCardsInDiscardTray().forEach(
        card -> Assert.assertTrue("Card " + card + " was not drawn!", drawnCards.contains(card)));

    // every card drawn is now discard
    drawnCards.forEach(card -> Assert.assertTrue("Card " + card + " was drawn but not discared!",
        deck.getCardsInDiscardTray().contains(card)));

    Set<VieCard> cardsOnTable = new HashSet<>();
    cardsOnTable.add(deck.drawCard());
    Assert.assertFalse(deck.getLastShuffleDueToEmptyDeck());

    Set<VieCard> cardsAfterShuffle = new HashSet<>();
    cardsAfterShuffle.add(deck.drawCard()); // force shuffle
    Assert.assertTrue(deck.getLastShuffleDueToEmptyDeck());

    // discard tray was shuffled back into the deck
    Assert.assertTrue(deck.getCardsInDiscardTray().isEmpty());

    // one card burned in the intermediate shuffle, one card drawn
    // means there should be drawnCards (the previous discard tray) - 2 cards
    // remaining in the shoe
    Assert.assertEquals(drawnCards.size() - cardsOnTable.size() - cardsAfterShuffle.size(),
        deck.getRemainingCardsInShoe());
    Assert.assertEquals(deck.getTotalNumCards(), deck.getRemainingCardsInShoe() + 2 + 1);

    deck.collectLiveCards();
    deck.shuffle();
    Assert.assertFalse(deck.getLastShuffleDueToEmptyDeck());
    Assert.assertEquals(deck.getTotalNumCards(), deck.getRemainingCardsInShoe());
  }

  /**
   * Counter for how many times each suit & rank combination was seen. For example it may be used to
   * answer the question how many Ace of Spades cards were drawn from the deck?
   *
   * @author ntl
   *
   */
  public static class SuitRankCounter {
    private final Map<Rank, Map<Suit, Integer>> countCards;

    /**
     * Initialize with 0 values for all suit and rank combinations
     */
    public SuitRankCounter() {
      this.countCards = new HashMap<>();
      clearCounts();
    }

    /**
     * Clear all count values
     */
    public final void clearCounts() {
      for (Rank rank : Rank.values()) {
        countCards.put(rank, new HashMap<>());
        for (Suit suit : Suit.values()) {
          countCards.get(rank).put(suit, 0);
        }
      }
    }

    /**
     * JUnit assertion that all suit & rank combinations have the specified count value.
     *
     * @param count
     */
    public void assertAllCombinationsHaveCount(int count) {
      for (Rank rank : Rank.values()) {
        for (Suit suit : Suit.values()) {
          Assert
              .assertTrue(
                  "Wrong number of draws " + countCards.get(rank).get(suit) + " for "
                      + rank.toString() + " of " + suit.toString(),
                  countCards.get(rank).get(suit) == count);
        }
      }
    }

    /**
     * Increment the count value for a card
     *
     * @param vieCard
     */
    public void incrementCount(VieCard vieCard) {
      Integer count = countCards.get(vieCard.getRank()).get(vieCard.getSuit());
      countCards.get(vieCard.getRank()).put(vieCard.getSuit(), ++count);
    }

  }

  @Test
  public void when_single_deck_then_all_cards_can_be_drawn_once() {
    final int numDecks = 1;
    final int numCards = 52 * numDecks;

    CardManager deck = new CardManager(getTableRulesForNumDecks(numDecks));
    SuitRankCounter drawCounter = new SuitRankCounter();

    Assert.assertTrue("All cards are not reported to be in the shoe",
        deck.getPercentCardsInShoe() == 100);

    // draw all cards from the shoe
    for (int cardNum = 0; cardNum < deck.getTotalNumCards(); cardNum++) {
      VieCard vieCard = deck.drawCard();
      Assert.assertTrue("Total Cards is incorrect after draw", deck.getTotalNumCards() == numCards);
      Assert.assertTrue("Wrong number of cards in the shoe",
          deck.getRemainingCardsInShoe() == (numCards - cardNum - 1));

      final int percentage = deck.getPercentCardsInShoe();
      final int expectedPercentage = (int) Math.ceil(100 * (1 - (double) cardNum / numCards));
      Assert.assertTrue("Wrong % reported to be in the shoe for draw " + cardNum + " %="
          + percentage + "/" + expectedPercentage, percentage - expectedPercentage <= 1);
      drawCounter.incrementCount(vieCard);
    }

    // all types of cards should have been drawn numDecks times
    drawCounter.assertAllCombinationsHaveCount(numDecks);

    // test that the deck is now empty, another draw results in an exception
    try {
      VieCard extraCard = deck.drawCard();
      Assert.fail("Card " + extraCard.toString() + " was drawn when the deck should be empty");
    } catch (RuntimeException e) {
      // correct behavior
    }

  }

  @Test
  public void when_multi_deck_then_all_cards_can_be_drawn_once() {
    final int numDecks = 8;
    final int numCards = 52 * numDecks;

    CardManager deck = new CardManager(getTableRulesForNumDecks(numDecks));
    SuitRankCounter drawCounter = new SuitRankCounter();

    // draw all cards from the shoe
    for (int cardNum = 0; cardNum < deck.getTotalNumCards(); cardNum++) {
      VieCard vieCard = deck.drawCard();
      Assert.assertTrue("Total Cards is incorrect after draw", deck.getTotalNumCards() == numCards);
      Assert.assertTrue("Wrong number of cards in the shoe",
          deck.getRemainingCardsInShoe() == (numCards - cardNum - 1));
      final int percentage = deck.getPercentCardsInShoe();
      final int expectedPercentage = (int) Math.ceil(100 * (1 - (double) cardNum / numCards));
      Assert.assertTrue("Wrong % reported to be in the shoe for draw " + cardNum + " %="
          + percentage + "/" + expectedPercentage, percentage - expectedPercentage <= 1);

      drawCounter.incrementCount(vieCard);
    }

    // all types of cards should have been drawn numDecks times
    drawCounter.assertAllCombinationsHaveCount(numDecks);

    // test that the shoe is now empty, another draw results in an exception
    try {
      VieCard extraCard = deck.drawCard();
      Assert.fail("Card " + extraCard.toString() + " was drawn when the deck should be empty");
    } catch (RuntimeException e) {
      // correct behavior
    }

  }

  @Test
  public void when_live_cards_collected_then_cards_can_be_drawn_again() {
    final int numDecks = 8;
    final int numCards = 52 * numDecks;
    final int cardsInPlay = (52 * numDecks) / 2;

    CardManager deck = new CardManager(getTableRulesForNumDecks(numDecks));
    SuitRankCounter drawCounter = new SuitRankCounter();

    // draw cards from the shoe
    for (int cardNum = 0; cardNum < cardsInPlay; cardNum++) {
      deck.drawCard();
    }

    Assert.assertTrue(deck.getRemainingCardsInShoe() + " cards remain in the shoe.",
        deck.getRemainingCardsInShoe() == (numCards - cardsInPlay));

    deck.collectLiveCards();
    // Collect adds the cards to the discard tray, an automatic shuffle will occur
    // when
    // necessary
    Assert.assertTrue(
        deck.getRemainingCardsInShoe() + " cards remain in the shoe after collectLiveCards",
        deck.getRemainingCardsInShoe() == numCards - cardsInPlay);

    // count the cards as cards are drawn, we should be able to draw the
    // cards that were already played now without an exception
    for (int cardNum = 0; cardNum < deck.getTotalNumCards(); cardNum++) {
      VieCard vieCard = deck.drawCard();
      drawCounter.incrementCount(vieCard);
    }

    drawCounter.assertAllCombinationsHaveCount(numDecks);

  }

  @Test
  public void when_manager_uses_same_seed_then_order_is_the_same() {
    final int numDecks = 8;
    final int numDraws = 52 * numDecks;

    CardManager deck1 = new CardManager(getTableRulesForNumDecks(numDecks), 1234);
    CardManager deck2 = new CardManager(getTableRulesForNumDecks(numDecks), 1234);

    // match rate for draws should be 100%
    int matches = 0;
    for (int drawNum = 0; drawNum < numDraws; drawNum++) {
      VieCard card1 = deck1.drawCard();
      VieCard card2 = deck2.drawCard();
      if (card1.equals(card2)) {
        matches++;
      }
    }

    Assert.assertTrue("Order of both decks is not the same", matches == numDraws);

  }

  @Test
  public void when_manager_uses_different_seed_then_order_is_not_the_same() {
    final int numDecks = 8;
    final int numDraws = 52 * numDecks;

    CardManager deck1 = new CardManager(getTableRulesForNumDecks(numDecks), 1234);
    CardManager deck2 = new CardManager(getTableRulesForNumDecks(numDecks), 9999);

    // match rate for draws should be less than 100%
    int matches = 0;
    for (int drawNum = 0; drawNum < numDraws; drawNum++) {
      VieCard card1 = deck1.drawCard();
      VieCard card2 = deck2.drawCard();
      if (card1.equals(card2)) {
        matches++;
      }
    }

    Assert.assertTrue("Order of both decks is the same", matches < numDraws);

  }

  @Test
  public void when_manager_uses_same_seed_and_multiple_shuffle_then_order_is_the_same() {
    final int numDecks = 8;
    final int numDraws = 52 * numDecks;

    CardManager deck1 = new CardManager(getTableRulesForNumDecks(numDecks), 1234);
    CardManager deck2 = new CardManager(getTableRulesForNumDecks(numDecks), 1234);

    for (int shuffle = 0; shuffle < 100; shuffle++) {
      // match rate for draws should be 100%
      int matches = 0;
      for (int drawNum = 0; drawNum < numDraws; drawNum++) {
        VieCard card1 = deck1.drawCard();
        VieCard card2 = deck2.drawCard();
        if (card1.equals(card2)) {
          matches++;
        }
      }

      Assert.assertTrue("Order of both decks is not the same", matches == numDraws);
      deck1.collectLiveCards();
      deck2.collectLiveCards();
    }
  }


  @Test
  public void when_ordered_deck_then_deck_is_correctly_ordered() {
    CardManager deck = new CardManager(getTableRulesForNumDecks(1), 1234);

    deck.orderDeckByRank(Rank.ACE, Rank.TWO, Rank.ACE, Rank.QUEEN);

    Assert.assertTrue(deck.drawCard().getRank() == Rank.ACE);
    Assert.assertTrue(deck.drawCard().getRank() == Rank.TWO);
    Assert.assertTrue(deck.drawCard().getRank() == Rank.ACE);
    Assert.assertTrue(deck.drawCard().getRank() == Rank.QUEEN);
    Assert.assertTrue("cards = " + deck.getRemainingCardsInShoe(),
        deck.getRemainingCardsInShoe() == 48);
  }

}
