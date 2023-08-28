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
package com.ibm.vie.blackjack.casino.driver.crooked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import com.ibm.vie.blackjack.casino.card.Rank;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.crooked.CardManagerViewer;
import com.ibm.vie.blackjack.casino.crooked.CardSwitchCardChooser;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;
import com.ibm.vie.blackjack.casino.observer.TableObserver;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;

/**
 * Sample observer that allows the deck to be manipulated based on observations
 * 
 * @author ntl
 *
 */
public class CrookedObserver implements CardSwitchCardChooser, TableObserver {

  /**
   * Tracks dealt cards for the dealer's turn
   * 
   * @author ntl
   *
   */
  public static class DealerTurnState implements TableState {
    List<ViePlayerHand> allPlayerHands;
    List<VieCard> dealerCards = new LinkedList<>();
    VieDealerHand dealerInitialHand;

    public DealerTurnState(VieDealerHand dealerInitialHand, List<ViePlayerHand> allPlayerHands) {
      this.dealerInitialHand = dealerInitialHand;
      this.allPlayerHands = allPlayerHands;
    }

    @Override
    public void addCard(VieCard card) {
      dealerCards.add(card);
    }


    public String drawStateMarkingNextCardLoc() {
      StringBuilder sb = new StringBuilder();
      sb.append("DRAWING A CARD FOR THE DEALER'S TURN: \n");
      sb.append("DEALER CARDS: ");
      dealerInitialHand.getCards().forEach(c -> sb.append(c + ", "));
      sb.append(" <***NEXT CARD HERE***>\n");
      sb.append("PLAYER HANDS:\n");
      allPlayerHands.forEach(h -> sb.append(h.toString() + "\n"));

      return sb.toString();
    }

  }


  /**
   * Tracks cards for the initial deal
   * 
   * @author ntl
   *
   */
  public static class InitialDealState implements TableState {
    private VieCard dealerUpCard = null;
    private VieCard dealerDownCard = null;
    private List<VieCard> playerCards = new LinkedList<>();

    @Override
    public void addCard(VieCard card) {
      // player first, then dealer dealer is face up and then face down
      if (playerCards.size() < 2) {
        playerCards.add(card);
      } else if (dealerUpCard == null) {
        dealerUpCard = card;
      } else if (dealerDownCard == null) {
        dealerDownCard = card;
      } else {
        throw new RuntimeException("Initially dealt too many cards");
      }
    }


    public String drawStateMarkingNextCardLoc() {
      StringBuilder sb = new StringBuilder();
      sb.append("***DRAWING A CARD FOR INITIAL DEAL \n");
      sb.append("PLAYER CARDS: ");
      playerCards.forEach(c -> sb.append(c.toString() + " "));
      if (playerCards.size() < 2) {
        sb.append(" <***NEXT CARD HERE***>\n");
      }
      sb.append("\n");

      if (playerCards.size() >= 2) {
        sb.append("DEALER UP CARD: "
            + ((dealerUpCard != null) ? dealerUpCard.toString() + "\n": "<***NEXT CARD HERE>\n"));
        if (dealerUpCard != null) {
          sb.append("DEALER DOWN CARD: "
              + ((dealerDownCard != null) ? dealerDownCard.toString() : "<***NEXT CARD HERE>\n"));
        }
      }
      sb.append("\n");

      return sb.toString();
    }

  }

  /**
   * Nothing to track for a hit/double down, the state will change as the result
   * of a new decision or dealer's turn.
   * 
   * However this allows the state of the table to be displayed prior to the
   * next card being drawn / choosen
   * 
   * @author ntl
   *
   */
  public static class PlayerTurnHitOrDoubleState implements TableState {
    List<ViePlayerHand> allPlayerHands;
    ViePlayerHand currentHand;
    VieCard dealerUpCard;
    PlayerDecision decision;

    public PlayerTurnHitOrDoubleState(ViePlayerHand currentHand, VieCard dealerUpCard,
        List<ViePlayerHand> allPlayerHands, PlayerDecision decision) {
      this.currentHand = currentHand;
      this.allPlayerHands = allPlayerHands;
      this.dealerUpCard = dealerUpCard;
      this.decision = decision;
    }

    @Override
    public void addCard(VieCard card) {
      // there are no intermediate draws, so nothing to track
    }


    public String drawStateMarkingNextCardLoc() {
      StringBuilder sb = new StringBuilder();
      sb.append("****DRAWING A CARD BECAUSE OF A DECISION TO " + this.decision + " on " + this.currentHand + "\n");
      sb.append("DEALER UP CARD: " + dealerUpCard + "\n");
      sb.append("PLAYER HANDS: ");
      for (ViePlayerHand hand : allPlayerHands) {
        if (hand == currentHand) {
          sb.append(hand.toString() + ", <***NEXT CARD HERE***>\n");
        } else {
          sb.append(hand.toString() + "\n");
        }
      }

      return sb.toString();
    }

  };

  /**
   * The split state tracks the cards that are dealt into the intermediate hands
   * that are being created as the result of the split.
   * 
   * @author ntl
   *
   */
  public static class PlayerTurnSplitState implements TableState {
    List<VieCard> cardsForSplit = new LinkedList<>();
    VieCard dealerUpCard;
    List<ViePlayerHand> priorAllPlayerHands;
    ViePlayerHand priorCurrentHand;

    public PlayerTurnSplitState(ViePlayerHand currentHand, VieCard dealerUpCard,
        List<ViePlayerHand> allPlayerHands) {
      this.priorCurrentHand = currentHand;
      this.priorAllPlayerHands = allPlayerHands;
      this.dealerUpCard = dealerUpCard;
    }

    @Override
    public void addCard(VieCard card) {
      cardsForSplit.add(card);
    }


    public String drawStateMarkingNextCardLoc() {
      StringBuilder sb = new StringBuilder();
      sb.append("SPLITING ORIGINAL HAND: " + priorCurrentHand + "\n");
      sb.append("DEALER UP CARD: " + dealerUpCard + "\n");
      sb.append("PLAYER HANDS:\n");
      for (ViePlayerHand hand : priorAllPlayerHands) {
        if (hand == priorCurrentHand) {
          if (cardsForSplit.isEmpty()) {
            sb.append("SPLIT HAND #1: ");
            sb.append(hand.getCards().get(0).toString() + ", <***NEXT CARD HERE***> \n");
            sb.append("SPLIT HAND #2: ");
            sb.append(hand.getCards().get(1).toString() + ", ....\n");
          } else {
            sb.append("SPLIT HAND #1: ");
            sb.append(hand.getCards().get(0).toString() + cardsForSplit.get(0) + "\n");
            sb.append("SPLIT HAND #2: ");
            sb.append(hand.getCards().get(1).toString() + ", <***NEXT CARD HERE***>\n");
          }
        } else {
          sb.append(hand.toString() + "\n");
        }
      }

      return sb.toString();
    }

  }


  /**
   * Interface that keeps track of cards that have been drawn as part of an 
   * intermediate state, such as a split, initial deal, or dealer's turn
   * 
   * @author ntl
   *
   */
  private interface TableState {
    /**
     * Adds a card to the state.
     * @param card
     */
    public void addCard(VieCard card);

    /**
     * Displays the way the table looks in the middle of the hand.
     * 
     * @return string representation of the table mid play.
     */
    public String drawStateMarkingNextCardLoc();
  }

  /**
   * tracks the intermediate state of dealt cards For examples tracks the 4 cards dealt for the
   * initial deal or the two cards that are dealt to two hands in a split.
   */
  TableState state;


  
 
  /* 
   * Chooses a card by presenting choices to the console, and reading the user's choice from standard in.
   * The state of the table is printed to the console prior to selection.
   * 
   * @see com.ibm.vie.blackjack.casino.crooked.CardSwitchCardChooser#chooseCard(com.ibm.vie.blackjack.casino.crooked.CardManagerViewer)
   */
  @Override
  public VieCard chooseCard(CardManagerViewer viewer) {
    /**
     * Choose card logic here
     */
    VieCard choosenCard = null; // default choose random card


    while (choosenCard == null) {
      System.out.println("------- CHANGE NEXT CARD OPTION ----------");
      System.out.println(state.drawStateMarkingNextCardLoc());
      System.out.println("\n\n");
      System.out.println("Next Card will be " + viewer.peek());

      Map<Rank, Collection<VieCard>> availableCardsByRank =
          new TreeMap<Rank, Collection<VieCard>>(viewer.getRankToCardsMap());
      System.out.println("CARD CHOICES");
      Map<Integer, VieCard> commandChoice = new java.util.HashMap<>();
      for (Entry<Rank, Collection<VieCard>> entry : availableCardsByRank.entrySet()) {
        commandChoice.put((entry.getKey().ordinal() + 1), entry.getValue().iterator().next());
        System.out
            .println((entry.getKey().ordinal() + 1) + ") " + entry.getValue().iterator().next());
      }
      System.out.println();

      System.out.println("999 = shuffle");
      System.out.println("998 = use ordinary next card");

      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      int num;
      try {
        num = Integer.parseInt(reader.readLine());
        System.out.print("You Choose: " + num + "\n");
        if (num == 999) {
          viewer.shuffle();
        } else if (num == 998) {
          choosenCard = viewer.peek();
        } else {
          choosenCard = commandChoice.get(num);
        }
      } catch (NumberFormatException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }



    }


    state.addCard(choosenCard);

    System.out.println("------- DONE CHANGE NEXT CARD OPTION ----------\n\n\n");
    return choosenCard;
  }



 
  /**
   * Switches the state to the dealer's turn
   * 
   * @see com.ibm.vie.blackjack.casino.observer.TableObserver#observeDealerTurn(com.ibm.vie.blackjack.player.GameInfo, com.ibm.vie.blackjack.casino.hand.VieDealerHand, java.util.List)
   */
  @Override
  public void observeDealerTurn(GameInfo gameInfo, VieDealerHand dealerHand,
      List<ViePlayerHand> allPlayerHands) {

    state = new DealerTurnState(dealerHand, allPlayerHands);

  }

 
  /** 
   * Changes the state based on the player's decision
   * 
   * @see com.ibm.vie.blackjack.casino.observer.TableObserver#observeDecisionMade(com.ibm.vie.blackjack.player.GameInfo, com.ibm.vie.blackjack.player.PlayerDecision, com.ibm.vie.blackjack.casino.hand.ViePlayerHand, com.ibm.vie.blackjack.casino.card.VieCard, java.util.List)
   */
  @Override
  public void observeDecisionMade(GameInfo gameInfo, PlayerDecision decision,
      ViePlayerHand currentHand, VieCard dealerUpCard, List<ViePlayerHand> allPlayerHands) {


    System.out.println("Decision was made to " + decision.toString());
    switch (decision) {
      case DOUBLE_DOWN:
      case HIT:
        state = new PlayerTurnHitOrDoubleState(currentHand, dealerUpCard, allPlayerHands, decision);
        break;
      case SPLIT:
        state = new PlayerTurnSplitState(currentHand, dealerUpCard, allPlayerHands);
        break;
      case STAND:
        state = null;
        break;
    }

  }

  /**
   * Changes the state to the initial deal state
   */
  @Override
  public void observeInitialBet(GameInfo gameInfo, int betAmount) {

    state = new InitialDealState();
  }



}
