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

import java.util.List;
import com.ibm.vie.blackjack.casino.observer.GameResultObserver;
import com.ibm.vie.blackjack.player.TableRules;

/**
 * The result of a game, including a round by round breakdown of outcomes
 *
 * @see GameResultObserver
 * @see GameResultStatCalculator
 *
 * @author ntl
 *
 */
public class GameResult {
  private final TableRules rules;
  private final int finalMoney;
  private final Exception gameEndingException;
  private final List<RoundResult> roundResults;
  private final int roundsPlayed;
  private final String studentName;

  /**
   * Builds a game result
   *
   * @param finalMoney final amount of money at the end of the game
   * @param roundsPlayed total number of rounds played in the game
   * @param outcomes list of round results
   * @param rules the table rules
   * @param gameEndingException an exception that ended the game, may be null if not exception
   * @param studentName name of the student that owns the strategy
   */
  public GameResult(final int finalMoney, final int roundsPlayed, final List<RoundResult> outcomes,
      final TableRules rules, final Exception gameEndingException, final String studentName) {
    this.finalMoney = finalMoney;
    this.roundsPlayed = roundsPlayed;
    roundResults = outcomes;
    this.rules = rules;
    this.gameEndingException = gameEndingException;
    this.studentName = studentName;
  }

  /**
   * The table rules for which this game was played
   *
   * @return table rules
   */
  public TableRules getRules() {
    return rules;
  }

  /**
   * returns the amount of money at the end of the game
   *
   * @return available money when the game ended
   */
  public int getFinalMoney() {
    return finalMoney;
  }

  /**
   * Check whether an exception ended the game
   *
   * @return true if an error caused the end of the game, false if the game ended normally
   */
  public boolean getGameEndedInError() {
    return gameEndingException != null;
  }

  /**
   * returns the exception that ended the game
   *
   * @return an exception, or null if the game ended normally
   */
  public Exception getGameEndingException() {
    return gameEndingException;
  }

  /**
   * Returns the game ending error message, or "OK" if there was no error
   * 
   * @return
   */
  public String getErrorMessage() {
    if (gameEndingException == null) {
      return "OK";
    } else {
      String msg = gameEndingException.getMessage();
      if (msg == null || msg.isEmpty()) {
        return gameEndingException.getClass().getName();
      } else {
        return msg;
      }
    }
  }
  
  /**
   * returns a list of details about each round
   *
   * @return list of round results
   */
  public List<RoundResult> getRoundResults() {
    return roundResults;
  }

  /**
   * returns number of rounds played
   *
   * @return number of rounds played when the game ended
   */
  public int getRoundsPlayed() {
    return roundsPlayed;
  }


  /**
   * returns the name of the student that played this game
   *
   * @return the name of the student playing the game
   */
  public String getStudentName() {
    return studentName;
  }
}
