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

import org.junit.Assert;
import org.junit.Test;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.card.Rank;
import com.ibm.vie.blackjack.casino.card.Suit;
import com.ibm.vie.blackjack.casino.hand.VieHand;


public class HandTest {

  @Test
  public void when_blackjack_then_is_blackjack_returns_true() {
    final VieHand blackjack =
        new VieHand(new VieCard(Rank.ACE, Suit.HEARTS), new VieCard(Rank.TEN, Suit.DIAMONDS));

    Assert.assertTrue(blackjack.isBlackJack());
  }

  @Test
  public void when_not_blackjack_then_is_blackjack_returns_false() {
    final VieHand notBlackjack1 =
        new VieHand(new VieCard(Rank.TEN, Suit.HEARTS), new VieCard(Rank.TEN, Suit.DIAMONDS));

    Assert.assertTrue(!notBlackjack1.isBlackJack());
    Assert.assertTrue("Busted value not correct", !notBlackjack1.isBusted());

    final VieHand notBlackjack2 = new VieHand(new VieCard(Rank.TEN, Suit.HEARTS),
        new VieCard(Rank.TEN, Suit.DIAMONDS), new VieCard(Rank.ACE, Suit.SPADES));

    Assert.assertTrue(!notBlackjack2.isBlackJack());
    Assert.assertTrue("Busted value not correct", !notBlackjack2.isBusted());
  }


  @Test
  public void when_no_aces_then_score_is_correct() {
    final VieHand myHand =
        new VieHand(new VieCard(Rank.TEN, Suit.HEARTS), new VieCard(Rank.TEN, Suit.DIAMONDS));

    Assert.assertTrue(myHand.getScoreAceAs1() == 20);
    Assert.assertTrue(myHand.getScore() == 20);
    Assert.assertTrue("Busted value not correct", !myHand.isBusted());

    final VieHand myHand2 =
        new VieHand(new VieCard(Rank.SEVEN, Suit.HEARTS), new VieCard(Rank.QUEEN, Suit.DIAMONDS));

    Assert.assertTrue(myHand2.getScoreAceAs1() == 17);
    Assert.assertTrue(myHand2.getScore() == 17);
    Assert.assertTrue("Busted value not correct", !myHand2.isBusted());


    final VieHand myHand3 = new VieHand(new VieCard(Rank.SEVEN, Suit.HEARTS),
        new VieCard(Rank.KING, Suit.DIAMONDS), new VieCard(Rank.JACK, Suit.SPADES));

    Assert.assertTrue(myHand3.getScoreAceAs1() == 27);
    Assert.assertTrue(myHand3.getScore() == 27);
    Assert.assertTrue("Busted value not correct", myHand3.isBusted());

  }

  @Test
  public void when_soft_points_exceed_21_then_hard_value_is_used() {
    final VieHand myHand = new VieHand(new VieCard(Rank.TEN, Suit.DIAMONDS), new VieCard(Rank.TEN, Suit.SPADES),
        new VieCard(Rank.ACE, Suit.HEARTS));

    Assert.assertTrue(myHand.getScoreAceAs1() == 21);
    Assert.assertTrue(myHand.getScore() == 21);
    Assert.assertTrue("Busted value not correct", !myHand.isBusted());
  }


  @Test
  public void when_soft_points_under_21_then_soft_value_is_used() {
    final VieHand myHand =
        new VieHand(new VieCard(Rank.TWO, Suit.DIAMONDS), new VieCard(Rank.FIVE, Suit.SPADES),
            new VieCard(Rank.ACE, Suit.CLUBS), new VieCard(Rank.THREE, Suit.HEARTS));

    Assert.assertTrue("Hard Score was " + myHand.getScoreAceAs1(), myHand.getScoreAceAs1() == 11);
    Assert.assertTrue("Soft Score was " + myHand.getScore(), myHand.getScore() == 21);
    Assert.assertTrue("Busted value not correct", !myHand.isBusted());

    final VieHand myHand2 =
        new VieHand(new VieCard(Rank.ACE, Suit.DIAMONDS), new VieCard(Rank.FIVE, Suit.SPADES),
            new VieCard(Rank.ACE, Suit.CLUBS), new VieCard(Rank.THREE, Suit.HEARTS));

    Assert.assertTrue("Hard Score was " + myHand2.getScoreAceAs1(), myHand2.getScoreAceAs1() == 10);
    Assert.assertTrue("Soft Score was " + myHand2.getScore(), myHand2.getScore() == 20);
    Assert.assertTrue("Busted value not correct", !myHand.isBusted());
  }


 @Test
 public void when_hand_exceeds_21_then_is_busted() {
   final VieHand myHand =
       new VieHand(new VieCard(Rank.TEN, Suit.DIAMONDS), new VieCard(Rank.TEN, Suit.SPADES), new VieCard(Rank.TWO, Suit.DIAMONDS));

   Assert.assertTrue(myHand.isBusted());
 }



}
