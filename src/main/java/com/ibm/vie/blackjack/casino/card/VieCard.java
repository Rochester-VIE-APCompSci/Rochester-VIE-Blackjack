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
package com.ibm.vie.blackjack.casino.card;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import com.ibm.vie.blackjack.player.Card;

/**
 * Represents a card in the blackjack game. This class makes use of advanced features such as enums,
 * although students can use this class, it is expected that they will make use of the
 * {@link com.ibm.vie.blackjack.player.Card} class, since this class is similar to ones
 * that they are expected to have worked with before.
 *
 * @author ntl
 *
 */
public class VieCard {
  private final Suit suit;
  private final Rank rank;
  private final Card playerCard;
  
  /**
   * Creates a new instance of a playing card Suit and Rank must be non-null values
   *
   * @param rank
   * @param suit
   */
  public VieCard(final Rank rank, final Suit suit) {
    this.rank = rank;
    this.suit = suit;
    this.playerCard = new Card(this.rank.toString().toLowerCase(), this.suit.toString().toLowerCase(), 
        this.rank.getRankMinScore(), this.rank.getRankMaxScore());
  }

  /**
   *
   * @return - rank of the card
   *
   */
  public Rank getRank() {
    return rank;
  }

  /**
   *
   * @return suit of the card
   *
   */
  public Suit getSuit() {
    return suit;
  }


  /**
   * Compute a hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(suit, rank);
  }

  /**
   * Test for equality
   */
  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    } else {
      if (other instanceof VieCard) {
        return suit.equals(((VieCard) other).suit) && rank.equals(((VieCard) other).rank);
      } else {
        return false;
      }
    }
  }

  /**
   * Human readable description of the card
   */
  @Override
  public String toString() {
    return "\"" + rank.toString() + " of " + suit.toString() + "\"";
  }


  /**
   * creates player accessible version
   * 
   * @return information about this card
   */
  public Card toCard() {
    return this.playerCard;
  }

  /**
   * Creates a list of {@link Card} from a list of {@link VieCard}
   * 
   * @param vieCards list of cards
   * @return list of card information objects
   */
  public static List<Card> toCardList(List<VieCard> vieCards) {
    List<Card> result = new LinkedList<>();
    for (VieCard vieCard : vieCards) {
      result.add(vieCard.toCard());
    }
    return Collections.unmodifiableList(result);
  }
  
  
}
