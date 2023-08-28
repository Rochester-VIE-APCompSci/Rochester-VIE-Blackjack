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
package com.ibm.vie.blackjack.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
public class CrookedObserverGui implements CardSwitchCardChooser, TableObserver {

  private ScreenTestGame screenTestGame;
  private VieCard choosenCard;
	private Blackjack blackjack;
	private ArrayList<GameButton> cardButtons;

  public CrookedObserverGui(ScreenTestGame screenTestGame, Blackjack blackjack) {
    this.screenTestGame = screenTestGame;
    this.blackjack = blackjack;
  }

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
    private ScreenTestGame screenTestGame;

    public DealerTurnState(VieDealerHand dealerInitialHand, List<ViePlayerHand> allPlayerHands, ScreenTestGame screenTestGame) {
      this.dealerInitialHand = dealerInitialHand;
      this.allPlayerHands = allPlayerHands;
      this.screenTestGame = screenTestGame;
    }

    @Override
    public void addCard(VieCard card) {
      dealerCards.add(card);
//      HandSet dealerHand = new HandSet(dealerCards, true);
//      dealerHand.setBounds(430, 50, dealerHand.totalWidth, dealerHand.totalHeight);
//      screenTestGame.dealerCards = dealerHand;
//      screenTestGame.update();
    }

    public String drawStateMarkingNextCardLoc() {
      screenTestGame.updateGameStatus("<html>" + "<B>" + "<span style='color:red'>Select a card from below</span> that will be drawn by the Dealer" + "<B>"  + "<html>");
      screenTestGame.moveStepArrow("draw");
	  StringBuilder sb = new StringBuilder();
      sb.append("DRAWING A CARD FOR THE DEALER'S TURN: \n");
      sb.append("DEALER CARDS: ");
      dealerInitialHand.getCards().forEach(c -> sb.append(c + ", "));
      // draw the dealer cards here


      VieCard[] existingCards = new VieCard[dealerInitialHand.getCards().size() + dealerCards.size()];
      int i = 0;
      for (VieCard card : dealerInitialHand.getCards()) {
      		existingCards[i++] = card;
      }
      for (VieCard card : dealerCards) {
      		existingCards[i++] = card;
      }
      VieDealerHand vieDealerHand = new VieDealerHand(existingCards);
      sb.append("HEY HI HELLO: ");


      HandSet dealerHand = new HandSet(vieDealerHand);
      dealerHand.setBounds(330, 50, dealerHand.totalWidth, dealerHand.totalHeight);
      screenTestGame.dealerCards = dealerHand;
      screenTestGame.update();

      sb.append(" <***NEXT CARD HERE***>\n");
      sb.append("PLAYER HANDS:\n");
      allPlayerHands.forEach(h -> sb.append(h.toString() + "\n"));

      return sb.toString();
    }

//    public String drawStateMarkingNextCardLoc() {
//      StringBuilder sb = new StringBuilder();
//      sb.append("DRAWING A CARD FOR THE DEALER'S TURN: \n");
//      sb.append("DEALER CARDS: ");
//      dealerInitialHand.getCards().forEach(c -> sb.append(c + ", "));
//      // draw the dealer cards here
//      if (dealerInitialHand.getDealerUpCard() != null) {
//        HandSet dealerHand = new HandSet(dealerInitialHand.getDealerUpCard());
//        dealerHand.setBounds(430, 50, dealerHand.totalWidth, dealerHand.totalHeight);
//        screenTestGame.dealerCards = dealerHand;
//        screenTestGame.update();
//      }
//
//      sb.append(" <***NEXT CARD HERE***>\n");
//      sb.append("PLAYER HANDS:\n");
//      allPlayerHands.forEach(h -> sb.append(h.toString() + "\n"));
//      // draw the player cards here
//
//
//      System.out.println("hey!!!!!!@");
//
//      return sb.toString();
//    }


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
    private ScreenTestGame screenTestGame;

    public InitialDealState(ScreenTestGame screenTestGame) {
    		this.screenTestGame = screenTestGame;
    }

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
      if (playerCards.size() > 0) {
      	
        HandSet playerHand = new HandSet(playerCards, true);
        playerHand.setBounds(330, 300, playerHand.totalWidth, playerHand.totalHeight);
        screenTestGame.playerCards = playerHand;

        screenTestGame.update();
      }
      if(playerCards.size() == 0) {
    	  screenTestGame.updateGameStatus("<html><span style='color: red'>Select a card from below</span> for the Player's hand </html>");
    	  screenTestGame.moveStepArrow("p1");
          sb.append(" <***PLAYERS FIRST CARD HERE***>\n");
      } else if(playerCards.size() == 1) {
    	  screenTestGame.updateGameStatus("<html><span style='color: red'>Select a second card from below</span> for the Player's hand </html>");
    	  screenTestGame.moveStepArrow("p2");
          sb.append(" <***PLAYERS SECOND CARD HERE***>\n");
      }
      sb.append("\n");

      if (playerCards.size() >= 2) {
    	screenTestGame.updateGameStatus("<html><span style='color: red'>Select a card</span> from below for the Dealer's hand <html>");
    	
        sb.append("DEALER UP CARD: "
            + ((dealerUpCard != null) ? dealerUpCard.toString() + "\n": "<***NEXT CARD HERE>\n"));
        if (dealerUpCard != null) {
          sb.append("DEALER DOWN CARD: "
              + ((dealerDownCard != null) ? dealerDownCard.toString() : "<***NEXT CARD HERE>\n"));
          HandSet dealerHand = new HandSet(dealerUpCard);
          dealerHand.setBounds(330, 50, dealerHand.totalWidth, dealerHand.totalHeight);
          screenTestGame.dealerCards = dealerHand;
          screenTestGame.moveStepArrow("d2");
        } else {
        	screenTestGame.moveStepArrow("d1");
        }
      }
      sb.append("\n");
      screenTestGame.update();
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
    private ScreenTestGame screenTestGame;

    public PlayerTurnHitOrDoubleState(ViePlayerHand currentHand, VieCard dealerUpCard,
        List<ViePlayerHand> allPlayerHands, PlayerDecision decision, ScreenTestGame screenTestGame) {
      this.currentHand = currentHand;
      this.allPlayerHands = allPlayerHands;
      this.dealerUpCard = dealerUpCard;
      this.decision = decision;
      this.screenTestGame = screenTestGame;
    }

    @Override
    public void addCard(VieCard card) {
      // there are no intermediate draws, so nothing to track
    }


    public String drawStateMarkingNextCardLoc() {
      screenTestGame.updateGameStatus("<html>" + "<B>" + "The player has decided to <span style='color:red'>" + this.decision + "</span> on the current hand<br/><br/> <span style='color:red'>Select a card from below</span> to be drawn by the player" + "<B>"  + "<html>");
      screenTestGame.moveStepArrow("draw");
	  
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
    choosenCard = null; // default choose random card

   // System.out.println("------- CHANGE NEXT CARD OPTION ----------");
   // System.out.println(state.drawStateMarkingNextCardLoc());
   // System.out.println("\n\n");
//   System.out.println("Next Card will be " + viewer.peek());

    state.drawStateMarkingNextCardLoc();
    // System.out.println("Next Card will be " + viewer.peek());

    Map<Rank, Collection<VieCard>> availableCardsByRank =
        new TreeMap<Rank, Collection<VieCard>>(viewer.getRankToCardsMap());
    Map<Rank, VieCard> commandChoice = new java.util.HashMap<>();

    try {
			this.screenTestGame.cardSelection = createCardSelection(availableCardsByRank, commandChoice);
			this.screenTestGame.update();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    if (blackjack.uiFinished) {
    		return null;
    }
    // System.out.println("before while loop...");
    doThreadWait(viewer);

//    while (choosenCard == null) {
//
////      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
////      int num;
////      try {
////        num = Integer.parseInt(reader.readLine());
////        System.out.print("You Choose: " + num + "\n");
////        if (num == 999) {
////          viewer.shuffle();
////        } else if (num == 998) {
////          choosenCard = viewer.peek();
////        } else {
////          choosenCard = commandChoice.get(num);
////        }
////      } catch (NumberFormatException e) {
////        e.printStackTrace();
////      } catch (IOException e) {
////        e.printStackTrace();
////      }
//
//    }
    // System.out.println("card picked!!!");


    state.addCard(choosenCard);

    // System.out.println("------- DONE CHANGE NEXT CARD OPTION ----------\n\n\n");
    return choosenCard;
  }

  // /* OLD VERSION
  //  * Chooses a card by presenting choices to the console, and reading the user's choice from standard in.
  //  * The state of the table is printed to the console prior to selection.
  //  *
  //  * @see com.ibm.vie.blackjack.casino.crooked.CardSwitchCardChooser#chooseCard(com.ibm.vie.blackjack.casino.crooked.CardManagerViewer)
  //  */
  // @Override
  // public VieCard chooseCard(CardManagerViewer viewer) {
  //   /**
  //    * Choose card logic here
  //    */
  //   VieCard choosenCard = null; // default choose random card
  //
  //
  //   while (choosenCard == null) {
  //     System.out.println("------- CHANGE NEXT CARD OPTION ----------");
  //     System.out.println(state.drawStateMarkingNextCardLoc());
  //     System.out.println("\n\n");
  //     System.out.println("Next Card will be " + viewer.peek());
  //     try {
  //       this.screenTestGame.cardSelection = createCardSelection();
  //       this.screenTestGame.update();
  //     } catch (IOException e1) {
  //       // TODO Auto-generated catch block
  //       e1.printStackTrace();
  //     }
  //
  //     Map<Rank, Collection<VieCard>> availableCardsByRank =
  //         new TreeMap<Rank, Collection<VieCard>>(viewer.getRankToCardsMap());
  //     System.out.println("CARD CHOICES");
  //     Map<Integer, VieCard> commandChoice = new java.util.HashMap<>();
  //     for (Entry<Rank, Collection<VieCard>> entry : availableCardsByRank.entrySet()) {
  //       commandChoice.put((entry.getKey().ordinal() + 1), entry.getValue().iterator().next());
  //       System.out
  //           .println((entry.getKey().ordinal() + 1) + ") " + entry.getValue().iterator().next());
  //     }
  //     System.out.println();
  //
  //     System.out.println("999 = shuffle");
  //     System.out.println("998 = use ordinary next card");
  //
  //     BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  //     int num;
  //     try {
  //       num = Integer.parseInt(reader.readLine());
  //       System.out.print("You Choose: " + num + "\n");
  //       if (num == 999) {
  //         viewer.shuffle();
  //       } else if (num == 998) {
  //         choosenCard = viewer.peek();
  //       } else {
  //         choosenCard = commandChoice.get(num);
  //       }
  //     } catch (NumberFormatException e) {
  //       e.printStackTrace();
  //     } catch (IOException e) {
  //       e.printStackTrace();
  //     }
  //
  //   }
  //
  //
  //   state.addCard(choosenCard);
  //
  //   System.out.println("------- DONE CHANGE NEXT CARD OPTION ----------\n\n\n");
  //   return choosenCard;
  // }




  /**
   * Switches the state to the dealer's turn
   *
   * @see com.ibm.vie.blackjack.casino.observer.TableObserver#observeDealerTurn(com.ibm.vie.blackjack.player.GameInfo, com.ibm.vie.blackjack.casino.hand.VieDealerHand, java.util.List)
   */
  @Override
  public void observeDealerTurn(GameInfo gameInfo, VieDealerHand dealerHand,
      List<ViePlayerHand> allPlayerHands) {

    state = new DealerTurnState(dealerHand, allPlayerHands, screenTestGame);

  }
  
	@Override
	public void observePlayerTurn(final GameInfo gameInfo, final ViePlayerHand currentHand, final VieCard dealerUpCard,
			final List<ViePlayerHand> allPlayerHands) {
		for (GameButton button : cardButtons) {
			button.setEnabled(false);
		}
	}


  /**
   * Changes the state based on the player's decision
   *
   * @see com.ibm.vie.blackjack.casino.observer.TableObserver#observeDecisionMade(com.ibm.vie.blackjack.player.GameInfo, com.ibm.vie.blackjack.player.PlayerDecision, com.ibm.vie.blackjack.casino.hand.ViePlayerHand, com.ibm.vie.blackjack.casino.card.VieCard, java.util.List)
   */
  @Override
  public void observeDecisionMade(GameInfo gameInfo, PlayerDecision decision,
      ViePlayerHand currentHand, VieCard dealerUpCard, List<ViePlayerHand> allPlayerHands) {

    while (!blackjack.uiFinished) {

      // System.out.println("Decision was made to " + decision.toString());
      switch (decision) {
        case DOUBLE_DOWN:
        case HIT:
          state = new PlayerTurnHitOrDoubleState(currentHand, dealerUpCard, allPlayerHands, decision, screenTestGame);
          break;
        case SPLIT:
          state = new PlayerTurnSplitState(currentHand, dealerUpCard, allPlayerHands);
          break;
        case STAND:
          state = null;
          break;
      }
      return;
    }

  }

  /**
   * Changes the state to the initial deal state
   */
  @Override
  public void observeInitialBet(GameInfo gameInfo, int betAmount) {
    while (!blackjack.uiFinished) {
      state = new InitialDealState(screenTestGame);
      return;
    }
  }

  private JPanel createCardSelection(Map<Rank, Collection<VieCard>> availableCardsByRank, Map<Rank, VieCard> commandChoice) throws IOException {

    // 6 column, 2 rows for 12 cards
    JPanel cardSelectionPanel = new JPanel();
    cardSelectionPanel.setLayout(new GridLayout(2, 7, 10, 10));
    cardSelectionPanel.setBounds(230, 500, 585, 230);
    cardSelectionPanel.setBorder(BorderFactory.createTitledBorder("Available cards"));
    cardButtons = new ArrayList<GameButton>();

    for (Entry<Rank, Collection<VieCard>> entry : availableCardsByRank.entrySet()) {
      VieCard card = entry.getValue().iterator().next();
      String rank = card.getRank().toString().toLowerCase();
      String suit = card.getSuit().toString().toLowerCase();

			if (!(rank.equals("ace") || rank.equals("king") || rank.equals("queen") || rank.equals("jack"))) {
				rank = "" + card.toCard().getMinScore();
			}

      GameButton cardButton = getCardButton(rank, suit);

      cardButton.addActionListener(new ActionListener() {
	  			public void actionPerformed(ActionEvent e) {
	  				synchronized (screenTestGame.threadWaitObjectCrooked ) {
	  					choosenCard = commandChoice.get(entry.getKey());
	  					screenTestGame.threadWaitObjectCrooked .notify(); // Wake up game thread
	  				}
	  			}
	  		});
      
      cardSelectionPanel.add(cardButton);
      cardButtons.add(cardButton);

      commandChoice.put(entry.getKey(), entry.getValue().iterator().next());
//      commandChoice.put((entry.getKey().ordinal() + 1), entry.getValue().iterator().next());
//      System.out
//          .println((entry.getKey().ordinal() + 1) + ") " + entry.getValue().iterator().next());

      // print card button that matches up
    }

    // Each box is a button with a Card image.
    return cardSelectionPanel;
  }


  private JPanel createInactiveCardSelection() throws IOException {
		// 6 column, 2 rows for 12 cards
		JPanel cardSelectionPanel = new JPanel();
		cardSelectionPanel.setLayout(new GridLayout(2, 7, 10, 10));
		cardSelectionPanel.setBounds(230, 500, 585, 230);

		GameButton cardButtonAce = getCardButton("ace", "diamonds");
		cardButtonAce.setEnabled(false);
		cardSelectionPanel.add(cardButtonAce);

		for(int i = 2; i < 11; i++){
			GameButton cardButton = getCardButton(i + "", "diamonds");
			cardButton.setEnabled(false);
			cardSelectionPanel.add(cardButton);
		}

		GameButton cardButtonJack = getCardButton("jack", "diamonds");
		GameButton cardButtonQueen = getCardButton("queen", "diamonds");
		GameButton cardButtonKing = getCardButton("king", "diamonds");

		cardSelectionPanel.add(cardButtonJack);
		cardSelectionPanel.add(cardButtonQueen);
		cardSelectionPanel.add(cardButtonKing);

		// 1 card to the left for Ace

		// Each box is a button with a Card image.
		return cardSelectionPanel;
	}

  private GameButton getCardButton(String cardRank, String cardSuit) throws IOException {
    URL imageUrl = getClass().getResource(Blackjack.getCardImageFileLocation(cardRank, cardSuit));
    BufferedImage in;
    in = ImageIO.read(imageUrl);
    GameButton card = new GameButton(in, 1, 1);
    return card;
  }

	private void doThreadWait(CardManagerViewer viewer) {
		try {
			screenTestGame.threadWaitObjectCrooked  = viewer;
			synchronized (screenTestGame.threadWaitObjectCrooked ) {
//				continueButton.setEnabled(true); // can't continue twice
				screenTestGame.threadWaitObjectCrooked .wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
