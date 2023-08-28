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
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.casino.rules.OrdinaryBlackjackRules;
import com.ibm.vie.blackjack.casino.card.Rank;
import com.ibm.vie.blackjack.casino.card.Suit;
import com.ibm.vie.blackjack.player.CasinoRules;
import com.ibm.vie.blackjack.player.TableRules;

public class HandUtilTest {

 private final static VieCard [] blackJack = {new VieCard(Rank.ACE, Suit.CLUBS), new VieCard(Rank.QUEEN, Suit.DIAMONDS)};
 private final static VieCard [] seventeen =  {new VieCard(Rank.SEVEN, Suit.CLUBS), new VieCard(Rank.QUEEN, Suit.DIAMONDS)};
 private final static VieCard [] sixteen = {new VieCard(Rank.SIX, Suit.DIAMONDS), new VieCard(Rank.JACK, Suit.CLUBS)};
 private final static VieCard [] nonNatural21 = {new VieCard(Rank.SEVEN, Suit.CLUBS), new VieCard(Rank.QUEEN, Suit.DIAMONDS), new VieCard(Rank.FOUR, Suit.HEARTS)};
 private final static VieCard [] bust = {new VieCard(Rank.SEVEN, Suit.CLUBS), new VieCard(Rank.EIGHT, Suit.CLUBS), new VieCard(Rank.NINE, Suit.SPADES)};
 private final static VieCard [] sixteenAce11 = {new VieCard(Rank.ACE, Suit.CLUBS), new VieCard(Rank.FIVE, Suit.DIAMONDS)};
 private final static VieCard [] twentyAce1 = { new VieCard(Rank.ACE, Suit.DIAMONDS), new VieCard(Rank.EIGHT, Suit.SPADES), new VieCard(Rank.KING, Suit.HEARTS)};
 private final static VieCard [] ten = {new VieCard(Rank.FIVE, Suit.HEARTS), new VieCard(Rank.FIVE, Suit.CLUBS)};

 private final TableRules rules = new TableRules(new OrdinaryBlackjackRules());

 @Test
 public void when_only_dealer_has_blackjack_then_dealer_win() {
   VieDealerHand dealer = new VieDealerHand(blackJack);
   ViePlayerHand player = new ViePlayerHand(100, seventeen);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.DEALER_WIN);

 }

 @Test
 public void when_both_dealer_and_player_blackjack_then_push() {
   VieDealerHand dealer = new VieDealerHand(blackJack);
   ViePlayerHand player = new ViePlayerHand(1, blackJack);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.PUSH);
 }

 @Test
 public void when_only_player_has_blackjack_then_player_win_with_blackjack() {
   VieDealerHand dealer = new VieDealerHand(seventeen);
   ViePlayerHand player = new ViePlayerHand(1, blackJack);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.PLAYER_WIN_W_BLACKJACK);
 }

 @Test
 public void when_player_has_blackjack_and_dealer_has_21_non_natural_then_player_wins_with_blackjack() {
   VieDealerHand dealer = new VieDealerHand(nonNatural21);
   ViePlayerHand player = new ViePlayerHand(1, blackJack);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.PLAYER_WIN_W_BLACKJACK);
 }


 @Test
 public void when_both_have_blackjack_then_push() {
   VieDealerHand dealer = new VieDealerHand(blackJack);
   ViePlayerHand player = new ViePlayerHand(1, blackJack);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.PUSH);
 }

 @Test
 public void when_dealer_has_higher_score_then_dealer_wins() {
   VieDealerHand dealer = new VieDealerHand(seventeen);
   ViePlayerHand player = new ViePlayerHand(1, sixteen);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.DEALER_WIN);
 }

 @Test
 public void when_same_score_then_push() {
   VieDealerHand dealer = new VieDealerHand(seventeen);
   ViePlayerHand player = new ViePlayerHand(1, seventeen);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.PUSH);
 }


 @Test
 public void when_player_busts_then_dealer_wins() {
   VieDealerHand dealer = new VieDealerHand(seventeen);
   ViePlayerHand player = new ViePlayerHand(1, bust);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.DEALER_WIN);
 }


 @Test
 public void when_dealer_busts_then_player_wins() {
   VieDealerHand dealer = new VieDealerHand(bust);
   ViePlayerHand player = new ViePlayerHand(1, sixteen);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.PLAYER_WIN);
 }



 @Test
 public void when_player_has_higher_score_then_player_wins() {
   VieDealerHand dealer = new VieDealerHand(sixteen);
   ViePlayerHand player = new ViePlayerHand(1, seventeen);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.PLAYER_WIN);
 }

 @Test
 public void when_player_has_higher_score_with_soft_ace_then_player_wins() {
   VieDealerHand dealer = new VieDealerHand(sixteenAce11);
   ViePlayerHand player = new ViePlayerHand(1, twentyAce1);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.PLAYER_WIN);
 }

 @Test
 public void when_dealer_has_higher_score_with_soft_ace_then_dealer_wins() {
   VieDealerHand dealer = new VieDealerHand(sixteenAce11);
   ViePlayerHand player = new ViePlayerHand(1, ten);

   HandOutcome outcome = HandUtil.compareDealerHandWithPlayer(dealer, player);

   Assert.assertTrue(outcome == HandOutcome.DEALER_WIN);
 }


 @Test
 public void when_player_win_then_player_makes_money() {
   VieDealerHand dealer = new VieDealerHand(sixteenAce11);
   List<ViePlayerHand> player = Collections.singletonList(new ViePlayerHand(10, twentyAce1));

   List<ViePlayerHandPayout> payouts = HandUtil.calculatePayouts(player, dealer, rules);
   Assert.assertTrue(20 == payouts.stream().mapToInt(ViePlayerHandPayout::getPayout).sum());
 }


 @Test
 public void when_dealer_win_then_player_gets_zero_money() {
   VieDealerHand dealer = new VieDealerHand(nonNatural21);
   List<ViePlayerHand> player = Collections.singletonList(new ViePlayerHand(10, twentyAce1));


   List<ViePlayerHandPayout> payouts = HandUtil.calculatePayouts(player, dealer, rules);
   Assert.assertTrue(0 == payouts.stream().mapToInt(ViePlayerHandPayout::getPayout).sum());
 }


 @Test
 public void when_player_gets_blackjack_then_player_gets_more_money() {
   VieDealerHand dealer = new VieDealerHand(seventeen);
   List<ViePlayerHand> player = Collections.singletonList(new ViePlayerHand(10, blackJack));


   List<ViePlayerHandPayout> payouts = HandUtil.calculatePayouts(player, dealer, rules);
   Assert.assertTrue((10 + rules.getCompetitionRules().getBlackJackPayOut() * 10)== payouts.stream().mapToInt(ViePlayerHandPayout::getPayout).sum());

 }

 @Test
 public void when_push_then_payer_gets_bet_back() {
   VieDealerHand dealer = new VieDealerHand(seventeen);
   List<ViePlayerHand> player = Collections.singletonList(new ViePlayerHand(15, seventeen));

   List<ViePlayerHandPayout> payouts = HandUtil.calculatePayouts(player, dealer, rules);
   Assert.assertTrue(15 == payouts.stream().mapToInt(ViePlayerHandPayout::getPayout).sum());

 }

 @Test
 public void when_push_and_push_benefits_player_then_player_wins() {
   VieDealerHand dealer = new VieDealerHand(seventeen);
   List<ViePlayerHand> player = Collections.singletonList(new ViePlayerHand(15, seventeen));
   
   final CasinoRules houseRules = new OrdinaryBlackjackRules() {
     @Override
     public double getPayoutForPush() {
       return .5;
     }

   };
   
   TableRules rulesWithExtraPayout = new TableRules(houseRules);
   
   List<ViePlayerHandPayout> payouts = HandUtil.calculatePayouts(player, dealer, rulesWithExtraPayout);
   Assert.assertTrue(15 + (int) Math.ceil(.5 * 15) == payouts.stream().mapToInt(ViePlayerHandPayout::getPayout).sum());
   
 }
 

 @Test
 public void when_player_splits_and_doubles_then_both_hands_are_counted() {
   VieDealerHand dealer = new VieDealerHand(bust);
   List<ViePlayerHand> player = Arrays.asList(new ViePlayerHand(10, seventeen), new ViePlayerHand(20, nonNatural21));

   List<ViePlayerHandPayout> payouts = HandUtil.calculatePayouts(player, dealer, rules);
   Assert.assertTrue(20 + 40 == payouts.stream().mapToInt(ViePlayerHandPayout::getPayout).sum());

 }



}
