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
package com.ibm.vie.blackjack.casino.exceptions;

/**
 * This exception will be thrown if an invalid bet is placed. For example if a player attempts more
 * money than he/she has or if a player attempts to split or double and does not have the funds to
 * cover the action.
 *
 * @author ntl
 *
 */
public class InvalidBetException extends BlackjackRuleViolationException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;


  /**
   *
   * @param message - a specific message documenting the reason for the error
   */
  public InvalidBetException(final String message) {
    super(message);
  }

}
