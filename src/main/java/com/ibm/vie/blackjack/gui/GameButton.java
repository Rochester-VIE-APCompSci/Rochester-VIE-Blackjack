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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;

public class GameButton extends JButton {

	private BufferedImage image;
	private int xLoc;
	private int yLoc;
	//private final static Border borderPadding = BorderFactory.createEmptyBorder(3, 5, 3, 5);
	private final static Border borderPadding = BorderFactory.createEmptyBorder(5, 10, 5, 10);
	private final static Border borderDefault = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), borderPadding);
	private final static Border borderHover = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red), borderPadding);
	private final static Border borderActive = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.yellow), borderPadding);

	// public GameButton() {
	// super();
	// init();
	// }
	public GameButton(String text) {
		super(text);
		initTextButton();
		setMargin(new Insets(3,5,3,5));
	}
	public GameButton(String text, int x, int y) {
		super(text);
		this.xLoc = x;
		this.yLoc = y;
		this.setPreferredSize(new Dimension(100, 200));
		this.setBounds(new Rectangle(xLoc, yLoc, 100, 100));
		initTextButton();
	}

	public GameButton(BufferedImage image, int x, int y) {
		super(new ImageIcon(image));
		this.image = image;
		this.xLoc = x;
		this.yLoc = y;
		this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		this.setBounds(new Rectangle(xLoc, yLoc, image.getWidth(), image.getHeight()));
		initImageButton();
	}

	private void initImageButton() {
		this.setBorder(borderDefault);
		this.setBackground(Color.black);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				setBorder(borderHover);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBorder(borderDefault);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				setBorder(borderActive);
			}
		});
	}

	private void initTextButton() {
		this.setContentAreaFilled(true);
		this.setBorder(borderDefault);
		this.setBackground(Color.LIGHT_GRAY);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				setBorder(borderHover);
				setBackground(Color.green);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBorder(borderDefault);
				setBackground(Color.LIGHT_GRAY);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				setBorder(borderActive);
			}
		});
	}

}
