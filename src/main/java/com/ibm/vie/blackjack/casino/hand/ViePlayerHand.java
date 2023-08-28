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
package com.ibm.vie.blackjack.casino.hand;

import java.util.LinkedList;
import java.util.List;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.player.PlayerHand;

/**
 * Represents an extension of hand which adds information about the context of a player's hand in a
 * game.
 *
 * @author ntl
 *
 */
public class ViePlayerHand extends VieHand {
  /**
   * The wager for this hand. Note that this could be higher than the original bet set by the user
   * in the case of a double down.
   */
  private final int betPaid;
  /**
   * Indicator for whether this hand can take part in a hit using blackjack rules.
   *
   * A false value could occur because the user stood, doubled down, hit 21 exactly or busted.
   */
  private final boolean rulesAllowHit;


  /**
   * Creates a hand that can be used in a hit to create a new hand, if and only if the cards allow
   * for a hit.
   *
   * @param betPaid
   * @param cards
   */
  public ViePlayerHand(final int betPaid, final VieCard... cards) {
    super(cards);
    this.betPaid = betPaid;
    rulesAllowHit = checkIfCardsAllowHit();
  }


  /**
   * Creates a new hand with the specified bet. Using this hand to create a new hand with additional
   * cards is allowed only if the rulesAllowHit param is true and if the cards allow for a hit.
   * (rulesAllowHit might be false if the player has STAND or DOUBLE DOWN)
   *
   * @param betPaid
   * @param rulesAllowHit
   * @param cards
   */
  public ViePlayerHand(final int betPaid, final boolean rulesAllowHit, final VieCard... cards) {
    super(cards);
    this.betPaid = betPaid;
    this.rulesAllowHit = rulesAllowHit && checkIfCardsAllowHit();
  }


  /**
   * Constructs a hand from a different hand A copy of a hand doesn't make much sense since hands
   * with the same cards are considered different if they are different objects.
   *
   * This can be used by child classes to convert a PlayerHand into some other type.
   *
   * @param other the hand to copy
   */
  protected ViePlayerHand(final ViePlayerHand other) {
    super(other.getCards().toArray(new VieCard[0]));
    betPaid = other.betPaid;
    rulesAllowHit = other.rulesAllowHit;
  }

  /**
   * Does the context in which this hand exists and cards allow for a HIT?
   *
   * @return whether or not blackjack rules allow the player to hit on this hand.
   *
   */
  public boolean getRulesAllowHit() {
    return rulesAllowHit;
  }

  /**
   *
   * @return the bet that was paid for this hand
   */
  public int getBetPaid() {
    return betPaid;
  }


  /**
   * Do the rules of blackjack allow this hand to be split into two hands?
   *
   * @return
   */
  public boolean rulesAllowSplit() {
    return rulesAllowHit && (getCards().size() == 2)
        && getCards().get(0).getRank().equals(getCards().get(1).getRank());
  }


  /**
   * Do the rules of blackjack allow this hand to be doubled down?
   *
   * @return
   */
  public boolean rulesAllowDoubleDown() {
    return (rulesAllowHit && getCards().size() == 2);
  }


  /**
   *
   * @return true if the cards alone would allow a hit
   *
   *         Hitting is not allowed if the value of the cards is greater than or equal to 21
   */
  private final boolean checkIfCardsAllowHit() {
    return (super.getScore() < 21) && (super.getScoreAceAs1() < 21);
  }


  /**
   * Player accessible Hand
   *
   * @return Information about the hand to be used by the player's solution
   */
  public PlayerHand toPlayerHand() {
    return new PlayerHand(betPaid, this.getScoreAceAs1(), this.getScore(),
        VieCard.toCardList(this.getCards()));
  }

  /**
   * List of player accessible hands from a list of PlayerHand
   *
   * @param hands the list of hands to convert
   */

  public static List<PlayerHand> toPlayerHandList(final List<ViePlayerHand> hands) {
    final List<PlayerHand> result = new LinkedList<>();
    for (final ViePlayerHand hand : hands) {
      result.add(hand.toPlayerHand());
    }
    return result;
  }

  /**
   * String representation of this object
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString());
    sb.append(" betPaid=" + this.getBetPaid() + ";");
    return sb.toString();
  }
}
