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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.card.Rank;

/**
 * A blackjack hand consists of one or more cards. A hand might only have one card if we decided to
 * play the European version of the game where there is no "hole" card for the dealer.
 *
 * Hands are immutable in that once constructed, they may not modified. When a player or dealer
 * "hits" a new hand is constructed from the existing hand and the additional card.
 *
 * The point value of a hand may exceed 21 if a player or dealer has "busted".
 *
 * @author ntl
 *
 */
public class VieHand {
  private final List<VieCard> vieCards;
  /**
   * Construct a hand from an array of cards
   *
   * @param initialCards
   */
  public VieHand(final VieCard... initialCards) {
    final List<VieCard> newHand = new LinkedList<>();
    newHand.addAll(Arrays.asList(initialCards));
    vieCards = Collections.unmodifiableList(newHand);
  }
  
  /**
   * Construct a hand from a List of cards
   *
   * @param initialCards
   */
  public VieHand(final List<VieCard> initialCards) {
    final List<VieCard> newHand = new LinkedList<>();
    newHand.addAll(initialCards);
    vieCards = Collections.unmodifiableList(newHand);
  }

  /**
   *
   * @return an unmodifiable list of cards in the hand
   */
  public List<VieCard> getCards() {
    return vieCards;
  }


  /**
   * This score means that an ace is always counted as 1, even if the rules of the game allow the
   * ace to be counted as 11.
   *
   * @return the score of the hand, with Aces being counted as 1
   */
  public int getScoreAceAs1() {
    int score = 0;
    for (final VieCard vieCard : vieCards) {
      score += vieCard.getRank().getRankMinScore();
    }

    return score;
  }

  /**
   * The score means that an ace is counted as 11, unless that value would cause the score to exceed
   * 21, in which case the ace is counted as 1.
   *
   * If there are no aces in the hand, the behavior of this method is the same as the
   * {@link #getScoreAceAs1()} method.
   *
   * @return - the max score for the hand
   */
  public int getScore() {
    /*
     * There is an implicit assumption here that only aces have a different maximum score, and that
     * the max score is a large enough value (11) such that there can only be one ace in a hand that
     * uses the max score.
     */
    boolean softAce = false;
    int score = 0;
    for (final VieCard vieCard : vieCards) {
      if (vieCard.getRank() == Rank.ACE && !softAce) {
        score += Rank.ACE.getRankMaxScore();
        softAce = true;
      } else {
        score += vieCard.getRank().getRankMinScore();
      }
    }

    if (softAce && score > 21) {
      return getScoreAceAs1();
    } else {
      return score;
    }
  }
  
  /**
   * This method returns the results of {@link #getScore()} as a String. If the results of 
   * {@link #getScoreAceAs1()} are different than {@link #getScore()}, that value is added to the 
   * result as well.
   * 
   * For example, if {@link #getScore()} returned 17, and {@link #getScoreAceAs1()} returned 7, this
   * method would return "17 or 7". Otherwise it would have simply returned "17".
   *
   * @return - the max score for the hand
   */
  public String getScoreDescription() {
	  int softScore = getScore();
	  int hardScore = getScoreAceAs1();
	  return softScore == hardScore ? String.valueOf(softScore) : softScore + " or " + hardScore;
  }


  /**
   *
   * @return true if this hand is a blackjack
   */
  public boolean isBlackJack() {
    if (vieCards.size() != 2) {
      return false;
    } else {
      return getScore() == 21;
    }

  }


  /**
   *
   * @return true if the had is a bust
   */
  public boolean isBusted() {
    return getScore() > 21;
  }

  /**
   * Readable string that represents the hand
   *
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("cards={");
    sb.append(vieCards.stream().map(Object::toString).collect(Collectors.joining(",")).toString());
    sb.append("}; score=");
    if (this.getScoreAceAs1() != this.getScore()) {
      sb.append("" + this.getScoreAceAs1() + "/");
    }
    sb.append("" + this.getScore());
    sb.append(";");
    return sb.toString();
  }

}
