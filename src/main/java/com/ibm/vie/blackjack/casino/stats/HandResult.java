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

/**
 * Class that represents any important information that must be tracked about a hand for statistics.
 * 
 * @author ntl
 *
 */
public class HandResult {
  private boolean wasDoubleDown = false;
  private HandResultCategory resultStat = null;
  private int earnings;

  public HandResult() {

  }

  /**
   * Set whether or not this hand was a doubled down hand
   * 
   * @param wasDoubleDown
   */
  public void setWasDoubleDown(boolean wasDoubleDown) {
    this.wasDoubleDown = wasDoubleDown;
  }

  /**
   * 
   * @return true if the hand involved a double down decision
   * 
   */
  public boolean getWasDoubleDown() {
    return wasDoubleDown;
  }

  /**
   * 
   * @param resultStat - the outcome category of the hand
   * 
   */
  public void setOutcomeStat(HandResultCategory resultStat) {
    this.resultStat = resultStat;
  }

  /**
   * Amount of money earned for the round.
   * 
   * <p>
   * This is the payout minus the bet paid for the hand. It may be positive, negative, or 0.
   * </p>
   * 
   * @param earnings
   */
  public void setEarnings(int earnings) {
    this.earnings = earnings;
  }


  /**
   * Amount of money earned in the round
   * 
   * <p>
   * This is the payout minus the bet paid for the hand. It may be positive, negative, or 0.
   * </p>
   * 
   * @return total amount of money earned
   */
  public int getEarnings() {
    return this.earnings;
  }

  /**
   * 
   * @return the outcome category of the hand
   */
  public HandResultCategory getResultStat() {
    return this.resultStat;
  }


}
