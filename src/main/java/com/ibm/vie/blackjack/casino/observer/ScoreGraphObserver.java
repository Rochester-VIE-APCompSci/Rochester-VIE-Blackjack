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
package com.ibm.vie.blackjack.casino.observer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHandPayout;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.TableRules;

/**
 * Real time graph for available money. This is just for demo, the result is of very poor quality
 *
 * The observer updates the graph at the end of each round
 *
 * @author ntl
 *
 */
public class ScoreGraphObserver implements TableObserver {

  /**
   * Static graphical component to plot score in real time
   *
   * @author ntl
   *
   */
  public static class PlotScore extends JPanel {
    final static int PAD = 30;

    /**
     * This is the maximum number of points that will be graphed, also the max number of prior
     * rounds that will be plotted.
     */
    final int maxSize;
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Returns a plotter for scores
     *
     * @param rules - table rules used to determine the initial available money
     *
     * @return score plotter
     */
    public static final PlotScore getPloter(final TableRules rules, final int maxSize) {
      final JFrame f = new JFrame();
      final PlotScore scorer = new PlotScore(rules, maxSize);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.getContentPane().add(scorer);
      f.setSize(400, 400);
      f.setPreferredSize(new Dimension(1500, 700));
      f.setLocation(200, 200);
      f.pack();
      f.setVisible(true);
      return scorer;

    }

    int[] data = new int[1];
    final TableRules rules;

    int startPos = 0;

    /**
     * Constructor takes the rules so that a line can be drawn for the initial money level
     * 
     * @param rules
     */
    public PlotScore(final TableRules rules, final int maxSize) {
      this.rules = rules;
      this.maxSize = maxSize;
    }

    /**
     *
     * @return the max score in the data
     */
    private int getMaxScore() {
      int max = -9999;
      for (int i = 0; i < data.length; i++) {
        if (data[i] > max) {
          max = data[i];
        }
      }
      return max;
    }


    /**
     * Paint the graph
     *
     */
    @Override
    protected void paintComponent(final Graphics g) {
      super.paintComponent(g);
      final Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      final int maxScore = getMaxScore();

      final int width = getWidth();
      final int height = getHeight();
      g2.setPaint(Color.gray);
      g2.drawLine(PAD, PAD, PAD, height - PAD);
      g2.drawLine(PAD, height - PAD, width - PAD, height - PAD);
      final double xScale = (width - 2 * PAD) / ((double) maxSize);
      final double maxValue = Math.max(maxScore, rules.getInitialMoney() * 4);
      final double yScale = (height - 2 * PAD) / maxValue;
      // The origin location.
      final int x0 = PAD;
      final int y0 = height - PAD;

      g2.setPaint(Color.green);
      g2.drawLine(x0, (int) Math.ceil(y0 - yScale * rules.getInitialMoney()), width - PAD,
          (int) Math.ceil(y0 - yScale * rules.getInitialMoney()));


      // plot the points
      for (int j = 0; j < data.length; j++) {
        final int x = x0 + (int) (xScale * (j + 1));
        final int y = y0 - (int) (yScale * data[j]);
        if (j > 0) {
          if (data[j - 1] > data[j]) {
            g2.setPaint(Color.red);
          } else if (data[j - 1] < data[j]) {
            g2.setPaint(Color.green);
          } else {
            g2.setPaint(Color.blue);
          }
        } else {
          g2.setPaint(Color.gray);
        }
        g2.fillOval(x - 2, y - 2, 4, 4);
      }

      g2.setPaint(Color.gray);

      final FontMetrics fm = g2.getFontMetrics();

      // plot y axis
      for (int i = 0; i < maxValue; i += maxValue / ((height - 2 * PAD) /  fm.getAscent() )) {
        final String value = Integer.toString(i);
        g2.drawString(value, x0 - fm.stringWidth(value),
            y0 - (int) (i * yScale) + fm.getAscent() / 2);
      }

      // plot x axis
      final int xInterval = maxSize / ((width - 2 * PAD) / (3 * fm.getMaxAdvance()));
      for (int xt = 0; xt < maxSize; xt += xInterval) {
        final String value = Integer.toString(xt + startPos);
        g2.drawString(value, x0 + (int) (xt * xScale), y0 + PAD - fm.getMaxAscent());

      }
    }


    /**
     * Update the plot with new scores. Only the last {@link ScoreGraphObserver#maxSize} scores will
     * be plotted.
     *
     * @param newScores
     */
    void updateScores(final List<Integer> newScores) {
      final int size = Math.min(newScores.size(), maxSize);
      startPos = Math.max(newScores.size() - size, 0);
      data = new int[size];
      for (int i = 0; i < size; i++) {
        data[i] = newScores.get(startPos + i);
      }

      repaint();
    }

  }



  private final List<Integer> points = new LinkedList<>();


  private PlotScore scoreGraph;

  /**
   *
   */
  public ScoreGraphObserver() {
    scoreGraph = null;
  }



  /**
   * Redraw the graph at the end of each round
   */
  @Override
  public void observeEndOfRound(final GameInfo gameInfo, final VieDealerHand vieDealerHand,
      final List<ViePlayerHandPayout> hands) {

    // if first round, build the plotter
    if (scoreGraph == null) {
      scoreGraph = ScoreGraphObserver.PlotScore.getPloter(gameInfo.getTableRules(),
          gameInfo.getTableRules().getMaxNumRounds());
    }

    // add the latest point score
    points.add(gameInfo.getAvailableMoney());

    if (points.size() % 20 == 0 || gameInfo.getAvailableMoney() < gameInfo.getMinBet()) {
      scoreGraph.updateScores(points);
      // small delay to make things readable
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        // ignore interrupts
      }
      
    }

   

  }



}
