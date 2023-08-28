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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.vie.blackjack.casino.config.CompetitionConfig;
import com.ibm.vie.blackjack.casino.config.TableConfig;
import com.ibm.vie.blackjack.casino.rules.OrdinaryBlackjackRules;
import com.ibm.vie.blackjack.casino.rules.RochesterMnCasinoRules;
import com.ibm.vie.blackjack.player.CasinoRules;

public class ScreenMainMenu extends Screen {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final ObjectMapper mapper = new ObjectMapper();
	private Blackjack blackjack;
	private int loadedTableConfigIndex = 0;
	private JList<TableConfig> configList;
	private JTextField tableConfigNameTextField;
	private JSpinner minBetSpinner;
	private JSpinner maxBetSpinner;
	private JSpinner numDecksSpinner;
	private JSpinner initialMoneySpinner;
	private JSpinner maxRoundsSpinner;
	private JSpinner deckNumberSpinner;

	private JLabel minBetValue;
	private JLabel maxBetValue;
	private JLabel startingCashValue;
	private JLabel numDecksValue;
	private JLabel maxRoundsValue;
	private JLabel seedValue;

	private DefaultListModel<TableConfig> listModel;
	GameButton multiStatsButton;

	private JComponent playRadioButtons;
	private JComponent gradeMeButton;
	private JComponent configListPanel;
	private JComponent optionsLayout;
	private JComponent directionTwo;
	private JComponent optionsPanel;

	public ScreenMainMenu(CardLayout screenManager, JPanel screens, Blackjack blackjack) {
		super(screenManager, screens);
		this.blackjack = blackjack;
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		createLayout();
	}

	private void createLayout() {
		this.setLayout(null);
		// this.add(createNormalGameLayout());
		// this.add(createTestGameLayout());
		// this.add(createStatsLayout());
//		 this.add(createMultiStatsLayout());
		 // this.add(createConfigListLayout());
		//this.add(createExitLayout());

		this.add(createTopRadioButtons());
		this.add(createDirectionOne());
		// this.add(createDirectionTwo());
		// this.add(createDirectionThree());
		// this.add(startRun());
	}

	private void clearScreen() {
		if(gradeMeButton != null) {
			this.remove(gradeMeButton);
		}

		if(playRadioButtons != null) {
			this.remove(playRadioButtons);
		}

		if(optionsLayout != null) {
			this.remove(optionsLayout);
		}

		if(configListPanel != null) {
			this.remove(configListPanel);
		}

		if(directionTwo != null) {
			this.remove(directionTwo);
		}

		if(optionsPanel != null) {
			this.remove(optionsPanel);
		}
	}

	private void playTableAction() {
		clearScreen();

		optionsLayout = createOptionsLayout();
		this.add(optionsLayout);

		configListPanel = createConfigListLayoutDropDown();
		this.add(configListPanel);

		directionTwo = createDirectionTwo("Select Table");
		this.add(directionTwo);

		playRadioButtons = createPlayRadioButtons();
		this.add(playRadioButtons);
		redraw();
	}

	private void gradeMeTableAction() {
		clearScreen();

		configListPanel = createConfigListLayout();
		this.add(configListPanel);

		gradeMeButton = createGradeMeButton();
		this.add(gradeMeButton);

		directionTwo = createDirectionTwo("Select Table(s)");
		this.add(directionTwo);

		redraw();
	}

	private void customTableAction() {
		clearScreen();

		gradeMeButton = createGradeMeButton();
		this.add(gradeMeButton);

		directionTwo = createDirectionTwo("Select Table(s)");
		this.add(directionTwo);

		optionsPanel = createOptionsLayoutOriginal();
		this.add(optionsPanel);

		configListPanel = createConfigListLayoutOriginal();
		this.add(configListPanel);

		redraw();
	}

	public JComponent createDirectionOne() {
		JPanel directionOne = new JPanel();
		// directionOne.setLayout(new FlowLayout());
		directionOne.setBounds(15, 65, 200, 50);

		JLabel text = new JLabel("Select Mode");
		text.setFont (text.getFont ().deriveFont (20.0f));

		directionOne.add(text);
		return directionOne;
	}

	public JComponent createDirectionTwo(String textInput) {
		JPanel directionTwo = new JPanel();
		directionTwo.setBounds(15, 300, 200, 50);

		JLabel text = new JLabel(textInput);
		text.setFont (text.getFont ().deriveFont (20.0f));

		directionTwo.add(text);
		return directionTwo;
	}

	// public JComponent createDirectionThree() {
	// 	JPanel directionThree = new JPanel();
	// 	directionThree.setBounds(15, 600, 200, 50);
	//
	// 	JLabel text = new JLabel("Select Style");
	// 	text.setFont (text.getFont ().deriveFont (20.0f));
	//
	// 	directionThree.add(text);
	// 	return directionThree;
	// }

	public JComponent startRun() {
		JPanel startRun = new JPanel();
		startRun.setBounds(300, 600, 220, 50);

		JButton run = new JButton("Start");
		run.setPreferredSize(new Dimension(220, 50));
		startRun.add(run);
		return startRun;
	}

	public JComponent createTopRadioButtons() {
		JPanel radioButtons = new JPanel();
		radioButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 1));
		radioButtons.setBounds(250, 50, 500, 120);
		// radioButtons.setBackground(Color.green);


		JToggleButton play = new JToggleButton("<html><center>" + "Play a Table" + "<center><html>");
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playTableAction();
			}
		});
		play.setPreferredSize(new Dimension(130, 80));
		// play.setBounds(new Rectangle(0, 0, 100, 100));

		JToggleButton debug = new JToggleButton("<html><center>" + "Play All Tables" + "<center><html>");
		debug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gradeMeTableAction();
			}
		});
		debug.setPreferredSize(new Dimension(130, 80));
		// debug.setBounds(new Rectangle(0, 0, 100, 100));

		JToggleButton autoPlay = new JToggleButton("<html><center>" + "Play Custom Table" + "<center><html>");
		autoPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customTableAction();
			}
		});
		autoPlay.setPreferredSize(new Dimension(130, 80));
		// autoPlay.setBounds(new Rectangle(0, 0, 100, 100));

		ButtonGroup playingOptions = new ButtonGroup();
		playingOptions.add(play);
		playingOptions.add(debug);
		playingOptions.add(autoPlay);

		radioButtons.add(play);
		radioButtons.add(debug);
		radioButtons.add(autoPlay);

		return radioButtons;
	}

	public JComponent createPlayRadioButtons() {
		JPanel radioButtons = new JPanel();
		radioButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 1));
		radioButtons.setBounds(250, 575, 500, 120);
		// radioButtons.setBackground(Color.green);


		JButton debug = new JButton("Debug");
		debug.setPreferredSize(new Dimension(100, 80));
		debug.addActionListener(super.getScreenAction("TestGame"));
		debug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				blackjack.uiFinished = false;
				Thread thread = new Thread(){
			    public void run(){
			    		GameThread game;
							try {
								game = new GameThread( blackjack,
										blackjack.clazz,
										new TestGameObserver(blackjack.testGame, blackjack),
										Integer.parseInt(startingCashValue.getText()),
										Integer.parseInt(minBetValue.getText()),
										Integer.parseInt(maxBetValue.getText()),
										Integer.parseInt(numDecksValue.getText()),
										Integer.parseInt(maxRoundsValue.getText()),
										Integer.parseInt(seedValue.getText()),
										blackjack.testGame);
					    		blackjack.gameThreads.add(game);
					    		game.playGame();
							} catch (InstantiationException | IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    }
			  };

			  thread.start();
			}
		});
		// play.setBounds(new Rectangle(0, 0, 100, 100));

		JButton play = new JButton("Play");
		play.setPreferredSize(new Dimension(100, 80));
		play.addActionListener(super.getScreenAction("NormalGame"));
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

					blackjack.uiFinished = false;
					Thread thread = new Thread(){
					public void run(){
							GameThread game;
							try {
								game = new GameThread( blackjack,
										blackjack.clazz,
										new NormalGameObserver(blackjack.normalGame, blackjack),
										Integer.parseInt(startingCashValue.getText()),
										Integer.parseInt(minBetValue.getText()),
										Integer.parseInt(maxBetValue.getText()),
										Integer.parseInt(numDecksValue.getText()),
										Integer.parseInt(maxRoundsValue.getText()),
										Integer.parseInt(seedValue.getText()),
										createCasinoRules());
								blackjack.gameThreads.add(game);
								game.playGame();
							} catch (InstantiationException | IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				};

				thread.start();
			}
		});
		// debug.setBounds(new Rectangle(0, 0, 100, 100));

		JButton autoPlay = new JButton("Stats");
		autoPlay.setPreferredSize(new Dimension(100, 80));
		autoPlay.addActionListener(super.getScreenAction("StatsLoading"));
		autoPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Thread thread = new Thread(){
			    public void run(){
			    		blackjack.statsLoadingScreen.loadingLabel.setText("Loading stats...");
			    		blackjack.statsLoadingScreen.showStatsButton.setEnabled(false);
			    		blackjack.statsLoadingScreen.menuButton.setEnabled(false);
			    		GameThread game;
							try {
								game = new GameThread( blackjack,
										blackjack.clazz,
										new ScoreSingleGraphObserverGui(blackjack.statsLoadingScreen, tableConfigNameTextField.getText()),
										Integer.parseInt(startingCashValue.getText()),
										Integer.parseInt(minBetValue.getText()),
										Integer.parseInt(maxBetValue.getText()),
										Integer.parseInt(numDecksValue.getText()),
										Integer.parseInt(maxRoundsValue.getText()),
										Integer.parseInt(seedValue.getText()),
										createCasinoRules());
								StatsLoadingScreen.redirectConsoleOutputToFile();
					    		try {
					    		  game.playGame();
					    		} finally {
					    		  StatsLoadingScreen.directConsoleOutputToStandardOut();
					    		}

					    		blackjack.gameThreads.add(game);
					    		blackjack.statsLoadingScreen.menuButton.setEnabled(true);
					    		blackjack.statsLoadingScreen.loadingLabel.setText("Stats complete!");
					    		blackjack.statsLoadingScreen.showStatsButton.setEnabled(true);
	//				    		screenManager.show(screens, "Stats");
							} catch (InstantiationException | IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    }
			  };

			  thread.start();

			}
		});
		// autoPlay.setBounds(new Rectangle(0, 0, 100, 100));

		radioButtons.add(play);
		radioButtons.add(debug);
		radioButtons.add(autoPlay);

		return radioButtons;
	}

	public JComponent createGradeMeButton() {
		JPanel radioButtons = new JPanel();
		radioButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 1));
		radioButtons.setBounds(250, 575, 500, 120);
		// radioButtons.setBackground(Color.green);

		JButton play = new JButton("Run Selected");
		play.setPreferredSize(new Dimension(380, 80));
		play.addActionListener(super.getScreenAction("StatsMultiLoading"));
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

	    		blackjack.statsMultiLoadingScreen.loadingLabel.setText("Loading stats...");
	    		blackjack.statsMultiLoadingScreen.showStatsButton.setEnabled(false);
	    		blackjack.statsMultiLoadingScreen.menuButton.setEnabled(false);
	    		blackjack.statsMultiLoadingScreen.startLoadingGameThreads(configList.getSelectedValuesList().size());

				//for each table, spin up a thread to run a new game
				for(TableConfig table : configList.getSelectedValuesList()) {
					Thread thread = new Thread(){
						public void run() {
			    			GameThread game;
								try {
									game = new GameThread( blackjack,
											blackjack.clazz,
											new ScoreMultiGraphObserverGui(blackjack.statsMultiLoadingScreen, table.getName()),
											table.getInitialMoney(),
											table.getMinBet(),
											table.getMaxBet(),
											table.getNumDecks(),
											table.getNumRounds(),
											table.getDeckNumber(),
											table.getName(),
											createCasinoRules());
						    		game.playGame();
						    		blackjack.gameThreads.add(game);
						    		blackjack.statsMultiLoadingScreen.finishedGameThread(table.getName());
								} catch (InstantiationException | IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}
				  };
				  thread.start();
		        }
			}
		});
		// play.setBounds(new Rectangle(0, 0, 100, 100));

		radioButtons.add(play);

		return radioButtons;
	}


	// // uneditable-version
	public JComponent createOptionsLayout() {

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(7, 1, 0, 0));
		optionsPanel.setBounds(300, 330, 425, 200);

		// Add field for naming the configuration
		tableConfigNameTextField = new JTextField("Untitled Config", 20);

		File customConfig = new File(this.getClass().getResource("/student/player/").getFile(),"customConfig.json");

		// may fail
		CompetitionConfig compConf = getConfigFromFile(customConfig);

		if (compConf == null ||
			compConf.getTableConfigs() == null ||
			compConf.getTableConfigs().isEmpty()) {
			compConf = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
		}

		// Minimum Bet
		JLabel minBetText = new JLabel("Minimum Bet:");

		Integer currentMinBet = new Integer(compConf.getTableConfigs().get(0).getMinBet());
		minBetValue = new JLabel(currentMinBet.toString());
		

		JPanel minBetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		minBetPanel.add(minBetText);
		minBetPanel.add(minBetValue);

		// Max Bet
		JLabel maxBetText = new JLabel("Maximum Bet:");

		Integer currentMaxBet = new Integer(compConf.getTableConfigs().get(0).getMaxBet());
		maxBetValue = new JLabel(currentMaxBet.toString());

		JPanel maxBetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		maxBetPanel.add(maxBetText);
		maxBetPanel.add(maxBetValue);

		// Number of Decks
		JLabel numDecksText = new JLabel("Number of decks:");

		Integer currentNumDecks = new Integer(compConf.getTableConfigs().get(0).getNumDecks());
		numDecksValue = new JLabel(currentNumDecks.toString());

		JPanel numDecksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		numDecksPanel.add(numDecksText);
		numDecksPanel.add(numDecksValue);

		// Amount of starting cash
		JLabel numStartingCash = new JLabel("Starting amount of cash for player:");

		Integer currentStartCash = new Integer(compConf.getTableConfigs().get(0).getInitialMoney());
		startingCashValue = new JLabel(currentStartCash.toString());

		JPanel startingCashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		startingCashPanel.add(numStartingCash);
		startingCashPanel.add(startingCashValue);

		// Max number of rounds
		JLabel numMaxRounds = new JLabel("Maximum number of rounds to play:");

		Integer currentMaxRounds = new Integer(compConf.getTableConfigs().get(0).getNumRounds());
		maxRoundsValue = new JLabel(currentMaxRounds.toString());

		JPanel maxRoundsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		maxRoundsPanel.add(numMaxRounds);
		maxRoundsPanel.add(maxRoundsValue);

		// Seed
		JLabel seedText = new JLabel("Deck number:");
		Integer currentSeed = new Integer(compConf.getTableConfigs().get(0).getDeckNumber());
		seedValue = new JLabel(currentSeed.toString());

		JPanel seedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		seedPanel.add(seedText);
		seedPanel.add(seedValue);

		optionsPanel.add(minBetPanel);
		optionsPanel.add(maxBetPanel);
		optionsPanel.add(numDecksPanel);
		optionsPanel.add(startingCashPanel);
		optionsPanel.add(maxRoundsPanel);
		optionsPanel.add(seedPanel);
		return optionsPanel;
	}

	public JComponent createOptionsLayoutOriginal() {

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(7, 1, 0, 0));
		optionsPanel.setBounds(240, 260, 410, 200);

		// Add field for naming the configuration
		JPanel tableConfigNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel tableConfigNameLabel = new JLabel("Table config name:");
		tableConfigNamePanel.add(tableConfigNameLabel);
		tableConfigNameTextField = new JTextField("Untitled Config", 20);
		tableConfigNamePanel.add(tableConfigNameTextField);

		// Minimum Bet
		JLabel minBetText = new JLabel("Set Minimum Bet:");

		Integer currentMinBet = new Integer(5);
		Integer minMinBet = new Integer(1);
		Integer maxMinBet = new Integer(10000);
		Integer stepMinBet = new Integer(1);
		SpinnerNumberModel minBetSpinnerModel = new SpinnerNumberModel(currentMinBet, minMinBet, maxMinBet, stepMinBet);
		minBetSpinner = new JSpinner(minBetSpinnerModel);
		minBetText.setLabelFor(minBetSpinner);

		JPanel minBetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		minBetPanel.add(minBetText);
		minBetPanel.add(minBetSpinner);

		// Max Bet
		JLabel maxBetText = new JLabel("Set Maximum Bet:");

		Integer currentMaxBet = new Integer(100);
		Integer minMaxBet = new Integer(10);
		Integer maxMaxBet = new Integer(100000);
		Integer stepMaxBet = new Integer(1);
		SpinnerNumberModel maxBetSpinnerModel = new SpinnerNumberModel(currentMaxBet, minMaxBet, maxMaxBet, stepMaxBet);
		maxBetSpinner = new JSpinner(maxBetSpinnerModel);

		JPanel maxBetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		maxBetPanel.add(maxBetText);
		maxBetPanel.add(maxBetSpinner);

		// Number of Decks
		JLabel numDecksText = new JLabel("Number of decks:");

		Integer currentNumDecks = new Integer(1);
		Integer minNumDecks = new Integer(1);
		Integer maxNumDecks = new Integer(100);
		Integer stepNumDecks = new Integer(1);
		SpinnerNumberModel numDecksSpinnerModel = new SpinnerNumberModel(currentNumDecks, minNumDecks, maxNumDecks,
				stepNumDecks);
		numDecksSpinner = new JSpinner(numDecksSpinnerModel);

		JPanel numDecksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		numDecksPanel.add(numDecksText);
		numDecksPanel.add(numDecksSpinner);

		// Amount of starting cash
		JLabel numStartingCash = new JLabel("Starting amount of cash for player:");

		Integer currentStartCash = new Integer(100);
		Integer minStartCash = new Integer(10);
		Integer maxStartCash = new Integer(100000000);
		Integer stepStartCash = new Integer(1);
		SpinnerNumberModel startCashSpinnerModel = new SpinnerNumberModel(currentStartCash, minStartCash, maxStartCash,
				stepStartCash);
		initialMoneySpinner = new JSpinner(startCashSpinnerModel);

		JPanel startingCashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		startingCashPanel.add(numStartingCash);
		startingCashPanel.add(initialMoneySpinner);

		// Max number of rounds
		JLabel numMaxRounds = new JLabel("Maximum number of rounds to play:");

		Integer currentMaxRounds = new Integer(100);
		Integer minMaxRounds = new Integer(1);
		Integer maxMaxRounds = new Integer(10000000);
		Integer stepMaxRounds = new Integer(1);
		SpinnerNumberModel maxRoundsSpinnerModel = new SpinnerNumberModel(currentMaxRounds, minMaxRounds, maxMaxRounds,
				stepMaxRounds);
		maxRoundsSpinner = new JSpinner(maxRoundsSpinnerModel);

		JPanel maxRoundsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		maxRoundsPanel.add(numMaxRounds);
		maxRoundsPanel.add(maxRoundsSpinner);

		// Seed
		JLabel seedText = new JLabel("Deck number:");
		Integer currentSeed = new Integer(42);
		Integer minSeed = new Integer(0);
		Integer maxSeed = new Integer(2147483646);
		Integer stepSeed = new Integer(1);
		SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel(currentSeed, minSeed, maxSeed,
				stepSeed);
		deckNumberSpinner = new JSpinner(seedSpinnerModel);

		JPanel seedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		seedPanel.add(seedText);
		seedPanel.add(deckNumberSpinner);

		optionsPanel.add(tableConfigNamePanel);
		optionsPanel.add(minBetPanel);
		optionsPanel.add(maxBetPanel);
		optionsPanel.add(numDecksPanel);
		optionsPanel.add(startingCashPanel);
		optionsPanel.add(maxRoundsPanel);
		optionsPanel.add(seedPanel);
		return optionsPanel;
	}

	public JComponent createNormalGameLayout() {
		JPanel normalGamePanel = new JPanel();
		normalGamePanel.setLayout(new BorderLayout());
		normalGamePanel.setBackground(Color.gray);
		normalGamePanel.setBounds(150, 250, 100, 85);

		GameButton normalGameButton = new GameButton("Play", 100, 85);
		// normalGameButton.addActionListener(super.getScreenAction("NormalGame"));
		// normalGameButton.addActionListener(new ActionListener() {
		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		//
		// 			blackjack.uiFinished = false;
		// 			Thread thread = new Thread(){
		// 			public void run(){
		// 					GameThread game;
		// 					try {
		// 						game = new GameThread( blackjack,
		// 								blackjack.clazz,
		// 								new NormalGameObserver(blackjack.normalGame, blackjack),
		// 								(Integer) initialMoneySpinner.getValue(),
		// 								(Integer) minBetSpinner.getValue(),
		// 								(Integer) maxBetSpinner.getValue(),
		// 								(Integer) numDecksSpinner.getValue(),
		// 								(Integer) maxRoundsSpinner.getValue(),
		// 								(Integer) deckNumberSpinner.getValue(),
		// 								createCasinoRules());
		// 						blackjack.gameThreads.add(game);
		// 						game.playGame();
		// 					} catch (InstantiationException | IllegalAccessException e) {
		// 						// TODO Auto-generated catch block
		// 						e.printStackTrace();
		// 					}
		// 			}
		// 		};
		//
		// 		thread.start();
		// 	}
		// });

		normalGamePanel.add(normalGameButton, BorderLayout.CENTER);
		// JLabel playDescription = new JLabel("Manually Play Blackjack");
		// normalGamePanel.add(playDescription, BorderLayout.EAST);
		return normalGamePanel;
	}

	public JComponent createTestGameLayout() {
		JPanel testGamePanel = new JPanel();
		testGamePanel.setLayout(new BorderLayout());
		testGamePanel.setBackground(Color.GRAY);
		testGamePanel.setBounds(392, 250, 100, 85);

		GameButton testGameButton = new GameButton("Debug", 100, 85);
		testGameButton.addActionListener(super.getScreenAction("TestGame"));
		testGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				blackjack.uiFinished = false;
				Thread thread = new Thread(){
			    public void run(){
			    		GameThread game;
							try {
								game = new GameThread( blackjack,
										blackjack.clazz,
										new TestGameObserver(blackjack.testGame, blackjack),
										(Integer) initialMoneySpinner.getValue(),
										(Integer) minBetSpinner.getValue(),
										(Integer) maxBetSpinner.getValue(),
										(Integer) numDecksSpinner.getValue(),
										(Integer) maxRoundsSpinner.getValue(),
										(Integer) deckNumberSpinner.getValue(),
										blackjack.testGame);
					    		blackjack.gameThreads.add(game);
					    		game.playGame();
							} catch (InstantiationException | IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    }
			  };

			  thread.start();
			}
		});

		testGamePanel.add(testGameButton, BorderLayout.CENTER);
		return testGamePanel;
	}

	public JComponent createStatsLayout() {
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BorderLayout());
		statsPanel.setBackground(Color.gray);
		statsPanel.setBounds(630, 275, 220, 180);

		GameButton statsButton = new GameButton("<html><center>" + "Auto-Play Selected Table" + "<center><html>", 200, 170);
		statsButton.addActionListener(super.getScreenAction("StatsLoading"));
		statsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Thread thread = new Thread(){
			    public void run(){
			    		blackjack.statsLoadingScreen.loadingLabel.setText("Loading stats...");
			    		blackjack.statsLoadingScreen.showStatsButton.setEnabled(false);
			    		blackjack.statsLoadingScreen.menuButton.setEnabled(false);
			    		GameThread game;
							try {
								game = new GameThread( blackjack,
										blackjack.clazz,
										new ScoreSingleGraphObserverGui(blackjack.statsLoadingScreen, tableConfigNameTextField.getText()),
										(Integer) initialMoneySpinner.getValue(),
										(Integer) minBetSpinner.getValue(),
										(Integer) maxBetSpinner.getValue(),
										(Integer) numDecksSpinner.getValue(),
										(Integer) maxRoundsSpinner.getValue(),
										(Integer) deckNumberSpinner.getValue(),
										createCasinoRules());
								StatsLoadingScreen.redirectConsoleOutputToFile();
					    		try {
					    		  game.playGame();
					    		} finally {
					    		  StatsLoadingScreen.directConsoleOutputToStandardOut();
					    		}

					    		blackjack.gameThreads.add(game);
					    		blackjack.statsLoadingScreen.menuButton.setEnabled(true);
					    		blackjack.statsLoadingScreen.loadingLabel.setText("Stats complete!");
					    		blackjack.statsLoadingScreen.showStatsButton.setEnabled(true);
	//				    		screenManager.show(screens, "Stats");
							} catch (InstantiationException | IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    }
			  };

			  thread.start();

			}
		});

		statsPanel.add(statsButton, BorderLayout.CENTER);
		return statsPanel;
	}

	public JComponent createMultiStatsLayout() {
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BorderLayout());
		statsPanel.setBackground(Color.gray);
		statsPanel.setBounds(1030, 250, 100, 85);

		multiStatsButton = new GameButton("<html><center>" + "Auto-Play" + "<center><html>", 100, 85);
		multiStatsButton.addActionListener(super.getScreenAction("StatsMultiLoading"));
		multiStatsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

	    		blackjack.statsMultiLoadingScreen.loadingLabel.setText("Loading stats...");
	    		blackjack.statsMultiLoadingScreen.showStatsButton.setEnabled(false);
	    		blackjack.statsMultiLoadingScreen.menuButton.setEnabled(false);
	    		blackjack.statsMultiLoadingScreen.startLoadingGameThreads(configList.getSelectedValuesList().size());
				//for each table, spin up a thread to run a new game

				for(int i = 0; i < configList.getModel().getSize(); i++) {
					TableConfig table = configList.getModel().getElementAt(i);
					Thread thread = new Thread(){
						public void run() {
			    			GameThread game;
								try {
									game = new GameThread( blackjack,
											blackjack.clazz,
											new ScoreMultiGraphObserverGui(blackjack.statsMultiLoadingScreen, table.getName()),
											table.getInitialMoney(),
											table.getMinBet(),
											table.getMaxBet(),
											table.getNumDecks(),
											table.getNumRounds(),
											table.getDeckNumber(),
											table.getName(),
											createCasinoRules());
						    		game.playGame();
						    		blackjack.gameThreads.add(game);
						    		blackjack.statsMultiLoadingScreen.finishedGameThread(table.getName());
								} catch (InstantiationException | IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}
				  };
				  thread.start();
		        }

	    		// for(TableConfig table: configList.getSelectedValuesList()) {
				// 	Thread thread = new Thread(){
				// 		public void run() {
			    // 			GameThread game;
				// 				try {
				// 					game = new GameThread( blackjack,
				// 							blackjack.clazz,
				// 							new ScoreMultiGraphObserverGui(blackjack.statsMultiLoadingScreen, table.getName()),
				// 							table.getInitialMoney(),
				// 							table.getMinBet(),
				// 							table.getMaxBet(),
				// 							table.getNumDecks(),
				// 							table.getNumRounds(),
				// 							table.getDeckNumber(),
				// 							table.getName(),
				// 							createCasinoRules());
				// 		    		game.playGame();
				// 		    		blackjack.gameThreads.add(game);
				// 		    		blackjack.statsMultiLoadingScreen.finishedGameThread(table.getName());
				// 				} catch (InstantiationException | IllegalAccessException e) {
				// 					// TODO Auto-generated catch block
				// 					e.printStackTrace();
				// 				}
				// 		}
				//   };
				//
				//   thread.start();
	    		// }
			}
		});

		statsPanel.add(multiStatsButton, BorderLayout.CENTER);
		return statsPanel;
	}

	public JComponent createConfigListLayoutDropDown() {
		JPanel configListLayout = new JPanel();
		configListLayout.setLayout(new BorderLayout());
		configListLayout.setBounds(300, 310, 270, 25);

		if (listModel != null) {
			listModel.clear();
		}

		listModel = new DefaultListModel<TableConfig>();
		File customConfig = new File(this.getClass().getResource("/student/player/").getFile(),"customConfig.json");

		// may fail
		CompetitionConfig compConf = getConfigFromFile(customConfig);

		if (compConf == null ||
				compConf.getTableConfigs() == null ||
				compConf.getTableConfigs().isEmpty()) {
	  compConf = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
		}


		TableConfig arrayTableConfig[] = new TableConfig[compConf.getTableConfigs().size()];
		int i = 0;
		for(TableConfig tblConf: compConf.getTableConfigs()) {
			listModel.addElement(tblConf);
			arrayTableConfig[i++] = tblConf;
		}

		// listModel = new DefaultListModel<TableConfig>();
		// File customConfig = new File(this.getClass().getResource("/student/player/").getFile(),"customConfig.json");
		//
		// // may fail
		// CompetitionConfig compConf = getConfigFromFile(customConfig);
		//
		// if (compConf == null ||
		// 	compConf.getTableConfigs() == null ||
		// 	compConf.getTableConfigs().isEmpty()) {
		// 	compConf = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
		// }
		//
		// TableConfig arrayTableConfig[] = new TableConfig[compConf.getTableConfigs().size()];
		// int i = 0;
		// for(TableConfig tblConf: compConf.getTableConfigs()) {
		// 	 // listModel.addElement(tblConf);
		// 	arrayTableConfig[i++] = tblConf;
		// }

		JComboBox comboBox = new JComboBox(arrayTableConfig);
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
		        int tableIndex = (int)cb.getSelectedIndex();
				TableConfig table = (TableConfig)cb.getSelectedItem();
				loadTableConfigInForm(table);
			}
		});

		configListLayout.add(comboBox, BorderLayout.CENTER);


		// configList.addListSelectionListener(new ListSelectionListener() {
		// 	@Override
		// 	public void valueChanged(ListSelectionEvent e) {
		// 		/*				int leadSelectionIndex = configList.getLeadSelectionIndex();
		// 		if (!configList.isSelectionEmpty() && listModel.size() > leadSelectionIndex) {
		// 			loadTableConfigInForm(leadSelectionIndex);
		// 		}
		// 		 */
		// 		int[] idxs = configList.getSelectedIndices();
		// 		if (idxs.length == 0) {
		// 			removeSelectedButton.setEnabled(false);
		// 			multiStatsButton.setEnabled(false);
		// 			saveButton.setEnabled(false);
		// 		} else {
		// 			removeSelectedButton.setEnabled(true);
		// 			multiStatsButton.setEnabled(true);
		// 			if (idxs.length == 1) {
		// 				saveButton.setEnabled(true);
		// 				loadTableConfigInForm(idxs[0]);
		// 			} else {
		// 				saveButton.setEnabled(false);
		// 			}
		// 		}
		// 	}
		// });

		return configListLayout;
	}

	// old version
	public JComponent createConfigListLayout() {
		JPanel configListLayout = new JPanel();
		configListLayout.setLayout(new BorderLayout());
		configListLayout.setBounds(300, 255, 270, 200);

		if (listModel != null) {
			listModel.clear();
		}

		listModel = new DefaultListModel<TableConfig>();
		File customConfig = new File(this.getClass().getResource("/student/player/").getFile(),"customConfig.json");

		// may fail
		CompetitionConfig compConf = getConfigFromFile(customConfig);

		if (compConf == null ||
				compConf.getTableConfigs() == null ||
				compConf.getTableConfigs().isEmpty()) {
	  compConf = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
		}


		TableConfig arrayTableConfig[] = new TableConfig[compConf.getTableConfigs().size()];
		int i = 0;
		for(TableConfig tblConf: compConf.getTableConfigs()) {
			listModel.addElement(tblConf);
			// arrayTableConfig[i++] = tblConf;
		}

		configList = new JList<TableConfig>(listModel);
		configList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// JLabel configListLabel = new JLabel("Table Configurations");
		// configListLabel.setLabelFor(configList);

		JPanel labelPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel();

		// JButton newButton = new GameButton("New");
		// JButton saveButton = new GameButton("Save");
		// JButton removeSelectedButton = new GameButton("Remove Selected");
		// JButton defaultsButton = new GameButton("Reload Defaults");

		// labelPanel.add(configListLabel, BorderLayout.WEST);
	//		labelPanel.add(defaultsButton, BorderLayout.EAST);
	//
	//		buttonPanel.add(newButton);
	//		buttonPanel.add(saveButton);
	//		buttonPanel.add(removeSelectedButton);

		configListLayout.add(labelPanel, BorderLayout.NORTH);
		 configListLayout.add(new JScrollPane(configList), BorderLayout.CENTER);
	//		configListLayout.add(new JComboBox(arrayTableConfig), BorderLayout.CENTER);
		configListLayout.add(buttonPanel, BorderLayout.SOUTH);

		// configList.addListSelectionListener(new ListSelectionListener() {
		// 	@Override
		// 	public void valueChanged(ListSelectionEvent e) {
		// 		/*				int leadSelectionIndex = configList.getLeadSelectionIndex();
		// 		if (!configList.isSelectionEmpty() && listModel.size() > leadSelectionIndex) {
		// 			loadTableConfigInForm(leadSelectionIndex);
		// 		}
		// 		 */
		// 		int[] idxs = configList.getSelectedIndices();
		// 		if (idxs.length == 0) {
		// 			removeSelectedButton.setEnabled(false);
		// 			multiStatsButton.setEnabled(false);
		// 			saveButton.setEnabled(false);
		// 		} else {
		// 			removeSelectedButton.setEnabled(true);
		// 			multiStatsButton.setEnabled(true);
		// 			if (idxs.length == 1) {
		// 				saveButton.setEnabled(true);
		// 				loadTableConfigInForm(idxs[0]);
		// 			} else {
		// 				saveButton.setEnabled(false);
		// 			}
		// 		}
		// 	}
		// });

		if (listModel.size() > 0) {
			// pre-load form with first TableConfig
			configList.setSelectedIndex(0);
		}

		// newButton.addActionListener(new ActionListener() {
		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		TableConfig cfgTgt = getTableConfigFromForm();
		// 		listModel.addElement(cfgTgt);
		// 		saveConfigToDisk(new ArrayList<TableConfig>(Collections.list(listModel.elements())), customConfig);
		// 	}
		// });

		// saveButton.addActionListener(new ActionListener() {
		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		TableConfig cfgTgt = getTableConfigFromForm();
		// 		if (loadedTableConfigIndex < listModel.size()) {
		// 			listModel.set(loadedTableConfigIndex,cfgTgt);
		// 		} else { // probably empty. will behave list adding new
		// 			listModel.addElement(cfgTgt);
		// 		}
		// 		saveConfigToDisk(new ArrayList<TableConfig>(Collections.list(listModel.elements())), customConfig);
		// 	}
		// });
		//
		// removeSelectedButton.addActionListener(new ActionListener() {
		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		int idx = configList.getSelectedIndex(); // smallest selected index
		// 		for(TableConfig obj: configList.getSelectedValuesList()) {
		// 			listModel.removeElement(obj);
		// 		}
		// 		saveConfigToDisk(new ArrayList<TableConfig>(Collections.list(listModel.elements())), customConfig);
		// 		if (idx < listModel.size()) {
		// 			configList.setSelectedIndex(idx); // if the same index is still valid, load it
		// 		} else if (listModel.size() > 0) {
		// 			configList.setSelectedIndex(idx - 1); // pretty sure this has to be valid if the above is not true
		// 		} // else we're empty, nothing to do
		// 	}
		// });

		// defaultsButton.addActionListener(new ActionListener() {
		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		if (!listModel.isEmpty()) {
		// 			// prompt for conf
		// 			if (JOptionPane.showConfirmDialog(null, "Reloading default board configurations will discard any custom board configurations or changes. Are you sure?", "Are you sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
		// 				listModel.clear();
		// 			} else {
		// 				return;
		// 			}
		// 		}
		//
		// 		CompetitionConfig compConf = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
		//
		// 		for(TableConfig tblConf: compConf.getTableConfigs()) {
		// 			listModel.addElement(tblConf);
		// 		}
		// 		configList.setSelectedIndex(0);
		// 	}
		// });

		return configListLayout;
	}

	// old version
	public JComponent createConfigListLayoutOriginal() {
		JPanel configListLayout = new JPanel();
		configListLayout.setLayout(new BorderLayout());
		configListLayout.setBounds(650, 255, 270, 200);

		listModel = new DefaultListModel<TableConfig>();
		File customConfig = new File(this.getClass().getResource("/student/player/").getFile(),"customConfig.json");

		// may fail
		CompetitionConfig compConf = getConfigFromFile(customConfig);

		if (compConf == null ||
				compConf.getTableConfigs() == null ||
				compConf.getTableConfigs().isEmpty()) {
      compConf = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
		}

		for(TableConfig tblConf: compConf.getTableConfigs()) {
			listModel.addElement(tblConf);
		}

		configList = new JList<TableConfig>(listModel);
		configList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JLabel configListLabel = new JLabel("Table Configurations");
		configListLabel.setLabelFor(configList);

		JPanel labelPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel();

		JButton newButton = new GameButton("New");
		JButton saveButton = new GameButton("Save");
		JButton removeSelectedButton = new GameButton("Remove Selected");
		JButton defaultsButton = new GameButton("Reload Defaults");

		labelPanel.add(configListLabel, BorderLayout.WEST);
		labelPanel.add(defaultsButton, BorderLayout.EAST);

		buttonPanel.add(newButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(removeSelectedButton);

		configListLayout.add(labelPanel, BorderLayout.NORTH);
		configListLayout.add(new JScrollPane(configList), BorderLayout.CENTER);
		configListLayout.add(buttonPanel, BorderLayout.SOUTH);

		configList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				/*				int leadSelectionIndex = configList.getLeadSelectionIndex();
				if (!configList.isSelectionEmpty() && listModel.size() > leadSelectionIndex) {
					loadTableConfigInForm(leadSelectionIndex);
				}
				 */
				int[] idxs = configList.getSelectedIndices();
				if (idxs.length == 0) {
					removeSelectedButton.setEnabled(false);
//					multiStatsButton.setEnabled(false);
					saveButton.setEnabled(false);
				} else {
					removeSelectedButton.setEnabled(true);
//					multiStatsButton.setEnabled(true);
					if (idxs.length == 1) {
						saveButton.setEnabled(true);
						loadTableConfigInFormOriginal(idxs[0]);
					} else {
						saveButton.setEnabled(false);
					}
				}
			}
		});

		if (listModel.size() > 0) {
			// pre-load form with first TableConfig
			configList.setSelectedIndex(0);
		}

		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String originalName = tableConfigNameTextField.getText();
				// for (TableConfig tblConf : listModel) {
				// 	if (tblConf.getName().equals(originalName + "*")) {
				// 		originalName
				// 	}
				// }
				Random random = new Random();
				int n = random.nextInt(2000);

				TableConfig cfgTgt = getTableConfigFromFormCustomName(originalName + "_custom_" + n);
				listModel.addElement(cfgTgt);
				// saveConfigToDisk(new ArrayList<TableConfig>(Collections.list(listModel.elements())), customConfig);
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TableConfig cfgTgt = getTableConfigFromForm();
				CompetitionConfig compConf = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
				boolean matchFound = false;
				for(TableConfig tblConf: compConf.getTableConfigs()) {
					if (tblConf.getName().equals(cfgTgt.getName())) {
						matchFound = true;
					}
				}
				if (matchFound == false) {
					if (loadedTableConfigIndex < listModel.size()) {
						listModel.set(loadedTableConfigIndex,cfgTgt);
					} else { // probably empty. will behave list adding new
						listModel.addElement(cfgTgt);
					}
					// get list from original custom

					DefaultListModel<TableConfig> listModelCustom = new DefaultListModel<TableConfig>();
					File customConfig = new File(this.getClass().getResource("/student/player/").getFile(),"customConfig.json");
					CompetitionConfig compConfCustom = getConfigFromFile(customConfig);

					if (compConf == null ||
							compConfCustom.getTableConfigs() == null ||
							compConfCustom.getTableConfigs().isEmpty()) {
			      compConf = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
					}

					for(TableConfig tblConfCustom: compConfCustom.getTableConfigs()) {
						listModelCustom.addElement(tblConfCustom);
					}

					listModelCustom.addElement(cfgTgt);

					// add to that custom list
					saveConfigToDisk(new ArrayList<TableConfig>(Collections.list(listModelCustom.elements())), customConfig);
				}
				else {
					JOptionPane.showMessageDialog(null, "Sorry, you cannot save changes to a default table");
				}
			}
		});

		removeSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int idx = configList.getSelectedIndex(); // smallest selected index
				for(TableConfig obj: configList.getSelectedValuesList()) {
					listModel.removeElement(obj);
				}
				saveConfigToDisk(new ArrayList<TableConfig>(Collections.list(listModel.elements())), customConfig);
				if (idx < listModel.size()) {
					configList.setSelectedIndex(idx); // if the same index is still valid, load it
				} else if (listModel.size() > 0) {
					configList.setSelectedIndex(idx - 1); // pretty sure this has to be valid if the above is not true
				} // else we're empty, nothing to do
			}
		});

		defaultsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!listModel.isEmpty()) {
					// prompt for conf
					if (JOptionPane.showConfirmDialog(null, "Reloading default board configurations will discard any custom board configurations or changes. Are you sure?", "Are you sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						listModel.clear();
					} else {
						return;
					}
				}

				CompetitionConfig compConfDefault = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
				DefaultListModel<TableConfig> listModelCustom = new DefaultListModel<TableConfig>();
				File customConfig = new File(this.getClass().getResource("/student/player/").getFile(),"customConfig.json");
				CompetitionConfig compConf = getConfigFromFile(customConfig);

				if (compConf == null ||
						compConf.getTableConfigs() == null ||
						compConf.getTableConfigs().isEmpty()) {
		      compConf = getConfigFromInputStream(CompetitionConfig.class.getResourceAsStream("defaultConfig.json"));
				}

				for(TableConfig tblConfCustom: compConf.getTableConfigs()) {
					listModelCustom.addElement(tblConfCustom);
				}

				for(int i = 0; i < listModelCustom.size(); i++) {
					TableConfig tblConfCustom = listModelCustom.get(i);
					for(TableConfig tblConfDefault: compConfDefault.getTableConfigs()) {
						if(tblConfCustom.getName().equals(tblConfDefault.getName())) {
							listModelCustom.removeElement(tblConfCustom);
						}
					}
				}

				for(TableConfig tblConf: compConfDefault.getTableConfigs()) {
					listModelCustom.addElement(tblConf);
				}

				saveConfigToDisk(new ArrayList<TableConfig>(Collections.list(listModelCustom.elements())), customConfig);

				for(int i = 0; i < listModelCustom.size(); i++) {
					TableConfig tblConf = listModelCustom.get(i);
					listModel.addElement(tblConf);
				}

				configList.setSelectedIndex(0);
			}
		});

		return configListLayout;
	}

	private CompetitionConfig getConfigFromFile(File file) {
		CompetitionConfig compConf = null;
		try (InputStream jsonStream = new FileInputStream(file)) {
			compConf = mapper.readValue(jsonStream, CompetitionConfig.class);
			if (compConf != null && compConf.getTableConfigs() != null && !compConf.getTableConfigs().isEmpty()) {
				System.out.println("Loaded table configurations from file: " + file);
			}
		} catch (FileNotFoundException e) {
			// do nothing
		} catch (IOException e) {
			e.printStackTrace();
		}
		return compConf;
	}

	private CompetitionConfig getConfigFromInputStream(InputStream stream) {
		CompetitionConfig compConf = null;
		try {
			compConf = mapper.readValue(stream, CompetitionConfig.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return compConf;
	}
	private void saveConfigToDisk(List<TableConfig> cfgs, File path) {
		try (OutputStream jsonStream = new FileOutputStream(path)) {
			if (jsonStream != null) {
				CompetitionConfig compConf = new CompetitionConfig(cfgs, null);
				mapper.writeValue(jsonStream, compConf);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	private TableConfig getTableConfigFromForm() {
		return new TableConfig(
				tableConfigNameTextField.getText(),
				(Integer) initialMoneySpinner.getValue(),
				(Integer) minBetSpinner.getValue(),
				(Integer) maxBetSpinner.getValue(),
				(Integer) numDecksSpinner.getValue(),
				(Integer) maxRoundsSpinner.getValue(),
				(Integer) deckNumberSpinner.getValue()
		);
	}
	private TableConfig getTableConfigFromFormCustomName(String name) {
		return new TableConfig(
				name,
				(Integer) initialMoneySpinner.getValue(),
				(Integer) minBetSpinner.getValue(),
				(Integer) maxBetSpinner.getValue(),
				(Integer) numDecksSpinner.getValue(),
				(Integer) maxRoundsSpinner.getValue(),
				(Integer) deckNumberSpinner.getValue()
		);
	}
	private void loadTableConfigInForm(TableConfig cfg){
		tableConfigNameTextField.setText(cfg.getName());
		minBetValue.setText(String.valueOf(cfg.getMinBet()));
		maxBetValue.setText(String.valueOf(cfg.getMaxBet()));
		startingCashValue.setText(String.valueOf(cfg.getInitialMoney()));
		numDecksValue.setText(String.valueOf(cfg.getNumDecks()));
		maxRoundsValue.setText(String.valueOf(cfg.getNumRounds()));
		seedValue.setText(String.valueOf(cfg.getDeckNumber()));
	}

	private void loadTableConfigInFormOriginal(int idx){
		loadedTableConfigIndex = idx;
		TableConfig cfg = configList.getModel().getElementAt(idx);
		tableConfigNameTextField.setText(cfg.getName());
		minBetSpinner.setValue(cfg.getMinBet());
		maxBetSpinner.setValue(cfg.getMaxBet());
		initialMoneySpinner.setValue(cfg.getInitialMoney());
		numDecksSpinner.setValue(cfg.getNumDecks());
		maxRoundsSpinner.setValue(cfg.getNumRounds());
		deckNumberSpinner.setValue(cfg.getDeckNumber());
	}

	// // old version
	// private void loadTableConfigInForm(int idx){
	// 	loadedTableConfigIndex = idx;
	// 	TableConfig cfg = configList.getModel().getElementAt(idx);
	// 	tableConfigNameTextField.setText(cfg.getName());
	// 	minBetSpinner.setValue(cfg.getMinBet());
	// 	maxBetSpinner.setValue(cfg.getMaxBet());
	// 	initialMoneySpinner.setValue(cfg.getInitialMoney());
	// 	numDecksSpinner.setValue(cfg.getNumDecks());
	// 	maxRoundsSpinner.setValue(cfg.getNumRounds());
	// 	deckNumberSpinner.setValue(cfg.getDeckNumber());
	// }

	public JPanel createExitLayout() {
		JPanel exitGamePanel = new JPanel();
		exitGamePanel.setLayout(new GridLayout(1, 1, 0, 0));
		exitGamePanel.setBounds(730, 100, 120, 120);

		// TODO - GameButton needs an image?
		GameButton quitButton = new GameButton("Quit", 730, 100);
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});


		exitGamePanel.add(quitButton);
		return exitGamePanel;
	}

	private CasinoRules createCasinoRules() {
		return Blackjack.isEggsActlyRight() ?
				new OrdinaryBlackjackRules() :
				new RochesterMnCasinoRules();
	}

}
