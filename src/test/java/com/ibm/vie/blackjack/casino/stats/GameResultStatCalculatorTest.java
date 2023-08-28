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
package com.ibm.vie.blackjack.casino.stats;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.Frequency;
import org.junit.Assert;
import org.junit.Test;

import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.casino.card.Rank;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.config.TableConfig;
import com.ibm.vie.blackjack.casino.crooked.CardManagerViewer;
import com.ibm.vie.blackjack.casino.crooked.CardSwitchCardChooser;
import com.ibm.vie.blackjack.casino.observer.GameResultObserver;
import com.ibm.vie.blackjack.casino.rules.OrdinaryBlackjackRules;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.CasinoRules;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;

public class GameResultStatCalculatorTest {
    private CasinoRules houseRules = new OrdinaryBlackjackRules();
	/**
	 * Strategy with complete control over how the game is played
	 * 
	 * @author ntl
	 *
	 */
	public class StrategyForTest implements PlayerStrategy, CardSwitchCardChooser {
		private final List<PlayerDecision> decisions = new LinkedList<PlayerDecision>();
		private final List<Rank> ranksToDraw = new LinkedList<>();
		private final List<Integer> bets = new LinkedList<>();

		private Iterator<PlayerDecision> nextDecision;
		private Iterator<Rank> nextCardToDraw;
		private Iterator<Integer> nextBet;

		public StrategyForTest() {

		}

		public void addBet(Integer... bets) {
			this.bets.addAll(Arrays.asList(bets));
		}

		public void addDecision(PlayerDecision... decisions) {
			this.decisions.addAll(Arrays.asList(decisions));
		}

		public void addRankToDeck(Rank... ranks) {
			this.ranksToDraw.addAll(Arrays.asList(ranks));
		}

		public void ready() {
			nextDecision = decisions.iterator();
			nextCardToDraw = ranksToDraw.iterator();
			nextBet = bets.iterator();
		}

		@Override
		public String getStudentName() {
			return "Thomas Watson";
		}

		@Override
		public int placeInitialBet(GameInfo gameInfo) {
			// TODO Auto-generated method stub
			return nextBet.next();
		}

		@Override
		public PlayerDecision decideHowToPlayHand(GameInfo gameInfo, PlayerHand currentHand, List<PlayerHand> playerHands,
				Card dealerUpCard) {
			return nextDecision.next();
		}

		@Override
		public boolean decideToWalkAway(GameInfo gameInfo, List<PlayerPayoutHand> playerHands, DealerHand dealerHand) {
			return !nextBet.hasNext();
		}

		@Override
		public VieCard chooseCard(CardManagerViewer viewer) {
			Rank requestedRank = nextCardToDraw.next();
			Map<Rank, Collection<VieCard>> availableCards = viewer.getRankToCardsMap();
			if (availableCards.get(requestedRank) != null && !availableCards.get(requestedRank).isEmpty()) {
				return availableCards.get(requestedRank).iterator().next();
			} else {
				viewer.shuffle();
				return viewer.getRankToCardsMap().get(requestedRank).iterator().next();
			}
		}

	}

	@Test
	public void when_player_blackjack_then_counts_correct() {
		StrategyForTest strategy = new StrategyForTest();
		TableConfig config = new TableConfig(1000, 1, 100, 1, 20, 999);
		Table blackjackTable = config.getTable(strategy, strategy, houseRules);
		GameResultObserver observer = new GameResultObserver(strategy.getStudentName(), blackjackTable.getRules());
		blackjackTable.addObserver(observer);
		// blackjackTable.addObserver(new PrintToConsoleObserver());
		strategy.addBet(1);
		strategy.addRankToDeck(Rank.ACE, Rank.TEN); // Player Hand
		strategy.addRankToDeck(Rank.TWO, Rank.TEN); // dealer hand
		// no decisions, player has blackjack

		strategy.addBet(2);
		strategy.addRankToDeck(Rank.ACE, Rank.EIGHT); // Player Hand
		strategy.addRankToDeck(Rank.TWO, Rank.TWO); // dealer hand
		strategy.addDecision(PlayerDecision.STAND);
		strategy.addRankToDeck(Rank.TEN); // dealer has 14
		strategy.addRankToDeck(Rank.FIVE); // dealer has 19
		strategy.ready();

		blackjackTable.playManyRoundsOfBlackJack();
		GameResult result = observer.getResult();
		Assert.assertTrue(!result.getGameEndedInError());
		GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), result);

		Assert.assertTrue(stats.getTotalNumberOfRoundsPlayed() == 2);
		Assert.assertTrue(stats.getTotalNumberOfHandsPlayed() == 2);
		Assert.assertTrue(stats.getFinalAvailableMoney() == 1002);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMax() == 0);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMin() == 1);

		// Overall stats
		Frequency freq = stats.getHandCategoryFrequency();
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_BLACKJACK) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PUSH) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		Assert.assertTrue(stats.getNumSpiltHandOpportunities() == 0);
		Assert.assertTrue(stats.getNumTimesSplit() == 0);

		// split stats
		Frequency freqSplit = stats.getFreqFromRoundsWithSplit();
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		// doubleDown stats
		Frequency freqDouble = stats.getDoubleDownHandCategoryFrequency();
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

	}

	@Test
	public void when_dealer_blackjack_then_counts_correct() {
		StrategyForTest strategy = new StrategyForTest();
		TableConfig config = new TableConfig(1000, 1, 100, 1, 20, 999);
		Table blackjackTable = config.getTable(strategy, strategy, houseRules);
		GameResultObserver observer = new GameResultObserver(strategy.getStudentName(), blackjackTable.getRules());
		blackjackTable.addObserver(observer);
		// blackjackTable.addObserver(new PrintToConsoleObserver());
		strategy.addBet(1);
		strategy.addRankToDeck(Rank.ACE, Rank.TEN); // Player Hand
		strategy.addRankToDeck(Rank.ACE, Rank.JACK); // dealer hand
		// no decisions, player has blackjack, dealer has blackjack

		strategy.addBet(20);
		strategy.addRankToDeck(Rank.ACE, Rank.EIGHT); // Player Hand
		strategy.addRankToDeck(Rank.ACE, Rank.QUEEN); // dealer hand
		// no decisions, dealer has blackjack
		strategy.ready();

		blackjackTable.playManyRoundsOfBlackJack();
		GameResult result = observer.getResult();
		GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), result);
		Assert.assertTrue(!result.getGameEndedInError());

		Assert.assertTrue(stats.getTotalNumberOfRoundsPlayed() == 2);
		Assert.assertTrue(stats.getTotalNumberOfHandsPlayed() == 2);
		Assert.assertTrue(stats.getFinalAvailableMoney() == 980);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMax() == 0);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMin() == 1);

		// Overall stats
		Frequency freq = stats.getHandCategoryFrequency();
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PUSH) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		Assert.assertTrue(stats.getNumSpiltHandOpportunities() == 0);
		Assert.assertTrue(stats.getNumTimesSplit() == 0);

		// split stats
		Frequency freqSplit = stats.getFreqFromRoundsWithSplit();
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		// doubleDown stats
		Frequency freqDouble = stats.getDoubleDownHandCategoryFrequency();
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

	}

	@Test
	public void when_blackjack_after_split_then_counts_correct() {
		StrategyForTest strategy = new StrategyForTest();
		TableConfig config = new TableConfig(1000, 1, 20, 1, 20, 999);
		Table blackjackTable = config.getTable(strategy, strategy, houseRules);
		GameResultObserver observer = new GameResultObserver(strategy.getStudentName(), blackjackTable.getRules());
		blackjackTable.addObserver(observer);
		// blackjackTable.addObserver(new PrintToConsoleObserver());
		strategy.addBet(1);
		strategy.addRankToDeck(Rank.ACE, Rank.ACE); // Player Hand
		strategy.addRankToDeck(Rank.FIVE, Rank.NINE); // dealer hand
		strategy.addDecision(PlayerDecision.SPLIT);
		strategy.addRankToDeck(Rank.ACE, Rank.TEN); // player hand = {ACE, ACE}, {blackjack}
		strategy.addDecision(PlayerDecision.STAND);
		strategy.addRankToDeck(Rank.FIVE); // dealer has 19

		strategy.addBet(20);
		strategy.addRankToDeck(Rank.ACE, Rank.EIGHT); // Player Hand
		strategy.addRankToDeck(Rank.ACE, Rank.QUEEN); // dealer hand
		// no decisions, dealer has blackjack
		strategy.ready();

		blackjackTable.playManyRoundsOfBlackJack();
		GameResult result = observer.getResult();
		GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), result);
		// System.out.println(stats);
		Assert.assertTrue(!result.getGameEndedInError());

		Assert.assertTrue(stats.getTotalNumberOfRoundsPlayed() == 2);
		Assert.assertTrue(stats.getTotalNumberOfHandsPlayed() == 3);
		Assert.assertTrue(stats.getFinalAvailableMoney() == 981);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMax() == 1);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMin() == 1);

		// Overall stats
		Frequency freq = stats.getHandCategoryFrequency();
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_BLACKJACK) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		Assert.assertTrue(stats.getNumSpiltHandOpportunities() == 2);
		Assert.assertTrue(stats.getNumTimesSplit() == 1);

		// split stats
		Frequency freqSplit = stats.getFreqFromRoundsWithSplit();
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_BLACKJACK) == 1);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 1);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		// doubleDown stats
		Frequency freqDouble = stats.getDoubleDownHandCategoryFrequency();
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);
	}

	@Test
	public void when_double_after_split_and_bust_then_counts_correct() {
		StrategyForTest strategy = new StrategyForTest();
		TableConfig config = new TableConfig(1000, 1, 50, 1, 20, 999);
		Table blackjackTable = config.getTable(strategy, strategy, houseRules);
		GameResultObserver observer = new GameResultObserver(strategy.getStudentName(), blackjackTable.getRules());
		blackjackTable.addObserver(observer);
		// blackjackTable.addObserver(new PrintToConsoleObserver());
		strategy.addBet(2);
		strategy.addRankToDeck(Rank.FIVE, Rank.FIVE); // Player Hand
		strategy.addRankToDeck(Rank.TWO, Rank.NINE); // dealer hand
		strategy.addDecision(PlayerDecision.SPLIT);
		strategy.addRankToDeck(Rank.FIVE, Rank.TEN); // {5,5}, {5,10}
		strategy.addDecision(PlayerDecision.SPLIT);
		strategy.addRankToDeck(Rank.JACK, Rank.KING); // {5, J}, {5, K}, {5, 10}
		strategy.addDecision(PlayerDecision.DOUBLE_DOWN);
		strategy.addRankToDeck(Rank.SIX); // {5,J,6}, {5, K}, {5,10}
		strategy.addDecision(PlayerDecision.DOUBLE_DOWN);
		strategy.addRankToDeck(Rank.NINE); // {5,J,6}, {5,K,9}, {5,10}
		strategy.addDecision(PlayerDecision.HIT);
		strategy.addRankToDeck(Rank.TWO);
		strategy.addDecision(PlayerDecision.STAND); // {5,J,6}, {5,K,9}, {5,10,2}
		// 21 +4 -4 17
		strategy.addRankToDeck(Rank.SIX); // dealer has 17

		strategy.addBet(20);
		strategy.addRankToDeck(Rank.ACE, Rank.EIGHT); // Player Hand
		strategy.addRankToDeck(Rank.ACE, Rank.QUEEN); // dealer hand
		// no decisions, dealer has blackjack
		strategy.ready();

		blackjackTable.playManyRoundsOfBlackJack();
		GameResult result = observer.getResult();
		GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), result);
		// System.out.println(stats);
		Assert.assertTrue(!result.getGameEndedInError());

		Assert.assertTrue(stats.getTotalNumberOfRoundsPlayed() == 2);
		Assert.assertTrue(stats.getTotalNumberOfHandsPlayed() == 4);
		Assert.assertTrue(stats.getFinalAvailableMoney() == 980);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMax() == 0);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMin() == 0);

		// Overall stats
		Frequency freq = stats.getHandCategoryFrequency();
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PUSH) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 1);

		Assert.assertTrue(stats.getNumSpiltHandOpportunities() == 2);
		Assert.assertTrue(stats.getNumTimesSplit() == 2);

		// split stats
		Frequency freqSplit = stats.getFreqFromRoundsWithSplit();
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PUSH) == 1);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 1);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 1);

		// doubleDown stats
		Frequency freqDouble = stats.getDoubleDownHandCategoryFrequency();
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 1);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 1);
	}

	@Test
	public void when_error_in_initial_bet_then_game_ends_in_error() {
		StrategyForTest strategy = new StrategyForTest();
		TableConfig config = new TableConfig(1000, 1, 10, 1, 20, 999);
		
		Table blackjackTable = config.getTable(strategy, strategy, houseRules);
		GameResultObserver observer = new GameResultObserver(strategy.getStudentName(), blackjackTable.getRules());
		
		blackjackTable.addObserver(observer);
		// blackjackTable.addObserver(new PrintToConsoleObserver());
		strategy.addBet(2);
		strategy.addRankToDeck(Rank.FIVE, Rank.FIVE); // Player Hand
		strategy.addRankToDeck(Rank.TWO, Rank.NINE); // dealer hand
		strategy.addDecision(PlayerDecision.SPLIT);
		strategy.addRankToDeck(Rank.FIVE, Rank.TEN); // {5,5}, {5,10}
		strategy.addDecision(PlayerDecision.SPLIT);
		strategy.addRankToDeck(Rank.JACK, Rank.KING); // {5, J}, {5, K}, {5, 10}
		strategy.addDecision(PlayerDecision.DOUBLE_DOWN);
		strategy.addRankToDeck(Rank.SIX); // {5,J,6}, {5, K}, {5,10}
		strategy.addDecision(PlayerDecision.DOUBLE_DOWN);
		strategy.addRankToDeck(Rank.NINE); // {5,J,6}, {5,K,9}, {5,10}
		strategy.addDecision(PlayerDecision.HIT);
		strategy.addRankToDeck(Rank.TWO);
		strategy.addDecision(PlayerDecision.STAND); // {5,J,6}, {5,K,9}, {5,10,2}
		// 21 +4 -4 17
		strategy.addRankToDeck(Rank.SIX); // dealer has 17

		strategy.addBet(20); // illegal initial bet
		strategy.ready();

		blackjackTable.playManyRoundsOfBlackJack();
		GameResult result = observer.getResult();
		GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), result);
		
		// System.out.println(stats);
		Assert.assertTrue(result.getGameEndedInError());
		Assert.assertTrue(result.getGameEndingException() != null);

		Assert.assertTrue(stats.getTotalNumberOfRoundsPlayed() == 1);
		Assert.assertTrue(stats.getTotalNumberOfHandsPlayed() == 3);
		Assert.assertTrue(stats.getFinalAvailableMoney() == 1000);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMax() == 0);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMin() == 0);

		// Overall stats
		Frequency freq = stats.getHandCategoryFrequency();
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PUSH) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 1);

		Assert.assertTrue(stats.getNumSpiltHandOpportunities() == 2);
		Assert.assertTrue(stats.getNumTimesSplit() == 2);

		// split stats
		Frequency freqSplit = stats.getFreqFromRoundsWithSplit();
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PUSH) == 1);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 1);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 1);

		// doubleDown stats
		Frequency freqDouble = stats.getDoubleDownHandCategoryFrequency();
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 1);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 1);
	}

	@Test
	public void when_error_on_split_then_game_ends_in_error() {
		StrategyForTest strategy = new StrategyForTest();
		TableConfig config = new TableConfig(1000, 1, 10, 1, 20, 999);
		Table blackjackTable = config.getTable(strategy, strategy, houseRules);
		GameResultObserver observer = new GameResultObserver(strategy.getStudentName(), blackjackTable.getRules());
		blackjackTable.addObserver(observer);
		// blackjackTable.addObserver(new PrintToConsoleObserver());
		strategy.addBet(2);
		strategy.addRankToDeck(Rank.FIVE, Rank.SIX); // Player Hand
		strategy.addRankToDeck(Rank.TWO, Rank.NINE); // dealer hand
		strategy.addDecision(PlayerDecision.SPLIT);
		strategy.ready();

		blackjackTable.playManyRoundsOfBlackJack();
		GameResult result = observer.getResult();
		GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), result);
		// System.out.println(stats);
		Assert.assertTrue(result.getGameEndedInError());
		Assert.assertTrue(result.getGameEndingException() != null);

		Assert.assertTrue(stats.getTotalNumberOfRoundsPlayed() == 0);
		Assert.assertTrue(stats.getTotalNumberOfHandsPlayed() == 0);
		Assert.assertTrue(stats.getFinalAvailableMoney() == 998);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMax() == 0);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMin() == 0);

		// Overall stats
		Frequency freq = stats.getHandCategoryFrequency();
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		Assert.assertTrue(stats.getNumSpiltHandOpportunities() == 0);
		Assert.assertTrue(stats.getNumTimesSplit() == 0);

		// split stats
		Frequency freqSplit = stats.getFreqFromRoundsWithSplit();
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		// doubleDown stats
		Frequency freqDouble = stats.getDoubleDownHandCategoryFrequency();
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);
	}

	@Test
	public void when_error_on_double_then_game_ends_in_error() {
		StrategyForTest strategy = new StrategyForTest();
		TableConfig config = new TableConfig(1000, 1, 1000, 1, 20, 999);
		Table blackjackTable = config.getTable(strategy, strategy, houseRules);
		GameResultObserver observer = new GameResultObserver(strategy.getStudentName(), blackjackTable.getRules());
		blackjackTable.addObserver(observer);
		// blackjackTable.addObserver(new PrintToConsoleObserver());
		strategy.addBet(2);
		strategy.addRankToDeck(Rank.FIVE, Rank.SIX); // Player Hand
		strategy.addRankToDeck(Rank.ACE, Rank.JACK); // dealer hand
		// no decision, dealer has blackjack

		strategy.addBet(900);
		strategy.addRankToDeck(Rank.FIVE, Rank.SIX); // Player Hand
		strategy.addRankToDeck(Rank.TEN, Rank.JACK); // dealer hand
		strategy.addDecision(PlayerDecision.HIT);
		strategy.addRankToDeck(Rank.TWO);
		strategy.addDecision(PlayerDecision.DOUBLE_DOWN); // illegal
		strategy.ready();

		blackjackTable.playManyRoundsOfBlackJack();
		GameResult result = observer.getResult();
		GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), result);
		// System.out.println(stats);
		Assert.assertTrue(result.getGameEndedInError());
		Assert.assertTrue(result.getGameEndingException() != null);

		Assert.assertTrue(stats.getTotalNumberOfRoundsPlayed() == 1);
		Assert.assertTrue(stats.getTotalNumberOfHandsPlayed() == 1);
		Assert.assertTrue(stats.getFinalAvailableMoney() == 98);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMax() == 0);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMin() == 0);

		// Overall stats
		Frequency freq = stats.getHandCategoryFrequency();
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		Assert.assertTrue(stats.getNumSpiltHandOpportunities() == 0);
		Assert.assertTrue(stats.getNumTimesSplit() == 0);

		// split stats
		Frequency freqSplit = stats.getFreqFromRoundsWithSplit();
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		// doubleDown stats
		Frequency freqDouble = stats.getDoubleDownHandCategoryFrequency();
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);
	}

	@Test
	public void when_out_of_money_then_game_ends() {
		StrategyForTest strategy = new StrategyForTest();
		TableConfig config = new TableConfig(1000, 1, 1000, 1, 20, 999);
		Table blackjackTable = config.getTable(strategy, strategy, houseRules);
		GameResultObserver observer = new GameResultObserver(strategy.getStudentName(), blackjackTable.getRules());
		blackjackTable.addObserver(observer);
		// blackjackTable.addObserver(new PrintToConsoleObserver());
		strategy.addBet(998);
		strategy.addRankToDeck(Rank.FIVE, Rank.SIX); // Player Hand
		strategy.addRankToDeck(Rank.ACE, Rank.JACK); // dealer hand
		// no decision, dealer has blackjack

		strategy.addBet(1);
		strategy.addRankToDeck(Rank.FIVE, Rank.SIX); // Player Hand
		strategy.addRankToDeck(Rank.TEN, Rank.JACK); // dealer hand
		strategy.addDecision(PlayerDecision.DOUBLE_DOWN);
		strategy.addRankToDeck(Rank.TWO);
		strategy.ready();

		blackjackTable.playManyRoundsOfBlackJack();
		GameResult result = observer.getResult();
		GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), result);
		// System.out.println(stats);
		Assert.assertTrue(!result.getGameEndedInError());
		Assert.assertTrue(result.getGameEndingException() == null);

		Assert.assertTrue(stats.getTotalNumberOfRoundsPlayed() == 2);
		Assert.assertTrue(stats.getTotalNumberOfHandsPlayed() == 2);
		Assert.assertTrue(stats.getFinalAvailableMoney() == 0);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMax() == 0);
		Assert.assertTrue(stats.getNumTimesInitialBetIsMin() == 1);

		// Overall stats
		Frequency freq = stats.getHandCategoryFrequency();
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 1);
		Assert.assertTrue(freq.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		Assert.assertTrue(stats.getNumSpiltHandOpportunities() == 0);
		Assert.assertTrue(stats.getNumTimesSplit() == 0);

		// split stats
		Frequency freqSplit = stats.getFreqFromRoundsWithSplit();
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 0);
		Assert.assertTrue(freqSplit.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);

		// doubleDown stats
		Frequency freqDouble = stats.getDoubleDownHandCategoryFrequency();
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PUSH) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_WIN_NO_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_DEALER_BLACKJACK) == 0);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_NO_BUST) == 1);
		Assert.assertTrue(freqDouble.getCount(HandResultCategory.PLAYER_LOSE_BUST) == 0);
	}

}
