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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalButtonUI;

public class StatsLoadingScreen extends Screen {
	protected JPanel statsGraph;
	protected JLabel loadingLabel;
	protected JPanel statsButtonPanel;
	protected GameButton showStatsButton;
	protected GameButton menuButton;
	private Blackjack blackjack;
	private JPanel menuButtons;
	// Standard output prior to a redirect, if null
	// standard out has not been redirected
	private static PrintStream originalStdOut = null;

	// redirected output, if stdout is redirected
	private static FileOutputStream redirectedOutput = null;

	public StatsLoadingScreen(CardLayout screenManager, JPanel screens, Blackjack blackjack) {
		super(screenManager, screens);
		this.blackjack = blackjack;
		init();
	}
	
	/**
	 * Return console output to standard out. If this is called and console output
	 * was not redirected, an IllegalArgumentException is thrown
	 * 
	 */
	public static void directConsoleOutputToStandardOut() {
		if (redirectedOutput == null || originalStdOut == null) {
			throw new IllegalArgumentException("Standard output was not correctly redirected!");
		}

		try {
			redirectedOutput.close();
			redirectedOutput = null;

			System.setOut(originalStdOut);
			originalStdOut = null;
			System.out.println("Directing standard output back to console.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Redirect all console output to a file-
	 */
	public static void redirectConsoleOutputToFile() {
		if (originalStdOut != null) {
			throw new IllegalArgumentException("An attempt was made to redirect standard out more than once!");
		}

		originalStdOut = System.out;
		try {
			System.out.println("Redirecting standard output to consoleOutput.out");
			redirectedOutput = new FileOutputStream("consoleOutput.out");
			System.setOut(new PrintStream(redirectedOutput));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected void update() {
		this.removeAll();

		this.setLayout(null);
		this.add(menuButtons);
		this.add(createStatsGraphPanelAndReturnIt());
		this.add(loadingLabel);
		this.add(statsButtonPanel);

		this.redraw();
	}

	private void init() {
		createLayout();
	}
	
	private JPanel createStatsGraphPanelAndReturnIt() {
		JPanel statsPanel = new JPanel();
		JPanel graphPanel = new JPanel();
		
		statsPanel.setLayout(new GridLayout(1, 1, 0, 0));
		statsPanel.setBounds(20, 70, 950, 650);
		
		graphPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), statsGraph.getName()));
		graphPanel.setLayout(new BorderLayout());
		graphPanel.setSize(950, 650);
		statsGraph.setSize(950, 650);
		
		JLabel graphXTitle = new JLabel();
		graphXTitle.setText("<html>" + "Number of Games" + "</html>");
		graphXTitle.setHorizontalAlignment(JLabel.CENTER);
		
		JLabel graphYTitle = new JLabel("Cash");
		graphYTitle.setText("<html>" + "Cash" + "</html>");
		graphYTitle.setHorizontalAlignment(JLabel.CENTER);
		
		graphPanel.add(statsGraph, BorderLayout.CENTER);
		graphPanel.add(graphXTitle, BorderLayout.SOUTH);
		graphPanel.add(graphYTitle, BorderLayout.WEST);
		statsPanel.add(graphPanel);

		return statsPanel;
	}

	private void createLayout() {
		this.setLayout(null);

		loadingLabel = new JLabel("Loading stats...");
		loadingLabel.setFont(new Font("Serif", Font.PLAIN, 32));
		loadingLabel.setBounds(400, 10, 275, 80);
		
		this.add(loadingLabel);

		this.statsButtonPanel = getViewStatsButton();
		this.menuButtons = createMenuButtons();

		this.add(menuButtons);
	}

	private JPanel getViewStatsButton() {
		JPanel showStatsPanel = new JPanel();
		showStatsPanel.setLayout(new GridLayout(1, 1, 0, 0));
		showStatsPanel.setBounds(700, 25, 220, 40);

		// TODO - GameButton needs an image?
		showStatsButton = new GameButton("View Stats", 730, 100);
		showStatsButton.addActionListener(super.getScreenAction("Stats"));

		showStatsButton.setUI(new MetalButtonUI());
		showStatsButton.setEnabled(false);

		showStatsPanel.add(showStatsButton);

		return showStatsPanel;
	}

	// any/all menu buttons primarily to return to the main menu.
	private JPanel createMenuButtons() {

		JPanel menuGamePanel = new JPanel();
		menuGamePanel.setLayout(new GridLayout(1, 1, 0, 0));
		menuGamePanel.setBounds(50, 25, 220, 40);

		// TODO - GameButton needs an image?
		menuButton = new GameButton("<html>&#x2B05;  Return to Main Menu</html>", 730, 100);
		menuButton.setEnabled(false);
		menuButton.addActionListener(super.getScreenAction("Menu"));
		menuButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				blackjack.gameThreads.clear();
			}

		});

		menuButton.setUI(new MetalButtonUI());
		menuButton.setEnabled(true);

		menuGamePanel.add(menuButton);
		return menuGamePanel;
	}
}
