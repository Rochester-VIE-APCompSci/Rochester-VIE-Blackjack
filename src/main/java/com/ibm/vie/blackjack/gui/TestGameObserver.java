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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.exceptions.BlackjackRuleViolationException;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHandPayout;
import com.ibm.vie.blackjack.casino.observer.TableObserver;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;

public class TestGameObserver extends JPanel implements TableObserver {

	private ScreenTestGame screenTestGame;

	JFrame myFrame;
	JPanel topPanel = new JPanel();
	JPanel dcardPanel = new JPanel();
	JPanel pcardPanel = new JPanel();
	JButton continueButton = new JButton();
	JLabel dealerlabel = new JLabel();
	private Blackjack blackjack;

	JLabel playerMoney = new JLabel();

	private boolean bStepMode = true; // TODO: add interface to choose this

	public TestGameObserver(ScreenTestGame screenTestGame, Blackjack blackjack) {
		this.screenTestGame = screenTestGame;
		this.blackjack = blackjack;
	}

	private void doThreadWait(GameInfo gameInfo) {
		if (bStepMode) {
			try {
				screenTestGame.threadWaitObject = gameInfo;
				synchronized (screenTestGame.threadWaitObject) {
					screenTestGame.continueButton.setEnabled(true); // can't continue twice
					screenTestGame.threadWaitObject.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Print the state of the table after a player decision has taken effect. For
	 * example if the Player hits, the additional card will be included in the
	 * output
	 */
	@Override
	public void observeDecisionMade(final GameInfo gameInfo, final PlayerDecision decision, final ViePlayerHand currentHand, final VieCard dealerUpCard, final List<ViePlayerHand> allPlayerHands) {
		// System.out.println("\nDecision=" + decision.toString() + "\n" + gameInfo.toString() + "\nDealer Up Card: "
		// 		+ dealerUpCard.toString() + "\nPlayer Hands:\n");

		// playerMoney.setText("Player money: $" + gameInfo.getAvailableMoney());
		while (!blackjack.uiFinished) {
			screenTestGame.updateGameStatus("<html>" + "<B>" + "Decision: " + decision.toString() + "<B>"  + "<html>");
			return;
		}
	}

	// /** OLD VERSION
	//  * Print the state of the table after a player decision has taken effect. For
	//  * example if the Player hits, the additional card will be included in the
	//  * output
	//  */
	// @Override
	// public void observeDecisionMade(final GameInfo gameInfo, final PlayerDecision decision,
	// 		final ViePlayerHand currentHand, final VieCard dealerUpCard, final List<ViePlayerHand> allPlayerHands) {
	// 	System.out.println("\nDecision=" + decision.toString() + "\n" + gameInfo.toString() + "\nDealer Up Card: "
	// 			+ dealerUpCard.toString() + "\nPlayer Hands:\n");
	//
	// 	SwingUtilities.invokeLater(new Runnable() {
	// 		public void run() {
	// 			playerMoney.setText("Player money: $" + gameInfo.getAvailableMoney());
	// 		}
	// 	});
	//
	// 	try {
	// 		Thread.sleep(500);
	// 	} catch (InterruptedException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
	//
	// }

	/**
	 * Print the state of the table when the round is over to the console.
	 */
	@Override
	public void observeEndOfRound(final GameInfo gameInfo, final VieDealerHand vieDealerHand,
			final List<ViePlayerHandPayout> hands) {
		while (!blackjack.uiFinished) {
			// System.out.println("\n\nEND OF ROUND");
			// System.out.println(gameInfo.toString() + "\n" + "Dealer Hand: " + vieDealerHand.toString() + "\nPlayer Hands:\n"
			// 		+ prettyPrintPlayerHands(hands));

			int totalPayout = 0;
			for (ViePlayerHandPayout handPayout : hands) {
	//			if(handPayout.getOutcome() == HandOutcome.DEALER_WIN) {
	//				totalPayout -= handPayout.getBetPaid();
	//			}
				totalPayout -= handPayout.getBetPaid();
				totalPayout += handPayout.getPayout();
			}
			JLabel gameStatus;
			if (totalPayout < 0) {
				screenTestGame.updateGameStatus("<html>" + "<B>" + "End of Round" + "<br/>" + "You lost $" + Math.abs(totalPayout) + "<B><br/><br/> Press <span style='color: green'>Continue</span>"  + "<html>");
			}
			else {
				screenTestGame.updateGameStatus("<html>" + "<B>" + "End of Round" + "<br/>" + "You won $" + totalPayout + "<B><br/><br/> Press <span style='color: green'>Continue</span>"  + "<html>");
			}
			screenTestGame.moveStepArrow("continue");
			JLabel playerMoney = new JLabel("Player money: $" + gameInfo.getAvailableMoney());
			JPanel playerMoneyPanel = new JPanel();
			playerMoneyPanel.setLayout(new BorderLayout());
			playerMoneyPanel.add(playerMoney, BorderLayout.SOUTH);
			playerMoneyPanel.setBounds(840, 500, 150, 20);
			screenTestGame.playerTotalMoney = playerMoneyPanel;
			screenTestGame.update();

			placeDealerCards(vieDealerHand);
			screenTestGame.roundHasEnded = true;
			doThreadWait(gameInfo);
			return;
		}
	}

	// /** OLD VERSION
	//  * Print the state of the table when the round is over to the console.
	//  */
	// @Override
	// public void observeEndOfRound(final GameInfo gameInfo, final VieDealerHand vieDealerHand,
	// 		final List<ViePlayerHandPayout> hands) {
	// 	System.out.println("\n\nEND OF ROUND");
	// 	System.out.println(gameInfo.toString() + "\n" + "Dealer Hand: " + vieDealerHand.toString() + "\nPlayer Hands:\n"
	// 			+ prettyPrintPlayerHands(hands));
	//
	// 	SwingUtilities.invokeLater(new Runnable() {
	// 		public void run() {
	// 			playerMoney.setText("Player money: $" + gameInfo.getAvailableMoney());
	//
	// 			// remove the current dealer-card panel
	// 			dcardPanel.removeAll();
	//
	// 			placeDealerCards(vieDealerHand);
	//
	// 			add(dcardPanel, BorderLayout.CENTER);
	//
	// 			myFrame.pack();
	// 			myFrame.setVisible(true);
	// 		}
	// 	});
	//
	// 	doThreadWait(gameInfo);
	// }

	private void placeDealerCards(VieDealerHand hand) {
		while (!blackjack.uiFinished) {
	//		GuiHand dealerHand = new GuiHand(hand);
			HandSet dealerHand = new HandSet(hand);

			// 73 * 97
			// 156
			dealerHand.setBounds(330, 50, dealerHand.totalWidth, dealerHand.totalHeight);
	//		dealerHand.setBackground(Color.RED);
			screenTestGame.dealerCards = dealerHand;
			screenTestGame.update();
			return;
		}
	}

//	@Override - OLD VERSION
//	public void observeInitialBet(final GameInfo gameInfo, final int betAmount) {
//		System.out.println("\nINITIAL BET: " + betAmount);
//
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				playerMoney.setText("$" + gameInfo.getAvailableMoney());
//
//				pcardPanel.removeAll();
//				dcardPanel.removeAll();
//				dcardPanel.add(dealerlabel);
//
//				JLabel initialBet = new JLabel();
//				initialBet.setText("Player Initial Bet: $" + betAmount);
//				pcardPanel.add(initialBet);
//
//				myFrame.pack();
//				myFrame.setVisible(true);
//			}
//		});
//
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	@Override
	public void observeInitialBet(final GameInfo gameInfo, final int betAmount) {
		// System.out.println("\nINITIAL BET: " + betAmount);

		// playerMoney.setText("$" + gameInfo.getAvailableMoney());

		// initialBet.setText("Player Initial Bet: $" + betAmount);

	}

	// @Override OLD VERSION
	// public void observeInitialBet(final GameInfo gameInfo, final int betAmount) {
	// 	System.out.println("\nINITIAL BET: " + betAmount);
	//
	// 	playerMoney.setText("$" + gameInfo.getAvailableMoney());
	//
	// 	pcardPanel.removeAll();
	// 	dcardPanel.removeAll();
	// 	dcardPanel.add(dealerlabel);
	//
	// 	JLabel initialBet = new JLabel();
	// 	initialBet.setText("Player Initial Bet: $" + betAmount);
	// 	pcardPanel.add(initialBet);
	//
	// 	myFrame.pack();
	// 	myFrame.setVisible(true);
	//
	// 	try {
	// 		Thread.sleep(500);
	// 	} catch (InterruptedException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
	// }

	/**
	 * Print the state of the table when a player has a decision to make. The hand
	 * requiring a decision will be marked with "**"
	 */
	@Override
	public void observePlayerTurn(final GameInfo gameInfo, final ViePlayerHand currentHand, final VieCard dealerUpCard,
			final List<ViePlayerHand> allPlayerHands) {
		// System.out.println("\nDecision to make:\n" + gameInfo.toString() + "\nDealer Up Card: " + dealerUpCard.toString()
		// 		+ "\nPlayer Hands:\n" + prettyPrintPlayerHands(allPlayerHands) + "\n");
		while (!blackjack.uiFinished) {
			screenTestGame.updateGameStatus("<html>" + "<B>" + "Initial Bet: $" + currentHand.getBetPaid() + "<br/>" + "<br/>" + "Player - strategy is now making a decision. <br/><br/> Press <span style='color: green'>Continue</span>" + "<B>"  + "<html>");
			screenTestGame.moveStepArrow("continue");
			
			JLabel playerMoney = new JLabel("Player money: $" + gameInfo.getAvailableMoney());
			JPanel playerMoneyPanel = new JPanel();
			playerMoneyPanel.setLayout(new BorderLayout());
			playerMoneyPanel.add(playerMoney, BorderLayout.SOUTH);
			playerMoneyPanel.setBounds(840, 500, 150, 20);
			screenTestGame.playerTotalMoney = playerMoneyPanel;

			HandSet playerHand = new HandSet(new ArrayList<>(allPlayerHands));
			playerHand.setBounds(330, 300, playerHand.totalWidth, playerHand.totalHeight);
			screenTestGame.playerCards = playerHand;


			HandSet dealerHand = new HandSet(dealerUpCard);
			dealerHand.setBounds(330, 50, dealerHand.totalWidth, dealerHand.totalHeight);
			screenTestGame.dealerCards = dealerHand;

			JLabel roundNumber = new JLabel("Round Number: " + gameInfo.getRoundNumber());
			JPanel roundNumberPanel = new JPanel();
			roundNumberPanel.setLayout(new BorderLayout());
			roundNumberPanel.add(roundNumber, BorderLayout.SOUTH);
			roundNumberPanel.setBounds(840, 520, 150, 20);
			screenTestGame.roundNumber = roundNumberPanel;

			screenTestGame.update();

			doThreadWait(gameInfo);
			return;
		}
	}

	@Override
	public void observeGameIsOver(final GameInfo gameInfo) {
		while (!blackjack.uiFinished) {
			screenTestGame.updateGameStatus("<html>" + "<B>" + "Game Completed!" +"<br/><br/>" + "Return to the Main Menu to play again" + "<B>"  + "<html>");
			return;
		}
	}

  public void observeDecisionOutcome(final GameInfo gameInfo, final PlayerDecision decision,
      final ViePlayerHand currentHand, final VieCard dealerUpCard,
      final List<ViePlayerHand> allPlayerHands) {
		// System.out.println("\nDecision outcome:\n" + gameInfo.toString() + "\nPlayer Hands:\n" + prettyPrintPlayerHands(allPlayerHands) + "\n");
		while (!blackjack.uiFinished) {
//			JLabel gameStatus = new JLabel("<html>" + "<B>" + "Player Decision: " + decision.toString() + "\"<br/>Press Continue to move on" +  "<B>"  + "<html>");
//			JPanel gameStatusPanel = new JPanel();
//			gameStatusPanel.setLayout(new BorderLayout());
//			gameStatusPanel.add(gameStatus, BorderLayout.SOUTH);
//			gameStatusPanel.setBounds(200, 300, 150, 80);
//			screenTestGame.gameStatus = gameStatusPanel;

			JLabel playerMoney = new JLabel("Player money: $" + gameInfo.getAvailableMoney());
			JPanel playerMoneyPanel = new JPanel();
			playerMoneyPanel.setLayout(new BorderLayout());
			playerMoneyPanel.add(playerMoney, BorderLayout.SOUTH);
			playerMoneyPanel.setBounds(840, 500, 150, 20);
			screenTestGame.playerTotalMoney = playerMoneyPanel;

			HandSet playerHand = new HandSet(new ArrayList<ViePlayerHand>(allPlayerHands));
			playerHand.setBounds(330, 300, playerHand.totalWidth, playerHand.totalHeight);
			screenTestGame.playerCards = playerHand;

			screenTestGame.update();

//			doThreadWait(gameInfo);
			return;
		}
  }

	// /** OLD VERSION
	//  * Print the state of the table when a player has a decision to make. The hand
	//  * requiring a decision will be marked with "**"
	//  */
	// @Override
	// public void observePlayerTurn(final GameInfo gameInfo, final ViePlayerHand currentHand, final VieCard dealerUpCard,
	// 		final List<ViePlayerHand> allPlayerHands) {
	// 	System.out.println("\nDecision to make:\n" + gameInfo.toString() + "\nDealer Up Card: " + dealerUpCard.toString()
	// 			+ "\nPlayer Hands:\n" + prettyPrintPlayerHands(allPlayerHands) + "\n");
	//
	// 	SwingUtilities.invokeLater(new Runnable() {
	// 		public void run() {
	// 			playerMoney.setText("Player money: $" + gameInfo.getAvailableMoney());
	//
	// 			// remove the current player-card panel
	// 			pcardPanel.removeAll();
	// 			dcardPanel.removeAll();
	//
	// 			// for each hand, create a hand panel (containing x number of cards)
	// 			for (ViePlayerHand hand : allPlayerHands) {
	// 				placePlayerCards(hand);
	// 			}
	//
	// 			myFrame.pack();
	// 			myFrame.setVisible(true);
	//
	// 			dealerlabel.setText("Dealer: ?");
	// 			dcardPanel.add(dealerlabel);
	// 			JLabel dealerCard = new JLabel(new ImageIcon(getCardImageFileLocation(dealerUpCard)));
	// 			dcardPanel.add(dealerCard);
	//
	// 			add(dcardPanel, BorderLayout.CENTER);
	// 			add(pcardPanel, BorderLayout.SOUTH);
	//
	// 			myFrame.pack();
	// 			myFrame.setVisible(true);
	// 		}
	// 	});
	//
	// 	doThreadWait(gameInfo);
	// }

	@Override
	public void observeProgramError(final GameInfo gameInfo, final List<ViePlayerHand> allPlayerHands,
			final VieDealerHand vieDealerHand, final Exception e) {

		while (!blackjack.uiFinished) {
		System.err.println("\n\nERROR ERROR ERROR ERROR\n");
			if (vieDealerHand != null && allPlayerHands != null) {
				System.err.println(gameInfo.toString() + "\n" + "Dealer Hand: " + vieDealerHand.toString() + "\nPlayer Hands:\n"
						+ prettyPrintPlayerHands(allPlayerHands) + "\n");
			}

			if (e.getMessage() != null) {
				System.err.println(e.getMessage());
			}

			// rule violations are detected and thrown from frame work to framework, so only
			// include a
			// stack trace if the error appears to be from the student's code
			if (!(e instanceof BlackjackRuleViolationException)) {
				e.printStackTrace();
			}
			System.err.flush();
			return;
		}
	}

	/**
	 * Convert a player's hands to a string
	 *
	 * @param hands
	 * @return string representation of the list of hands
	 */
	protected String prettyPrintPlayerHands(final List<? extends ViePlayerHand> hands) {
		return hands.stream().map(Object::toString).collect(Collectors.joining("\n"));
	}

	/**
	 * Convert a player's hands to a string, marking the current hand with '**'
	 *
	 * @param hands
	 *          list of hands for the player
	 * @param activeHand
	 *          the current hand for which decisions are to be made for
	 * @return string representation of the list of hands
	 */
	protected String prettyPrintPlayerHands(final List<? extends ViePlayerHand> hands, final ViePlayerHand activeHand) {
		return hands.stream().map((h) -> (h == activeHand) ? "**" + h.toString() : h.toString())
				.collect(Collectors.joining("\n"));
	}

}
