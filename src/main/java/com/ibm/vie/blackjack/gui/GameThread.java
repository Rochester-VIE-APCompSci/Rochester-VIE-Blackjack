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

import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.casino.config.TableConfig;
import com.ibm.vie.blackjack.casino.crooked.CardSwitchCardManager;
import com.ibm.vie.blackjack.casino.observer.GameResultObserver;
import com.ibm.vie.blackjack.casino.observer.TableObserver;
import com.ibm.vie.blackjack.casino.stats.GameResultStatCalculator;
import com.ibm.vie.blackjack.player.CasinoRules;
import com.ibm.vie.blackjack.player.PlayerStrategy;
import com.ibm.vie.blackjack.player.TableRules;

public class GameThread implements Runnable {

	public final TableConfig config;
	public final Table table;
	private GameResultObserver statObserver;
	private final PlayerStrategy strategy;
	private Blackjack blackjack;
	private boolean normalGame = false;
	private boolean testGame = false;

	//normal game
	public GameThread(Blackjack blackjack, Class<? extends PlayerStrategy> clazz, NormalGameObserver observer, int initialMoney, int minBet, int maxBet, int numDecks, int numRounds, int deckSeed, CasinoRules casinoRules) throws InstantiationException, IllegalAccessException {
		strategy = clazz.newInstance();
		this.config = new TableConfig(initialMoney, minBet, maxBet, numDecks, numRounds, deckSeed);
		this.table = config.getTable(strategy, casinoRules);
		this.blackjack = blackjack;
		table.addObserver(observer);
//		table.addObserver(new PrintToConsoleObserver());
		this.normalGame = true;
	}
	
	//single stats game
	public GameThread(Blackjack blackjack, Class<? extends PlayerStrategy> clazz, ScoreSingleGraphObserverGui observer, int initialMoney, int minBet, int maxBet, int numDecks, int numRounds, int deckSeed, CasinoRules casinoRules) throws InstantiationException, IllegalAccessException {
		strategy = clazz.newInstance();
		this.config = new TableConfig(initialMoney, minBet, maxBet, numDecks, numRounds, deckSeed);
		this.table = config.getTable(strategy, casinoRules);
		table.addObserver(observer);
//		table.addObserver(new PrintToConsoleObserver());
		this.blackjack = blackjack;
	}

	//for multi-stats game
	public GameThread(Blackjack blackjack, Class<? extends PlayerStrategy> clazz, TableObserver observer, int initialMoney, int minBet, int maxBet, int numDecks, int numRounds, int deckSeed, String name, CasinoRules casinoRules) throws InstantiationException, IllegalAccessException {
		strategy = clazz.newInstance();
		this.config = new TableConfig(name, initialMoney, minBet, maxBet, numDecks, numRounds, deckSeed);
		this.table = config.getTable(strategy, casinoRules);
		table.addObserver(observer);
		this.blackjack = blackjack;
	}

	// for test game
	public GameThread(Blackjack blackjack, Class<? extends PlayerStrategy> clazz, TableObserver testGameObserver, int initialMoney, int minBet, int maxBet, int numDecks, int numRounds, int deckSeed, ScreenTestGame screenTestGame) throws InstantiationException, IllegalAccessException {
		strategy = clazz.newInstance();
		final TableRules rules = new TableRules(initialMoney, minBet, maxBet, numRounds, numDecks);
		final CrookedObserverGui observer = new CrookedObserverGui(screenTestGame, blackjack);
		this.config = null;

		final CardSwitchCardManager decks = new CardSwitchCardManager(rules, deckSeed, observer);

		this.table = new Table(strategy, decks, rules);
		table.addObserver(observer);
		table.addObserver(testGameObserver);
		this.testGame = true;
		this.blackjack = blackjack;
	}

	public GameThread(Blackjack blackjack, Class<? extends PlayerStrategy> clazz, int initialMoney, int minBet, int maxBet, int numDecks, int numRounds, int deckSeed, CasinoRules houseRules) throws InstantiationException, IllegalAccessException {
		strategy = clazz.newInstance();
		this.config = new TableConfig(initialMoney, minBet, maxBet, numDecks, numRounds, deckSeed);
		this.table = config.getTable(strategy, houseRules);
		this.blackjack = blackjack;
	}

	//	public GameThread() {
	//		this.config = new TableConfig(5000000, 1, 100, 1, 1000000, 5);
	//    this.table = config.getTable(new TestBlackJackSplitOrDouble());
	//	}

	public void playGame() {
		statObserver = new GameResultObserver(strategy.getStudentName(), table.getRules());
		table.addObserver(statObserver);

		table.playManyRoundsOfBlackJack();
	}

	public GameResultStatCalculator getStats() {
		GameResultStatCalculator stats = new GameResultStatCalculator(config.getName(), statObserver.getResult());
		return stats;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		playGame();
	}
	
	protected void endEarly() {
		table.signalEarlyTermination(true);
		if (normalGame) {
			synchronized (blackjack.normalGame.threadWaitObject) {
				blackjack.normalGame.threadWaitObject.notify();							
			}
		}
		else if (testGame) {
			synchronized (blackjack.testGame.threadWaitObject) {
				blackjack.testGame.threadWaitObject.notify();	
			}
			synchronized (blackjack.testGame.threadWaitObjectCrooked) {
				blackjack.testGame.threadWaitObjectCrooked.notify();
			}
		}
	}

}
