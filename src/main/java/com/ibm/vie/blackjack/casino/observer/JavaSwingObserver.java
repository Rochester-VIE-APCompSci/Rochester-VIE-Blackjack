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
package com.ibm.vie.blackjack.casino.observer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
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
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;

public class JavaSwingObserver extends JPanel implements TableObserver {

	JFrame myFrame;
	JPanel topPanel = new JPanel();
	JPanel dcardPanel = new JPanel();
	JPanel pcardPanel = new JPanel();
	JButton continueButton = new JButton();
	JLabel dealerlabel = new JLabel();

	JLabel playerMoney = new JLabel();
	JPanel pcardSubPanel1 = new JPanel();
	JPanel pcardSubPanel2 = new JPanel();
	JPanel pcardSubPanel3 = new JPanel();
	JPanel pcardSubPanel4 = new JPanel();
	JPanel pcardSubPanel5 = new JPanel();
	JPanel pcardSubPanel6 = new JPanel();
	private boolean bStepMode = true; // TODO: add interface to choose this
	private Object threadWaitObject = null;

	private static final String IMAGEPATH = "/cards/";

	public JavaSwingObserver() {

		topPanel.setBackground(new Color(94, 170, 255));
		dcardPanel.setBackground(new Color(94, 170, 255));
		pcardPanel.setBackground(new Color(94, 170, 255));

		topPanel.setLayout(new FlowLayout());
		continueButton.setText("Continue");
		continueButton.addActionListener(new ContinueButton());
		continueButton.setEnabled(false);

		dealerlabel.setText("  Dealer:  ");

		JLabel playerlabel1 = new JLabel();
		playerlabel1.setText("  Player:  ");

		playerMoney.setText("$0");

		topPanel.add(playerMoney);
		topPanel.add(continueButton);
		pcardPanel.add(playerlabel1);
		dcardPanel.add(dealerlabel);

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(dcardPanel, BorderLayout.CENTER);
		add(pcardPanel, BorderLayout.SOUTH);

		display();
	}

	public void display() {
		myFrame = new JFrame("BlackJack");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setContentPane(this);
		myFrame.setPreferredSize(new Dimension(700, 550));

		// Display the window.
		myFrame.pack();
		myFrame.setVisible(true);
	}

	class ContinueButton implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (threadWaitObject) {
				threadWaitObject.notify(); // Wake up game thread
				continueButton.setEnabled(false);
			}
		}
	}

	private void doThreadWait(GameInfo gameInfo) {
		if (bStepMode) {
			try {
				threadWaitObject = gameInfo;
				synchronized (threadWaitObject) {
					continueButton.setEnabled(true); // can't continue twice
					threadWaitObject.wait();
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
	public void observeDecisionMade(final GameInfo gameInfo, final PlayerDecision decision,
			final ViePlayerHand currentHand, final VieCard dealerUpCard, final List<ViePlayerHand> allPlayerHands) {
		System.out.println("\nDecision=" + decision.toString() + "\n" + gameInfo.toString() + "\nDealer Up Card: "
				+ dealerUpCard.toString() + "\nPlayer Hands:\n");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				playerMoney.setText("Player money: $" + gameInfo.getAvailableMoney());
			}
		});

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Print the state of the table when the round is over to the console.
	 */
	@Override
	public void observeEndOfRound(final GameInfo gameInfo, final VieDealerHand vieDealerHand,
			final List<ViePlayerHandPayout> hands) {
		System.out.println("\n\nEND OF ROUND");
		System.out.println(gameInfo.toString() + "\n" + "Dealer Hand: " + vieDealerHand.toString() + "\nPlayer Hands:\n"
				+ prettyPrintPlayerHands(hands));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				playerMoney.setText("Player money: $" + gameInfo.getAvailableMoney());

				// remove the current dealer-card panel
				dcardPanel.removeAll();

				placeDealerCards(vieDealerHand);

				add(dcardPanel, BorderLayout.CENTER);

				myFrame.pack();
				myFrame.setVisible(true);
			}
		});

		doThreadWait(gameInfo);
	}

	private void placeDealerCards(VieDealerHand hand) {
		JPanel dcardPanelHand = new JPanel();
		dcardPanel.add(dcardPanelHand);
		dcardPanelHand.setBackground(new Color(153, 255, 255));

		JLabel dealerlabel1 = new JLabel();
		// pcardPanel.add(playerlabel1);
		dealerlabel1.setText("Hand value: " + hand.getScoreDescription());
		dcardPanelHand.add(dealerlabel1);

		// for each card in the hand
		for (VieCard card : hand.getCards()) {
			JLabel dealerCard = new JLabel(new ImageIcon(getCardImageFileLocation(card)));
			dcardPanelHand.add(dealerCard);
		}
	}

	@Override
	public void observeInitialBet(final GameInfo gameInfo, final int betAmount) {
		System.out.println("\nINITIAL BET: " + betAmount);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				playerMoney.setText("$" + gameInfo.getAvailableMoney());

				pcardPanel.removeAll();
				dcardPanel.removeAll();
				dcardPanel.add(dealerlabel);

				JLabel initialBet = new JLabel();
				initialBet.setText("Player Initial Bet: $" + betAmount);
				pcardPanel.add(initialBet);

				myFrame.pack();
				myFrame.setVisible(true);
			}
		});

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Print the state of the table when a player has a decision to make. The hand
	 * requiring a decision will be marked with "**"
	 */
	@Override
	public void observePlayerTurn(final GameInfo gameInfo, final ViePlayerHand currentHand, final VieCard dealerUpCard,
			final List<ViePlayerHand> allPlayerHands) {
		System.out.println("\nDecision to make:\n" + gameInfo.toString() + "\nDealer Up Card: " + dealerUpCard.toString()
				+ "\nPlayer Hands:\n" + prettyPrintPlayerHands(allPlayerHands) + "\n");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				playerMoney.setText("Player money: $" + gameInfo.getAvailableMoney());

				// remove the current player-card panel
				pcardPanel.removeAll();
				dcardPanel.removeAll();

				// for each hand, create a hand panel (containing x number of cards)
				for (ViePlayerHand hand : allPlayerHands) {
					placePlayerCards(hand);
				}

				myFrame.pack();
				myFrame.setVisible(true);

				dealerlabel.setText("Dealer: ?");
				dcardPanel.add(dealerlabel);
				JLabel dealerCard = new JLabel(new ImageIcon(getCardImageFileLocation(dealerUpCard)));
				dcardPanel.add(dealerCard);

				add(dcardPanel, BorderLayout.CENTER);
				add(pcardPanel, BorderLayout.SOUTH);

				myFrame.pack();
				myFrame.setVisible(true);
			}
		});

		doThreadWait(gameInfo);
	}

	private void placePlayerCards(ViePlayerHand hand) {
		JPanel pcardPanelHand = new JPanel();
		pcardPanelHand.setBackground(new Color(153, 255, 255));

		JLabel playerlabel1 = new JLabel();
		// pcardPanel.add(playerlabel1);
		playerlabel1.setText("Hand value: " + hand.getScoreDescription() + "\nBet: $" + hand.getBetPaid());
		pcardPanelHand.add(playerlabel1);

		// for each card in the hand
		for (VieCard card : hand.getCards()) {
			JLabel playercard0 = new JLabel(new ImageIcon(getCardImageFileLocation(card)));
			pcardPanelHand.add(playercard0);
		}

		pcardPanel.add(pcardPanelHand);
	}

	public URL getCardImageFileLocation(VieCard card) {
		Card card1 = card.toCard();
		String cardRank = card1.getRank();
		// handle cards 2-10
		if (!(cardRank.equals("ace") || cardRank.equals("king") || cardRank.equals("queen") || cardRank.equals("jack"))) {
			cardRank = "" + card.toCard().getMinScore();
		}
		String cardSuit = ("" + card1.getSuit());
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		URL fileLocation = JavaSwingObserver.class.getResource(IMAGEPATH + cardRank + cardSuit + ".GIF");
		return fileLocation;
	}

	@Override
	public void observeProgramError(final GameInfo gameInfo, final List<ViePlayerHand> allPlayerHands,
			final VieDealerHand vieDealerHand, final Exception e) {
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
