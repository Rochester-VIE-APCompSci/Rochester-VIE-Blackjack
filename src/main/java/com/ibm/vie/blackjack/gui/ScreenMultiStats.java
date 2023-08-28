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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.metal.MetalButtonUI;

public class ScreenMultiStats extends Screen {

	double finalMoney = 123.32;
	private Blackjack blackjack;
//	private GameResultStatCalculator stats;
	private int totalEarnings;
	private String fullStats;
	private String briefStats;
	private int numTablesPlayed;
	private String errorsStatus;
	private int numErrors;
	protected GameButton viewGraphButton;
	
public ScreenMultiStats(CardLayout screenManager, JPanel screens, Blackjack blackjack) {
	super(screenManager, screens);
	this.blackjack = blackjack;
	init();
}

private void init() {

	createLayout();
}

@Override
public synchronized void refresh() {
	if (blackjack.gameThreads.size() > 0) {
		numTablesPlayed = blackjack.gameThreads.size();
		totalEarnings = 0;
		numErrors = 0;
		fullStats = "";
		briefStats = "";
		errorsStatus = "";
		for (GameThread gameThread : blackjack.gameThreads) {
			if (gameThread.getStats().gameEndedInError()) {
				numErrors++;
				errorsStatus += "\"" + gameThread.config.getName() + "\" ended in error\n";
			}
			if (gameThread.getStats().getTotalEarnings() < 0) {
				briefStats += "\"" + gameThread.config.getName() + "\" profit: -$" + Math.abs(gameThread.getStats().getTotalEarnings()) + "\n";
			}
			else {
				briefStats += "\"" + gameThread.config.getName() + "\" profit: $" + gameThread.getStats().getTotalEarnings() + "\n";
			}
			totalEarnings += gameThread.getStats().getTotalEarnings();
			fullStats += "\n\n";
			fullStats += "START OF STATS FOR TABLE: " + gameThread.config.getName() + "\n";
			fullStats += "---------------------------------------------------------------------------\n\n";
			fullStats += gameThread.getStats().toString();
			fullStats += "\n END OF STATS FOR TABLE: " + gameThread.config.getName() + "\n\n";
			fullStats += "##########################################################################################################\n";
			fullStats += "##########################################################################################################\n";
			fullStats += "##########################################################################################################\n\n\n";
		}
		blackjack.gameThreads.clear();
	}
		
	this.removeAll();
	
	this.setLayout(null);
	this.add(createStatsInfo());
	this.add(createMenuButtons());
	this.add(getViewGraphButton());

	this.redraw();
}

private void createLayout() {

	this.setLayout(null);

	this.add(getViewGraphButton());
	this.add(createMenuButtons());

}

private JScrollPane createStatsInfo() {
	JPanel baseStatsPanel = new JPanel();
	baseStatsPanel.setBounds(100, 140, 500, 500);
	
	JTextArea statsText = new JTextArea();
	statsText.setFont(new Font("monospaced", Font.PLAIN, 12));
	String initialText;
	if (totalEarnings < 0) {
		initialText = "Total Profit over " + numTablesPlayed + " played games: -$" + Math.abs(totalEarnings) + "\n" + "Individual Table Stats: \n";
	}
	else {
		initialText = "Total Profit over " + numTablesPlayed + " played games: $" + totalEarnings + "\n\n" + "Individual Table Stats: \n";
	}
	
	if (numErrors == 0 ) {
		errorsStatus = "None of the tables encountered an error while running!\n";
	}
	statsText.setText(errorsStatus + "\n" + initialText + "\n" + briefStats + "\n" + fullStats);
	statsText.setCaretPosition(0);
	JScrollPane scroller = new JScrollPane(statsText);
	scroller.setBounds(50, 100, (1024 - 100), 610);
	
	baseStatsPanel.add(scroller);
	
	baseStatsPanel.setBorder(BorderFactory.createLineBorder(Color.black));


	return scroller;
}

// any/all menu buttons primarily to return to the main menu.
private JPanel createMenuButtons() {

	JPanel menuGamePanel = new JPanel();
	menuGamePanel.setLayout(new GridLayout(1, 1, 0, 0));
	menuGamePanel.setBounds(50, 25, 220, 40);

	// TODO - GameButton needs an image?
	GameButton menuButton = new GameButton("<html>&#x2B05;  Return to Main Menu</html>", 730, 100);
	menuButton.addActionListener(super.getScreenAction("Menu"));
	
	menuButton.setUI(new MetalButtonUI());

	menuGamePanel.add(menuButton);
	return menuGamePanel;
}

private JPanel getViewGraphButton() {
	
	JPanel viewGraphPanel = new JPanel();
	viewGraphPanel.setLayout(new GridLayout(1, 1, 0, 0));
	viewGraphPanel.setBounds(700, 25, 220, 40);

	// TODO - GameButton needs an image?
	viewGraphButton = new GameButton("View Graph", 730, 100);
	viewGraphButton.addActionListener(super.getScreenAction("StatsMultiLoading"));
	viewGraphButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			blackjack.statsMultiLoadingScreen.update();
		}
		
	});
	
	viewGraphButton.setUI(new MetalButtonUI());
	viewGraphButton.setEnabled(true);

	viewGraphPanel.add(viewGraphButton);
	
	return viewGraphPanel;
}

}
