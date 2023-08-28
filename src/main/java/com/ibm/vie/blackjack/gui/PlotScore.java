package com.ibm.vie.blackjack.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.ibm.vie.blackjack.player.TableRules;

/**
 * Static graphical component to plot score in real time
 *
 * @author ntl
 *
 */
public class PlotScore extends JPanel {
	int startPos = 0;
	ArrayList<Integer> data = new ArrayList<Integer>();

	final int PAD;
	final int maxSize;
	final TableRules rules;

	protected StatsMultiLoadingScreen statsMultiLoadingScreen;
	protected StatsLoadingScreen statsLoadingScreen;

	public PlotScore(final TableRules rules, final int maxSize, StatsLoadingScreen statsLoadingScreen) {
		this.statsLoadingScreen = statsLoadingScreen;
		this.rules = rules;
		this.maxSize = maxSize;
		this.PAD = 65;
	}

	public PlotScore(final TableRules rules, final int maxSize, StatsMultiLoadingScreen statsMultiLoadingScreen) {
		this.statsMultiLoadingScreen = statsMultiLoadingScreen;
		this.rules = rules;
		this.maxSize = maxSize;
		this.PAD = 40;
	}

	private int getMaxScore() {
		int max = -9999;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) > max) {
				max = data.get(i);
			}
		}
		return max;
	}

	@Override
	synchronized protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		
		int maxAdvance = 1;
		
		final Graphics2D g2 = (Graphics2D) g;
		final int maxScore = getMaxScore();
		final int width = getWidth();
		final int height = getHeight();
		final double xScale = (width - 2 * PAD) / ((double) maxSize);
		final double maxValue = Math.max(maxScore, rules.getInitialMoney() * 4);
		final double yScale = (height - 2 * PAD) / maxValue;
		final int x0 = PAD;
		final int y0 = height - PAD;
		final int graphedPointsRatio = (int) Math.max(Math.ceil(maxSize / width), 1);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(Color.gray);
		g2.drawLine(PAD, PAD, PAD, height - PAD);
		g2.drawLine(PAD, height - PAD, width - PAD, height - PAD);
		g2.setPaint(Color.green);
		g2.drawLine(x0, (int) Math.ceil(y0 - yScale * rules.getInitialMoney()), width - PAD, (int) Math.ceil(y0 - yScale * rules.getInitialMoney()));

		int lastGraphedPointValue = -1;
		
		for (int i = 0; i < data.size(); i += graphedPointsRatio) {
			final int x = x0 + (int) (xScale * (i + 1));
			final int y = y0 - (int) (yScale * data.get(i));

			if (i > 0) {
				if (data.get(i) < lastGraphedPointValue) {
					g2.setPaint(Color.red);
				} else if (data.get(i) > lastGraphedPointValue) {
					g2.setPaint(Color.green);
				} else {
					g2.setPaint(Color.blue);
				}
			} else {
				g2.setPaint(Color.gray);
			}

			lastGraphedPointValue = data.get(i);
			g2.fillOval(x - 2, y - 2, 4, 4);
		}

		g2.setPaint(Color.gray);
		
		final FontMetrics fm = g2.getFontMetrics();

		for (int i = 0; i < maxValue; i += Math.max(maxValue / ((height - 2 * PAD) / fm.getAscent()), 1)) {
			final String value = Integer.toString(i);
			g2.drawString(value, x0 - fm.stringWidth(value), y0 - (int) (i * yScale) + fm.getAscent() / 2);
		}

		if (fm.getMaxAdvance() > 0) {
			maxAdvance = fm.getMaxAdvance();
		}
		
		int widthPad = (getWidth() - 2 * PAD) / (3 * maxAdvance);

		if (widthPad <= 0) {
			widthPad = 1;
		}
		
		final int xInterval = maxSize / widthPad;
		
		for (int xt = 0; xt < maxSize; xt += Math.max(xInterval, 1)) {
			final String value = Integer.toString(xt + startPos);
			g2.drawString(value, x0 + (int) (xt * xScale), y0 + PAD - fm.getMaxAscent());
		}
	}

	synchronized void addScoreAndRepaint(int score) {
		data.add(score);
		repaint();
	}
}