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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.plaf.basic.BasicArrowButton;

public class ScreenTestGame extends Screen {

	protected GameButton continueButton;
	private HandSet playerCardsGUI;
	private GuiHand dealerCardsGUI;
	private Blackjack blackjack;

	protected boolean roundHasEnded;

	protected JPanel menuButtons;
	protected JPanel continueButtonPanel;
	protected JPanel stepArrowPanel;
	protected JPanel dealerCards;
	protected JPanel playerCards;
	protected JPanel cardSelection;
	protected JPanel playerTotalMoney;
	protected JPanel roundNumber;
	protected JPanel gameStatusPanel;
	protected JLabel gameStatusLabel;
	protected Object threadWaitObject = new Object();
	protected Object threadWaitObjectCrooked = null;

	public ScreenTestGame(CardLayout screenManager, JPanel screens, Blackjack blackjack) {
		super(screenManager, screens);
		this.blackjack = blackjack;
		roundHasEnded = false;
		init();
	}

	private void clearScreen() {
		this.removeAll();

		// playerCardsGUI = null;
		// dealerCardsGUI = null;

		init();
	}

	private void init() {
		playerCards = new JPanel();
		playerTotalMoney = new JPanel();
		roundNumber = new JPanel();
		createLayout();
	}

	// public void update(GameInfo gameInfo, ViePlayerHand currentHand, VieCard
	// dealerUpCard, List<ViePlayerHand> allPlayerHands) {
	//// GameThread game = blackjack.gameThreads.poll();
	//// GameResultStatCalculator stats = game.getStats();
	//
	//// System.out.println("Stats:\n" + stats.toString());
	//
	//// finalMoney = stats.getFinalAvailableMoney();
	// this.removeAll();
	//
	// this.setLayout(null);
	//
	// this.add(createCardSelection());
	// this.add(createMenuButtons());
	// this.add(createContinueButton());
	// this.add(createBlankDealerCards());
	//
	//
	// this.redraw();
	// }

	public void update() {
		this.removeAll();

		this.setLayout(null);

		this.add(playerCards);
		this.add(menuButtons);
		this.add(continueButtonPanel);
		this.add(stepArrowPanel);
		this.add(dealerCards);
		this.add(cardSelection);
		this.add(playerTotalMoney);
		this.add(roundNumber);
		this.add(gameStatusPanel);

		this.redraw();
	}

	public void updateGameStatus(String status) {
		this.gameStatusLabel.setText(status);
	}

	public void moveStepArrow(String location) {
		switch (location) {
		case "p1": // Player's first card
			stepArrowPanel = createStepArrow("down", 348, 264);
			break;
		case "p2": // Player's second card
			stepArrowPanel = createStepArrow("down", 428, 264);
			break;
		case "d1": // Dealer's first card
			stepArrowPanel = createStepArrow("down", 348, 10);
			break;
		case "d2": // Dealer's second card
			stepArrowPanel = createStepArrow("down", 428, 10);
			break;
		case "continue": // Continue button
			stepArrowPanel = createStepArrow("down", 120, 472);
			break;
		case "draw": // Draw card
			stepArrowPanel = createStepArrow("left", 820, 560);
			break;
		default:
			stepArrowPanel.setVisible(false);
			break;
		}
	}

	private void createLayout() {
		this.setLayout(null);

		try {
			menuButtons = createMenuButtons();
			continueButtonPanel = createContinueButton();
			dealerCards = createBlankDealerCards();
			cardSelection = createCardSelection();
			playerCards = createBlankPlayerCards();
			gameStatusPanel = createGameStatus();
			this.add(cardSelection);
			this.add(menuButtons);
			this.add(continueButtonPanel);
			this.add(dealerCards);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// any/all menu buttons primarily to return to the main menu.
	private JPanel createMenuButtons() {

		JPanel menuGamePanel = new JPanel();
		menuGamePanel.setLayout(new GridLayout(1, 1, 0, 0));
		menuGamePanel.setBounds(50, 25, 220, 40);

		// TODO - GameButton needs an image?
		GameButton menuButton = new GameButton("<html>&#x2B05;  Return to Main Menu</html>", 730, 100);
		menuButton.addActionListener(super.getScreenAction("Menu"));
		menuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameThread currentThread = blackjack.gameThreads.poll();
				blackjack.uiFinished = true;
				currentThread.endEarly();
				clearScreen();
			}
		});

		menuButton.setUI(new MetalButtonUI());

		menuGamePanel.add(menuButton);
		return menuGamePanel;
	}

	private JPanel createContinueButton() {
		JPanel continueGamePanel = new JPanel();
		continueGamePanel.setLayout(new GridLayout(1, 1, 0, 0));
		continueGamePanel.setBounds(75, 510, 120, 120);

		continueButton = new GameButton("Continue", 0, 0);
		continueButton.setEnabled(false);

		continueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (threadWaitObject) {
					threadWaitObject.notify(); // Wake up game thread
					continueButton.setEnabled(false);
					if (roundHasEnded) {
						clearScreen();
						roundHasEnded = false;
					}
				}
			}
		});

		continueButton.setUI(new MetalButtonUI());

		continueGamePanel.add(continueButton);
		return continueGamePanel;
	}

	// section showing the Dealer's Cards.
	private JPanel createBlankDealerCards() throws IOException {
		JPanel dealerCardsPanel = new JPanel();
		dealerCardsPanel.setLayout(new GridLayout(1, 2, 10, 10));
		dealerCardsPanel.setBounds(330, 50, 156, 97);

		dealerCardsPanel.add(Blackjack.getBackCardLabel());
		dealerCardsPanel.add(Blackjack.getBackCardLabel());

		return dealerCardsPanel;
	}

	// section showing the Players Cards.
	private JPanel createBlankPlayerCards() throws IOException {
		JPanel playerCardsPanel = new JPanel();
		playerCardsPanel.setLayout(new GridLayout(1, 2, 10, 10));
		playerCardsPanel.setBounds(330, 300, 156, 97);

		playerCardsPanel.add(Blackjack.getBackCardLabel());
		playerCardsPanel.add(Blackjack.getBackCardLabel());

		return playerCardsPanel;
	}

	private JPanel createCardSelection() throws IOException {
		// 6 column, 2 rows for 12 cards
		JPanel cardSelectionPanel = new JPanel();
		cardSelectionPanel.setLayout(new GridLayout(2, 7, 10, 10));
		cardSelectionPanel.setBounds(230, 500, 585, 230);
		cardSelectionPanel.setBorder(BorderFactory.createTitledBorder("Available cards"));

		GameButton cardButtonAce = getCardButton("ace", "diamonds");
		cardSelectionPanel.add(cardButtonAce);

		for (int i = 2; i < 11; i++) {
			GameButton cardButton = getCardButton(i + "", "diamonds");
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

	// extra: deck
	private JPanel deckPanel() {
		return null;
	}

	// Panel that contains game messages
	private JPanel createGameStatus() {
		gameStatusLabel = new JLabel();

		JPanel gameStatusPanel = new JPanel();
		gameStatusPanel.setLayout(new BorderLayout());
		gameStatusPanel.setBounds(50, 100, 220, 320);
		gameStatusPanel.add(gameStatusLabel);
		gameStatusPanel.setBorder(BorderFactory.createTitledBorder("Information"));

		return gameStatusPanel;
	}

	// Panel that contains the arrow that moves each step
	private JPanel createStepArrow(String direction, int x, int y) {
		String content = "";
		switch (direction) {
		case "up":
			content = "▲";
			break;
		case "down":
			content = "▼";
			break;
		case "left":
			content = "◄";
			break;
		}

		JPanel stepArrowPanel = new JPanel();
		stepArrowPanel.setBounds(x, y, 40, 40);

		JLabel stepArrow = new JLabel("<html>" + content + "</html>");
		stepArrow.setFont(new Font("Arial", Font.BOLD, 30));
		stepArrow.setForeground(Color.RED);
		stepArrowPanel.add(stepArrow);
		return stepArrowPanel;
	}

	private GameButton getCardButton(String cardRank, String cardSuit) throws IOException {
		URL imageFile = getClass().getResource(Blackjack.getCardImageFileLocation(cardRank, cardSuit));
		BufferedImage in;
		in = ImageIO.read(imageFile);
		GameButton card = new GameButton(in, 1, 1);
		return card;
	}
}
