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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.exceptions.BlackjackRuleViolationException;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHandPayout;
import com.ibm.vie.blackjack.casino.observer.TableObserver;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;

@SuppressWarnings("serial")
public class NormalGameObserver extends JPanel implements TableObserver {

	private ScreenNormalGame screenNormalGame;

	JFrame myFrame;
	JPanel topPanel = new JPanel();
	JPanel dcardPanel = new JPanel();
	JPanel pcardPanel = new JPanel();
	JButton continueButton = new JButton();
	JLabel dealerlabel = new JLabel();
	JLabel playerMoney = new JLabel();
	protected Blackjack blackjack;

	public NormalGameObserver(ScreenNormalGame screenNormalGame, Blackjack blackjack) {
		this.screenNormalGame = screenNormalGame;
		this.blackjack = blackjack;
	}

	private void doThreadWait(GameInfo gameInfo) {
		screenNormalGame.pauseThread(gameInfo);
	}

	/**
	 * Print the state of the table after a player decision has taken effect. For
	 * example if the Player hits, the additional card will be included in the
	 * output
	 */
	@Override
	public void observeDecisionMade(final GameInfo gameInfo, final PlayerDecision decision,
			final ViePlayerHand currentHand, final VieCard dealerUpCard, final List<ViePlayerHand> allPlayerHands) {

		if (!blackjack.uiFinished) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JLabel gameStatus = new JLabel("<html>" + "<B>" + "Player Decision: " + decision.toString() + "<B>"  + "<html>");
					JPanel gameStatusPanel = new JPanel();
					gameStatusPanel.setLayout(new BorderLayout());
					gameStatusPanel.add(gameStatus, BorderLayout.SOUTH);
					gameStatusPanel.setBounds(50, 300, 150, 150);
					screenNormalGame.gameStatus = gameStatusPanel;
				}
			});
		}

	}

	@Override
	public void observeGameIsOver(final GameInfo gameInfo) {
		if (!blackjack.uiFinished) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JLabel gameStatus = new JLabel("<html>" + "<B>" + "Game Completed!" +"<br/><br/>" + "Return to the Main Menu to play again" + "<B>"  + "<html>");
					JPanel gameStatusPanel = new JPanel();
					gameStatusPanel.setLayout(new BorderLayout());
					gameStatusPanel.add(gameStatus, BorderLayout.SOUTH);
					gameStatusPanel.setBounds(50, 300, 150, 150);
					screenNormalGame.gameStatus = gameStatusPanel;
					screenNormalGame.update();
				}
			});
		}
	}

	/**
	 * Print the state of the table when the round is over to the console.
	 */
	@Override
	public void observeEndOfRound(final GameInfo gameInfo, final VieDealerHand vieDealerHand,
			final List<ViePlayerHandPayout> hands) {
		if (!blackjack.uiFinished) {

			int totalPayout = 0;
			for (ViePlayerHandPayout handPayout : hands) {
				totalPayout -= handPayout.getBetPaid();
				totalPayout += handPayout.getPayout();
			}
			JLabel gameStatus;
			if (totalPayout < 0) {
				gameStatus = new JLabel("<html>" + "<B>" + "End of Round" + "<br/>" + "You lost $" + Math.abs(totalPayout) + "<B>"  + "<html>");
			}
			else {
				gameStatus = new JLabel("<html>" + "<B>" + "End of Round" + "<br/>" + "You won $" + totalPayout + "<B>"  + "<html>");
			}

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JPanel gameStatusPanel = new JPanel();
					gameStatusPanel.setLayout(new BorderLayout());
					gameStatusPanel.add(gameStatus, BorderLayout.SOUTH);
					gameStatusPanel.setBounds(50, 300, 150, 150);
					screenNormalGame.gameStatus = gameStatusPanel;

					JLabel playerMoney = new JLabel("Player money: $" + gameInfo.getAvailableMoney());
					JPanel playerMoneyPanel = new JPanel();
					playerMoneyPanel.setLayout(new BorderLayout());
					playerMoneyPanel.add(playerMoney, BorderLayout.SOUTH);
					playerMoneyPanel.setBounds(840, 500, 150, 20);
					screenNormalGame.playerTotalMoney = playerMoneyPanel;
					screenNormalGame.update();
					placeDealerCards(vieDealerHand);
				}
			});
			doThreadWait(gameInfo);
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

	public void observeEndEarly() {

	}

	private void placeDealerCards(VieDealerHand hand) {
		if (!blackjack.uiFinished) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					HandSet dealerHand = new HandSet(hand);

					dealerHand.setBounds(330, 50, dealerHand.totalWidth, dealerHand.totalHeight);
					screenNormalGame.dealerCards = dealerHand;
					screenNormalGame.update();
				}
			});
		}
	}

	@Override
	public void observeInitialBet(final GameInfo gameInfo, final int betAmount) {
		// System.out.println("\nINITIAL BET: " + betAmount);

		// playerMoney.setText("$" + gameInfo.getAvailableMoney());

		// initialBet.setText("Player Initial Bet: $" + betAmount);


	}


	/**
	 * Print the state of the table when a player has a decision to make. The hand
	 * requiring a decision will be marked with "**"
	 */
	@Override
	public void observePlayerTurn(final GameInfo gameInfo, final ViePlayerHand currentHand, final VieCard dealerUpCard,
			final List<ViePlayerHand> allPlayerHands) {

		if (!blackjack.uiFinished) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					JLabel gameStatus = new JLabel("<html>" + "<B>" + "Initial Bet: $" + currentHand.getBetPaid() + "<br/>" + "<br/>" + "Player-strategy now making a decision" + "<B>"  + "<html>");
					JPanel gameStatusPanel = new JPanel();
					gameStatusPanel.setLayout(new BorderLayout());
					gameStatusPanel.add(gameStatus, BorderLayout.SOUTH);
					gameStatusPanel.setBounds(50, 300, 150, 150);
					screenNormalGame.gameStatus = gameStatusPanel;

					JLabel playerMoney = new JLabel("Player money: $" + gameInfo.getAvailableMoney());
					JPanel playerMoneyPanel = new JPanel();
					playerMoneyPanel.setLayout(new BorderLayout());
					playerMoneyPanel.add(playerMoney, BorderLayout.SOUTH);
					playerMoneyPanel.setBounds(840, 500, 150, 20);
					screenNormalGame.playerTotalMoney = playerMoneyPanel;

					HandSet playerHand = new HandSet(new ArrayList<ViePlayerHand>(allPlayerHands));
					playerHand.setBounds(330, 300, playerHand.totalWidth, playerHand.totalHeight);
					screenNormalGame.playerCards = playerHand;
					// screenNormalGame.playerCards.setBackground(Color.cyan);

					HandSet dealerHand = new HandSet(dealerUpCard);
					dealerHand.setBounds(330, 50, dealerHand.totalWidth, dealerHand.totalHeight);
					screenNormalGame.dealerCards = dealerHand;

					JLabel roundNumber = new JLabel("Round Number: " + gameInfo.getRoundNumber());
					JPanel roundNumberPanel = new JPanel();
					roundNumberPanel.setLayout(new BorderLayout());
					roundNumberPanel.add(roundNumber, BorderLayout.SOUTH);
					roundNumberPanel.setBounds(840, 520, 150, 20);
					screenNormalGame.roundNumber = roundNumberPanel;

					screenNormalGame.update();
				}
			});

			doThreadWait(gameInfo);
		}
	}

	public void observeDecisionOutcome(final GameInfo gameInfo, final PlayerDecision decision,
			final ViePlayerHand currentHand, final VieCard dealerUpCard,
			final List<ViePlayerHand> allPlayerHands) {
		// System.out.println("\nDecision outcome:\n" + gameInfo.toString() + "\nPlayer Hands:\n" + prettyPrintPlayerHands(allPlayerHands) + "\n");

		if (!blackjack.uiFinished) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {


					JLabel playerMoney = new JLabel("Player money: $" + gameInfo.getAvailableMoney());
					JPanel playerMoneyPanel = new JPanel();
					playerMoneyPanel.setLayout(new BorderLayout());
					playerMoneyPanel.add(playerMoney, BorderLayout.SOUTH);
					playerMoneyPanel.setBounds(840, 500, 150, 20);
					screenNormalGame.playerTotalMoney = playerMoneyPanel;

					HandSet playerHand = new HandSet(new ArrayList<ViePlayerHand>(allPlayerHands));
					playerHand.setBounds(330, 300, playerHand.totalWidth, playerHand.totalHeight);
					screenNormalGame.playerCards = playerHand;

					screenNormalGame.update();
				}
			});

			doThreadWait(gameInfo);
		}
  }

	@Override
	public void observeProgramError(final GameInfo gameInfo, final List<ViePlayerHand> allPlayerHands,
			final VieDealerHand vieDealerHand, final Exception e) {
		if (!blackjack.uiFinished) {
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
