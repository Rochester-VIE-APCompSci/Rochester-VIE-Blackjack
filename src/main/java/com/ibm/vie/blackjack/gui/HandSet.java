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

import java.util.List;

import javax.swing.JPanel;

import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHand;

public class HandSet extends JPanel{

	List<GuiHand> hands;
	protected int totalWidth;
	protected int totalHeight;

	// TODO do some sort of organization/wrapping for 3+ hands
	public HandSet(List<ViePlayerHand> allPlayerHands) {
		totalWidth = 0;
		totalHeight = 0;
		for (ViePlayerHand vieHand : allPlayerHands) {
			GuiHand hand = new GuiHand(vieHand);
			totalWidth += hand.totalWidth;
			totalWidth += 20;
			totalHeight = hand.totalHeight;
			this.add(hand);
		}
		totalWidth -= 20;
	}

	public HandSet(List<VieCard> playerCards, boolean x) {
		GuiHand playerHand = new GuiHand(playerCards);
		totalWidth = playerHand.totalWidth;
		totalHeight = playerHand.totalHeight;
		this.add(playerHand);
	}

	// TODO do some sort of organization/wrapping for 3+ hands
	public HandSet(VieDealerHand hand) {
		GuiHand dealerHand = new GuiHand(hand);
		totalWidth = dealerHand.totalWidth;
		totalHeight = dealerHand.totalHeight;
		this.add(dealerHand);
	}

	public HandSet(VieCard dealerUpCard) {
		GuiHand dealerHand = new GuiHand(dealerUpCard);
		totalWidth = dealerHand.totalWidth;
		totalHeight = dealerHand.totalHeight;
		this.add(dealerHand);
	}

}
