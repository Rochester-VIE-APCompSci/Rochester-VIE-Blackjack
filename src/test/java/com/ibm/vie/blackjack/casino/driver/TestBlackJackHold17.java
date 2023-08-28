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
package com.ibm.vie.blackjack.casino.driver;

import java.util.List;

import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.casino.observer.JavaSwingObserver;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;

public class TestBlackJackHold17 implements PlayerStrategy {

	/**
	 * Always uses the minimum bet
	 *
	 * @see PlayerStrategy#placeInitialBet(GameInfo)
	 */
	@Override
	public int placeInitialBet(final GameInfo gameInfo) {
		return gameInfo.getMinBet();
	}

	/**
	 *
	 * Stands on 17, even if the 17 involves an ace that is counted as 11 (soft)
	 *
	 * @see PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, Card)
	 */
	@Override
	public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo, final PlayerHand currentHand,
			final List<PlayerHand> playerHands, final Card dealerUpCard) {

		PlayerDecision decision;

		if (currentHand.getPointScore() >= 17) {
			decision = PlayerDecision.STAND;
		} else {
			decision = PlayerDecision.HIT;
		}

		return decision;
	}

	/**
	 * Walks away after 20 rounds
	 *
	 * @see PlayerStrategy#decideToWalkAway(GameInfo, List, DealerHand)
	 */
	@Override
	public boolean decideToWalkAway(final GameInfo gameInfo, final List<PlayerPayoutHand> playerHands,
			final DealerHand dealerHand) {

//		 /**
//		  * Play at most 20 rounds
//		  */
//		 if (gameInfo.getRoundNumber() >= 20) {
//		 	return true;
//		 } else {
//		 	return false;
//		 }
		 return false;
	}

	/**
	 * Main driver to play blackjack
	 *
	 * @param args
	 *          - ignored
	 */
	public static void main(final String[] args) {

		final Table table = new Table(new TestBlackJackHold17());
		// table.addObserver(new PrintToConsoleObserver());
		table.addObserver(new JavaSwingObserver());
		table.playManyRoundsOfBlackJack();
		System.out.println("Finished with " + table.getAvailableMoney() + " dollars");
	}

	@Override
	public String getStudentName() {
		return "Han Solo";
	}

}
