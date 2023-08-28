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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.metal.MetalButtonUI;

import com.ibm.vie.blackjack.casino.stats.GameResultStatCalculator;

public class ScreenStats extends Screen {

	double finalMoney = 123.32;
	private Blackjack blackjack;
	private GameResultStatCalculator stats;
	protected GameButton viewGraphButton;
	
public ScreenStats(CardLayout screenManager, JPanel screens, Blackjack blackjack) {
	super(screenManager, screens);
	this.blackjack = blackjack;
	init();
}

private void init() {

	createLayout();
}

@Override
public void refresh() {
	if (blackjack.gameThreads.size() > 0) {
		GameThread game = blackjack.gameThreads.poll();
		stats = game.getStats();
	}
	
//	System.out.println("Stats:\n" + stats.toString());
	
	finalMoney = stats.getFinalAvailableMoney();
	this.removeAll();
	
	this.setLayout(null);
	this.add(createStatsInfo());
//	this.add(createBaseStatsLayout(game.config.getInitialMoney(), stats.getFinalAvailableMoney(), stats.getTotalEarnings(), -1, stats.getTotalNumberOfRoundsPlayed()));
//	this.add(createBaseOutcomesPanel(stats.getTotalNumberOfRoundsPlayed(), stats.getTotalNumberOfHandsPlayed(), -1, -1));
	this.add(createMenuButtons());
	this.add(getViewGraphButton());

	this.redraw();
	
//	this.updateUI();
}

private void createLayout() {

	this.setLayout(null);

//	this.add(createBaseStatsLayout());
//	this.add(createBaseOutcomesPanel());
//	this.add(createStatsInfo());
	this.add(getViewGraphButton());
	this.add(createMenuButtons());

}

private JScrollPane createStatsInfo() {
	JPanel baseStatsPanel = new JPanel();
	baseStatsPanel.setBounds(100, 140, 500, 500);
	
	JTextArea statsText = new JTextArea();
	statsText.setFont(new Font("monospaced", Font.PLAIN, 12));
	statsText.setText(stats.toString());
	statsText.setCaretPosition(0);
	JScrollPane scroller = new JScrollPane(statsText);
	scroller.setBounds(50, 100, (1024 - 100), 610);
	
//	JScrollPane scroller = new JScrollPane(statsLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//	scroller.setSize(500, 500);
	
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
	viewGraphButton.addActionListener(super.getScreenAction("StatsLoading"));
	
	viewGraphButton.setUI(new MetalButtonUI());
	viewGraphButton.setEnabled(true);

	viewGraphPanel.add(viewGraphButton);
	
	return viewGraphPanel;
}

}
