/* Copyright (c) 2018,2018 IBM Corporation Licensed under the Apache License, Version 2.0 (the
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.exceptions.BlackjackRuleViolationException;
import com.ibm.vie.blackjack.casino.hand.HandUtil;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHandPayout;
import com.ibm.vie.blackjack.casino.observer.TableObserver;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;
import com.ibm.vie.blackjack.player.TableRules;

/**
 * A {@link Table} binds all aspects of the game model together. This is what effectively plays the
 * game.
 * 
 * <p>
 * The model contains:
 * <ul>
 * <li>the available money</li>
 * <li>the round number</li>
 * <li>the deck of cards (the {@link CardManager})</li>
 * <li>the dealer's hand</li>
 * <li>all player hands and their wagers</li>
 * <li>the rules of the game (such as max bet, min bet, number of rounds, etc)</li>
 * <li>the strategy of the player</li>
 * </ul>
 * </p>
 *
 *
 * @author ntl
 *
 */
public class Table {
  private int availableMoney;
  private final CardManager cardManager;

  private final List<TableObserver> observers = new LinkedList<>();
  private int roundNumber = 0;

  private final TableRules rules;

  private final PlayerStrategy strategy;

  private VieDealerHand vieDealerHand;

  private final List<ViePlayerHand> viePlayerHands = new LinkedList<>();

  private AtomicBoolean earlyTerminationRequested = new AtomicBoolean(false);

  /**
   * Internal Exception to indicate that the game should end because of a cancel request
   * 
   * @author ntl
   *
   */
  private static class EarlyTerminationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

  }


  /**
   * Indicate that processing of this game should end without playing the complete number of rounds.
   * 
   * <p>
   * Used by the UI to indicate that the game is over because of a cancel request (back button or
   * close)
   * </p>
   * 
   * <p>
   * It is possible that the simulation may continue for a short while after this method has been called.
   * Termination will only be checked when the observers are called to observe a state change.
   * </p>
   * 
   * @param termination
   */
  public void signalEarlyTermination(boolean termination) {
    earlyTerminationRequested.set(termination);
  }

  /**
   * Call the table observers, throwing an exception if any requests early termination
   * 
   * @param action
   * @throws EarlyTerminationException
   */
  private void callObservers(Consumer<TableObserver> action) throws EarlyTerminationException {
    for (TableObserver observer : observers) {
      if (earlyTerminationRequested.get()) {
        throw new EarlyTerminationException();
      } else {
        action.accept(observer);
      }
    }
  }

  /**
   * Constructs a table to play blackjack on
   *
   * @param strategy
   */
  public Table(final PlayerStrategy strategy) {
    this(strategy, new TableRules());
  }

  /**
   * Constructs a table with a specific deck
   *
   * @param strategy
   * @param cardManager
   */
  public Table(final PlayerStrategy strategy, final CardManager cardManager) {
    this(strategy, cardManager, new TableRules());
  }

  /**
   * Constructs a table with the specified parameters
   *
   * @param strategy - strategy to play against the table
   * @param cardManager - deck configuration to use
   * @param rules - rules for the table
   *
   * @throws IllegalArgumentException if a parameter is invalid
   */
  public Table(final PlayerStrategy strategy, final CardManager cardManager, final TableRules rules)
      throws IllegalArgumentException {
    if (cardManager.getNumberOfDecks() != rules.getNumberOfDecks()) {
      throw new IllegalArgumentException(
          "The number of decks defined by the rules does not equal the number of decks in the card manager");
    }
    this.rules = rules;
    this.cardManager = cardManager;
    this.strategy = strategy;
    availableMoney = rules.getInitialMoney();

  }

  /**
   * Constructs a table to play blackjack on
   *
   * @param strategy
   * @param rules - the rules to use for the game
   */
  public Table(final PlayerStrategy strategy, final TableRules rules) {
    this(strategy, new CardManager(rules), rules);
  }

  /**
   * Adds an observer to the table
   *
   * @param observer
   */
  public void addObserver(final TableObserver observer) {
    observers.add(observer);
  }

  /**
   * Cleans up the table after all hands are complete. Live cards go to the discard tray, the player
   * and dealer's hands are cleared.
   */
  private void cleanUpTable() {
    cardManager.collectLiveCards();
    viePlayerHands.clear();
    vieDealerHand = null;

    // if the rules specify a deck penetration < 100, then we may have
    // to shuffle
    if ((100 - cardManager.getPercentCardsInShoe()) > rules.getCompetitionRules()
        .getDeckPenetration()) {
      cardManager.shuffle();
    } else if (cardManager.getLastShuffleDueToEmptyDeck()
        && rules.getCompetitionRules().getUseRealCasinoRulesWhenOutOfCards()) {
      // the deck was shuffled "mid round", a real casino would shuffle again
      // before the next round so that the deck is complete
      cardManager.shuffle();
    }

  }

  /**
   * Perform a hit action for the dealer
   *
   */
  private void dealerHits() {
    final VieCard[] dealerCards = new VieCard[vieDealerHand.getCards().size() + 1];
    vieDealerHand.getCards().toArray(dealerCards);
    dealerCards[vieDealerHand.getCards().size()] = cardManager.drawCard();

    vieDealerHand = new VieDealerHand(dealerCards);
  }

  /**
   * Deal the initial hand. Player's two cards are dealt first, and then the dealer receives two
   * cards.
   *
   * @param initialBet the wager placed on the hand
   *
   */
  private void dealInitialHand(final int initialBet) {
    viePlayerHands
        .add(new ViePlayerHand(initialBet, cardManager.drawCard(), cardManager.drawCard()));
    vieDealerHand = new VieDealerHand(cardManager.drawCard(), cardManager.drawCard());
  }

  /**
   *
   * @return the amount of money that is available
   */
  public int getAvailableMoney() {
    return availableMoney;
  }

  /**
   * Get a decision for the current hand from the Player's strategy
   *
   * @param currentHand
   * @return the decision
   * @throws EarlyTerminationException
   */
  private PlayerDecision getDecisionFromStrategy(final ViePlayerHand currentHand)
      throws EarlyTerminationException {
    final List<PlayerHand> handInfos = ViePlayerHand.toPlayerHandList(viePlayerHands);
    final PlayerHand currentHandInfo = handInfos.get(viePlayerHands.indexOf(currentHand));

    if (this.earlyTerminationRequested.get()) {
      throw new EarlyTerminationException();
    }

    return strategy.decideHowToPlayHand(getGameInfo(), currentHandInfo, handInfos,
        vieDealerHand.getDealerUpCardInfo());
  }

  /**
   *
   * @return information about the game that will be passed to the player
   */
  private GameInfo getGameInfo() {
    return new GameInfo(availableMoney, rules, roundNumber + 1,
        VieCard.toCardList(cardManager.getCardsInDiscardTray()));
  }

  /**
   *
   * @return - the rules that are in effect for this table
   */
  public TableRules getRules() {
    return rules;
  }

  /**
   * Perform a double down action for the player
   *
   * @param oldHand the hand to double down on
   *
   * @throws BlackjackRuleViolationException
   */
  private void playerDoubles(final ViePlayerHand oldHand) throws BlackjackRuleViolationException {
    BlackjackRuleUtils.checkDoubleDownIsLegal(this, oldHand);

    availableMoney -= oldHand.getBetPaid();

    final VieCard[] newHandCards = new VieCard[oldHand.getCards().size() + 1];

    oldHand.getCards().toArray(newHandCards);


    newHandCards[oldHand.getCards().size()] = cardManager.drawCard();

    final ViePlayerHand newHand = new ViePlayerHand(oldHand.getBetPaid() * 2, false, newHandCards);

    viePlayerHands.set(viePlayerHands.indexOf(oldHand), newHand);
  }

  /**
   * Perform a hit action for the player
   *
   * @param oldHand the hand to hit on
   *
   * @throws BlackjackRuleViolationException
   */
  private void playerHits(final ViePlayerHand oldHand) throws BlackjackRuleViolationException {

    BlackjackRuleUtils.checkHitIsLegal(oldHand);
    final VieCard[] newHandCards = new VieCard[oldHand.getCards().size() + 1];

    oldHand.getCards().toArray(newHandCards);

    newHandCards[oldHand.getCards().size()] = cardManager.drawCard();
    final ViePlayerHand newHand = new ViePlayerHand(oldHand.getBetPaid(), true, newHandCards);

    viePlayerHands.set(viePlayerHands.indexOf(oldHand), newHand);
  }

  /**
   * Perform a split action for the player
   *
   * @param oldHand the hand to split
   *
   * @throws BlackjackRuleViolationException
   */
  private void playerSplits(final ViePlayerHand oldHand) throws BlackjackRuleViolationException {
    BlackjackRuleUtils.checkSpiltIsLegal(this, oldHand);
    availableMoney -= oldHand.getBetPaid();

    final VieCard[] newHandCards1 = {oldHand.getCards().get(0), cardManager.drawCard()};
    final VieCard[] newHandCards2 = {oldHand.getCards().get(1), cardManager.drawCard()};

    final ViePlayerHand newHand1 = new ViePlayerHand(oldHand.getBetPaid(), true, newHandCards1);
    final ViePlayerHand newHand2 = new ViePlayerHand(oldHand.getBetPaid(), true, newHandCards2);

    final int splitIndex = viePlayerHands.indexOf(oldHand);

    viePlayerHands.add(splitIndex, newHand1);
    viePlayerHands.set(splitIndex + 1, newHand2); // replace original
  }

  /**
   * Perform a stand action for the player
   *
   * @param oldHand the hand to STAND
   *
   * @throws BlackjackRuleViolationException
   */
  private void playerStands(final ViePlayerHand oldHand) throws BlackjackRuleViolationException {
    BlackjackRuleUtils.checkStandIsLegal(oldHand);

    final ViePlayerHand newHand =
        new ViePlayerHand(oldHand.getBetPaid(), false, oldHand.getCards().toArray(new VieCard[0]));

    viePlayerHands.set(viePlayerHands.indexOf(oldHand), newHand);

  }

  /**
   * Iterate over playing rounds of blackjack. <BR/>
   * For each round:
   * <OL>
   * <LI>An initial bet is obtained by calling
   * {@link PlayerStrategy#placeInitialBet(int, TraditionalTableRules)}</LI>
   * <LI>The initial hand is dealt</LI>
   * <LI>hit/stand/double or split decisions are made by calling
   * {@link PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, player.Card)}. These
   * Decisions result in dealing more cards, adjusting bets, and splitting hands as appropriate.
   * This process continues until all player decisions have been made.</LI>
   * <LI>The dealer's turn begins when all player decisions have been made. The dealer must hit
   * until a score of at least 17 is reached. Whether the dealer can hit on a "soft 17" is
   * determined by {@link CasinoRules#getDealerHitsOnSoft17()}. <B>If every player hand is a bust,
   * then the dealer does not have to play.</B></LI>
   * <LI>At the conclusion of the dealer's turn, the available money is updated considering all bets
   * and outcomes.</LI>
   * <LI>{@link PlayerStrategy#decideToWalkAway(GameInfo, List, player.hand.DealerHand)} is called
   * to decide whether to play another hand</LI>
   * <LI>The cards on the table are cleared and placed in the discard tray</LI>
   * </OL>
   *
   * <BR/>
   * The cardManager (decks of cards) is shuffled when
   * <UL>
   * <LI>A card needs to be drawn, and there are no cards in the cardManager. In this scenario, only
   * the cards in the discard tray are shuffled into the deck. Cards on the table remain on the
   * table and will be placed in the discard tray at the end of the hand.</LI>
   * <LI>A real casino will have a marker card in the deck. When drawn, this card will signal the
   * dealer to shuffle the deck at the end of the hand. This makes card counting much more difficult
   * since only a certain percentage of cards are drawn from the front of the deck before shuffling
   * again. This behavior is simulated based on the deck penetration param in the
   * {@link com.ibm.vie.blackjack.player.CasinoRules CasinoRules}.</LI>
   * </UL>
   *
   * <BR/>
   * All player decisions for a hand are considered to be made when any of the following is true:
   * <UL>
   * <LI>The maximum number of points equals 21</LI>
   * <LI>The minimum number of points exceeds 21</LI>
   * <LI>A <B>Stand</B> decision has been made on the hand</LI>
   * <LI>A <B>Double Down</B> decision has been made on the hand</LI>
   * <LI>The dealer has blackjack.</LI>
   * </UL>
   *
   * <BR/>
   * This method returns (game is over) when any of the following occur
   * <UL>
   * <LI>{@link PlayerStrategy#decideToWalkAway(int, TraditionalTableRules, int, List, player.hand.DealerHandInfo)}
   * returns true</LI>
   * <LI>The player has played the maximum number of games as specified by
   * {@link TableRules#getMaxNumRounds()}</LI>
   * <LI>The player no longer has sufficient available money to cover the minimum bet as specified
   * by {@link TableRules#getMinBet()}</LI>
   * <LI>An exception is thrown from a method in the {@link PlayerStrategy}. If this occurs, any
   * bets in progress are awarded to the house.</LI>
   * </UL>
   *
   *
   */
  public void playManyRoundsOfBlackJack() {
    try {
      for (roundNumber = 0; roundNumber < rules.getMaxNumRounds(); roundNumber++) {
        try {
          final List<ViePlayerHandPayout> payouts = playOneRoundOfBlackJack();

          // notify observers that the round is over
          callObservers((observer) -> observer.observeEndOfRound(getGameInfo(), vieDealerHand,
              Collections.unmodifiableList(payouts)));

          // ask if the player should walk away
          if (strategy.decideToWalkAway(getGameInfo(),
              ViePlayerHandPayout.toPlayerHandPayoutList(payouts), vieDealerHand.toDealerHand())) {
            return; // player says done;
          }


          if (availableMoney < rules.getMinBet()) {
            return; // out of money
          }

        } catch (final Exception e) {
          /*
           * Rule violations result in being forced to leave the table, any bets that have been
           * placed but not resolved are forfeit.
           */
          try {
            callObservers((observer) -> observer.observeProgramError(getGameInfo(),
                Collections.unmodifiableList(viePlayerHands), vieDealerHand, e));
          } catch (final EarlyTerminationException e3) {
            return; // don't keep playing because of UI request
          } catch (final Exception e2) {
            // this probably means an error in the framework, some exception might have
            // occurred and
            // now the information passed
            // to the observer is not correct...or there is some problem in the observer. If
            // the
            // strategy is defective, we shouldn't
            // get an exception calling the observers.
            e2.printStackTrace();
            e.printStackTrace();
          }

          if (observers.isEmpty()) { // If there are no observers, just the log the error.
            e.printStackTrace();
          }
          return;
        } finally {
          cleanUpTable();
        }
      }
    } finally {
      roundNumber = Math.min(rules.getMaxNumRounds() - 1, roundNumber); // current round must appear
                                                                        // to be the last

      try {
        callObservers((observer) -> observer.observeGameIsOver(getGameInfo()));
      } catch (EarlyTerminationException e) {
        // asked to terminate but game is over, so nothing to do
        return;
      }

    }
  }

  /**
   * Play one hand of blackjack, leaves the state of the table intact at the end of the round.
   * Winnings have been paid back to the player at the end of this method.
   *
   * @returns the completed hands with outcome and payout information
   * @throws BlackjackRuleViolationException
   * @throws EarlyTerminationException
   */
  private List<ViePlayerHandPayout> playOneRoundOfBlackJack()
      throws BlackjackRuleViolationException, EarlyTerminationException {


    // put the initial bet in the betting box
    final int initialBet = strategy.placeInitialBet(getGameInfo());

    BlackjackRuleUtils.checkInitialBetIsLegal(this, initialBet);
    availableMoney -= initialBet;
    callObservers((observer) -> observer.observeInitialBet(getGameInfo(), initialBet));

    // deal the initial cards
    dealInitialHand(initialBet);


    // check for blackjack, which ends the round
    if (vieDealerHand.isBlackJack() || viePlayerHands.get(0).isBlackJack()) {
      return settleAllBets();
    }


    // Player makes decisions on his/her hands until there are no more decisions to
    // be made
    boolean playerTurnIsOver = false;
    while (!playerTurnIsOver) {
      final ViePlayerHand currentHand = HandUtil.findHandForPlayerTurn(viePlayerHands);

      if (currentHand != null) {
        playOneTurn(currentHand);
      } else {
        playerTurnIsOver = true;
      }
    }

    // Dealer's turn
    callObservers(o -> o.observeDealerTurn(getGameInfo(), vieDealerHand, viePlayerHands));

    if (HandUtil.playerHasAtLeastOneHandWithoutBust(viePlayerHands)) {
      // dealer only plays if the player didn't bust
      // Dealer hits until >= 17 or bust (dealer may hit on soft 17 if the rules
      // allow)
      while (rules.getCompetitionRules().getDealerHitsOnSoft17()
          ? (vieDealerHand.getScoreAceAs1() < 17 && vieDealerHand.getScore() < 17 + 1)
          : (vieDealerHand.getScore() < 17)) {
        dealerHits();
      }
    }

    return settleAllBets();
  }

  /**
   * Retrieves a decision for the current hand and implements the decision. Observers are called as
   * appropriate
   *
   * @param currentHand - the hand to play on
   * @throws EarlyTerminationException
   * @throws BlackjackRuleViolationException
   */
  private void playOneTurn(final ViePlayerHand currentHand)
      throws EarlyTerminationException, BlackjackRuleViolationException {

    callObservers(o -> o.observePlayerTurn(getGameInfo(), currentHand,
        vieDealerHand.getDealerUpCard(), Collections.unmodifiableList(viePlayerHands)));

    final PlayerDecision decision = getDecisionFromStrategy(currentHand);
    callObservers(o -> o.observeDecisionMade(getGameInfo(), decision, currentHand,
        vieDealerHand.getDealerUpCard(), Collections.unmodifiableList(viePlayerHands)));

    switch (decision) {
      case HIT:
        playerHits(currentHand);
        break;
      case SPLIT:
        playerSplits(currentHand);
        break;
      case STAND:
        playerStands(currentHand);
        break;
      case DOUBLE_DOWN:
        playerDoubles(currentHand);
        break;
    }

    callObservers(o -> o.observeDecisionOutcome(getGameInfo(), decision, currentHand,
        vieDealerHand.getDealerUpCard(), Collections.unmodifiableList(viePlayerHands)));
  }

  /**
   * This method determines the outcome and payouts for all player hands. It updates the available
   * money for the player
   *
   * @return
   */
  private List<ViePlayerHandPayout> settleAllBets() {
    final List<ViePlayerHandPayout> payouts =
        HandUtil.calculatePayouts(viePlayerHands, vieDealerHand, rules);

    availableMoney += payouts.stream().mapToInt(ViePlayerHandPayout::getPayout).sum();
    return Collections.unmodifiableList(payouts);
  }

}
