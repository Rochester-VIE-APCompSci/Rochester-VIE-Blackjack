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

import java.awt.Color;
import java.awt.GridLayout;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.VieHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;
import com.ibm.vie.blackjack.player.Card;

public class GuiHand extends JPanel {

	protected int totalWidth;
	protected int totalHeight;

	public GuiHand(ViePlayerHand hand) {
		//grid layout, cards on top, value & bet on bottom
		this.setLayout(new GridLayout(2, 1, 0, 0));
		JPanel cardsPanel = new JPanel();
		cardsPanel.setLayout(new GridLayout(1, hand.getCards().size(), 10, 0));
		for (VieCard card : hand.getCards()) {
			JLabel playerCard = getCardLabel(card);
			cardsPanel.add(playerCard);
		}
		JLabel playerLabel = new JLabel();

		playerLabel.setText("<html>" + "Hand value: " + hand.getScoreDescription() + "<br/>" + "\nBet: $" + hand.getBetPaid() + "</html>");

		playerLabel.setHorizontalAlignment(JLabel.CENTER);
//		cardsPanel.setBackground(Color.MAGENTA);
		totalWidth = (hand.getCards().size() * 73) + ((hand.getCards().size() - 1) * 10);
		totalHeight = 97 * 2;

		this.add(cardsPanel);
		this.add(playerLabel);
		this.setBackground(Color.pink);
	}

	public GuiHand(List<VieCard> playerCards) {
		//grid layout, cards on top, value & bet on bottom
		this.setLayout(new GridLayout(2, 1, 0, 0));
		JPanel cardsPanel = new JPanel();
		cardsPanel.setLayout(new GridLayout(1, playerCards.size(), 10, 0));
		for (VieCard card : playerCards) {
			JLabel playerCard = getCardLabel(card);
			cardsPanel.add(playerCard);
		}

		if (playerCards.size() == 1) {
			cardsPanel.add(Blackjack.getBackCardLabel());
			totalWidth = (2 * 73) + 10;
			totalHeight = 97 * 2;
		}
		else {
			totalWidth = (playerCards.size() * 73) + ((playerCards.size() - 1) * 10);
			totalHeight = 97 * 2;
		}
		JLabel playerLabel = new JLabel();
		playerLabel.setText("<html>" + "Hand value: " + "<br/>" + (new VieHand(playerCards)).getScoreDescription() + "<br/><br/>" + "\nBet: $" + "-" + "</html>");

		playerLabel.setHorizontalAlignment(JLabel.CENTER);


		this.add(cardsPanel);
		this.add(playerLabel);
		this.setBackground(Color.pink);
	}
	
	public GuiHand(VieDealerHand hand) {
		//grid layout, cards on top, value & bet on bottom
		this.setLayout(new GridLayout(2, 1, 0, 0));
		JPanel cardsPanel = new JPanel();

		cardsPanel.setLayout(new GridLayout(1, hand.getCards().size(), 10, 0));
		for (VieCard card : hand.getCards()) {
			JLabel playerCard = getCardLabel(card);
			cardsPanel.add(playerCard);
		}

		JLabel playerLabel = new JLabel();
		playerLabel.setText("Hand value: " + hand.getScoreDescription());

		playerLabel.setHorizontalAlignment(JLabel.CENTER);

		totalWidth = (hand.getCards().size() * 73) + ((hand.getCards().size() - 1) * 10);
		totalHeight = 97 * 2;
		// this.setBackground(Color.CYAN);
		this.add(cardsPanel);
		this.add(playerLabel);
	}

	public GuiHand(VieCard dealerUpCard) {
		//grid layout, cards on top, value & bet on bottom
		this.setLayout(new GridLayout(2, 1, 0, 0));
		JPanel cardsPanel = new JPanel();

		cardsPanel.setLayout(new GridLayout(1, 2, 10, 0));
		JLabel dealerCard = getCardLabel(dealerUpCard);
		cardsPanel.add(dealerCard);
		cardsPanel.add(Blackjack.getBackCardLabel());
		JLabel playerLabel = new JLabel();
		playerLabel.setText("Hand value: " + dealerUpCard.getRank().getRankMaxScore());
		playerLabel.setHorizontalAlignment(JLabel.CENTER);

		totalWidth = (2 * 73) + 10;
		totalHeight = 97 * 2;
		this.add(cardsPanel);
		this.add(playerLabel);
		// this.setBackground(Color.CYAN);
	}

	protected void redraw() {
		revalidate();
		validate();
		repaint();
	}

	private JLabel getCardLabel(VieCard card) {
		Card card1 = card.toCard();
		String cardRank = card1.getRank();
		// handle cards 2-10
		if (!(cardRank.equals("ace") || cardRank.equals("king") || cardRank.equals("queen") || cardRank.equals("jack"))) {
			cardRank = "" + card.toCard().getMinScore();
		}
		String cardSuit = ("" + card1.getSuit());
		
		URL fileLocation;
		if (Blackjack.eggStra) {
			fileLocation = getClass().getResource(Blackjack.getCardImageFileLocationInverse(cardRank, cardSuit));
		}
		else {
			fileLocation = getClass().getResource(Blackjack.getCardImageFileLocation(cardRank, cardSuit));
		}
		JLabel cardLabel = new JLabel(new ImageIcon(fileLocation));
		return cardLabel;
	}

}
