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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.plaf.metal.MetalButtonUI;


@SuppressWarnings("serial")
public class ScreenNormalGame extends Screen {

		private GameButton stepButton;
		private JSlider speedSlider;

		private JPanel menuButtons;
		private JPanel controlPanel;
		protected JPanel dealerCards;
		protected JPanel playerCards;
		protected JPanel playerTotalMoney;
		protected JPanel roundNumber;
		protected JPanel gameStatus;
		protected Object threadWaitObject = null;
		private boolean bStepMode = true;
		private Blackjack blackjack;

		public ScreenNormalGame(CardLayout screenManager, JPanel screens, Blackjack blackjack) {
			super(screenManager, screens);
			this.blackjack = blackjack;
			init();
		}

		private void init() {
			playerCards = new JPanel();
			playerTotalMoney = new JPanel();
			roundNumber = new JPanel();
			gameStatus = new JPanel();
			createLayout();
		}

		public void update() {
			this.removeAll();

			this.setLayout(null);

			this.add(menuButtons);
			this.add(controlPanel);
			this.add(dealerCards);
			this.add(playerCards);
			this.add(playerTotalMoney);
			this.add(roundNumber);
			this.add(gameStatus);

			this.redraw();
		}

		private void createLayout() {
			this.setLayout(null);

			try {
				menuButtons = createMenuButtons();
				controlPanel = createControlPanel();
				dealerCards = createBlankDealerCards();
				this.add(menuButtons);
				this.add(controlPanel);
				this.add(dealerCards);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// any/all menu buttons primarily to return to the main menu.
		private JPanel createMenuButtons() {

			JPanel menuGamePanel = new JPanel();
			menuGamePanel.setLayout(new GridLayout(1, 1, 0, 0));
			menuGamePanel.setBounds(50, 25, 250, 40);

			GameButton menuButton = new GameButton("<html>&#x2B05;  Return to Main Menu</html>", 730, 100);
			menuButton.addActionListener(super.getScreenAction("Menu"));
			menuButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GameThread currentThread = blackjack.gameThreads.poll();
					blackjack.uiFinished = true;
					currentThread.endEarly();
				}
			});

			menuButton.setUI(new MetalButtonUI());

			menuGamePanel.add(menuButton);
			return menuGamePanel;
		}
		
		public void pauseThread(Object threadWaitObject) {
			if (bStepMode) {
				try {
					this.threadWaitObject = threadWaitObject;
					synchronized (threadWaitObject) {
						stepButton.setEnabled(true); // can't continue twice
						threadWaitObject.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					int speed = speedSlider.getValue();
					long wait = (100 - speed) * 10 + 10;
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private JPanel createControlPanel() {
			JPanel controlPanel = new JPanel();
			controlPanel.setLayout(new BorderLayout());
			controlPanel.setBounds(25, 525, 250, 75);

			stepButton = new GameButton("Step");
			stepButton.setEnabled(false);
			JButton playButton = new GameButton("Start");
			JLabel speedLabel = new JLabel("Speed");
			speedLabel.setLabelFor(speedSlider);
			speedSlider = new JSlider(0, 100);

			stepButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					synchronized (threadWaitObject) {
						threadWaitObject.notify(); // Wake up game thread
						stepButton.setEnabled(false);
					}
				}
			});
			
			playButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					bStepMode = !bStepMode;
					stepButton.setEnabled(bStepMode);
					playButton.setText(bStepMode ? "Start" : "Pause");
					if (!bStepMode) {
						synchronized (threadWaitObject) {
							threadWaitObject.notify();							
						}
					}
				}
			});

			stepButton.setUI(new MetalButtonUI());
			playButton.setUI(new MetalButtonUI());
			
			JPanel buttonPanel = new JPanel(new FlowLayout());
			JPanel speedPanel = new JPanel(new BorderLayout());

			buttonPanel.add(playButton);
			buttonPanel.add(stepButton);
			speedPanel.add(speedLabel, BorderLayout.WEST);
			speedPanel.add(speedSlider, BorderLayout.CENTER);
			
			controlPanel.add(buttonPanel, BorderLayout.NORTH);
			controlPanel.add(speedPanel, BorderLayout.SOUTH);
			
			return controlPanel;
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
}
