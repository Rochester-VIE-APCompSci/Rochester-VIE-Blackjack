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
package com.ibm.vie.blackjack.casino.driver.crooked;

import java.util.List;

import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.casino.crooked.CardSwitchCardManager;
import com.ibm.vie.blackjack.casino.observer.PrintToConsoleObserver;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;
import com.ibm.vie.blackjack.player.TableRules;

/**
 * Provides full control over the cards that are dealt and the decisions that are made.
 * Standard input MUST be available for this to work.
 * 
 * @author ntl
 *
 */
public class GameInTestMode implements PlayerStrategy {

  @Override
  public int placeInitialBet(GameInfo gameInfo) {
    return gameInfo.getMinBet();
  }

  /**
  *
  * Always split, if legal, otherwise stand on 17
  *
  * @see PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, Card)
  */
 @Override
 public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
     final PlayerHand currentHand, final List<PlayerHand> playerHands,
     final Card dealerUpCard) {

   PlayerDecision decision;

   final List<Card> cards = currentHand.getCards();

   if (currentHand.getCards().size() == 2
       && gameInfo.getAvailableMoney() > currentHand.getBetPaid()
       && cards.get(0).getRank().equals(cards.get(1).getRank())) {
     decision = PlayerDecision.SPLIT;
   } else {
     if (currentHand.getPointScore() >= 17) {
       decision = PlayerDecision.STAND;
     } else {
       decision = PlayerDecision.HIT;
     }
   }


   return decision;
 }
  
//  @Override OLD VERSION
//  public PlayerDecision decideHowToPlayHand(GameInfo gameInfo, PlayerHand currentHand,
//      List<PlayerHand> playerHands, Card dealerUpCard) {
//    
//    while (true) {
//    System.out.println("\n\nStrategy:\nS=Split\nH=Hit\nT=Stand\nD=Double\n");
//    System.out.println("Prompt For Strategy--->");
//    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//    char [] choice = new char[1];
//    try {
//      reader.read(choice);
//      switch (choice[0]) {
//        case 'S': return PlayerDecision.SPLIT;
//        case 'H': return PlayerDecision.HIT;
//        case 'T': return PlayerDecision.STAND;
//        case 'D': return PlayerDecision.DOUBLE_DOWN;
//        default: System.err.println("Invalid Choice\n");
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    }
//  }

 /**
  * Walks away after 20 rounds
  *
  * @see PlayerStrategy#decideToWalkAway(GameInfo, List, DealerHand)
  */
 @Override
 public boolean decideToWalkAway(final GameInfo gameInfo,
     final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

   /**
    * Play at most 20 rounds
    */
   if (gameInfo.getRoundNumber() >= 20) {
     return true;
   } else {
     return false;
   }
 }
  
//  @Override
//  public boolean decideToWalkAway(GameInfo gameInfo, List<PlayerPayoutHand> playerHands,
//      DealerHand dealerHand) {
//   
//    System.out.println("\n\n\nWalk Away? Y/N");
//    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//    char [] choice = new char[1];
//    try {
//      reader.read(choice);
//    } catch (IOException e) {
//      choice[0] = 'N';
//      e.printStackTrace();
//    }
//    
//    return (choice[0] == 'Y');
//  }

  
  
  public static void main(String [] args) {
    final TableRules rules = new TableRules(100, 10, 25, 500, 1);
    final CrookedObserver observer = new CrookedObserver();
    
    // this object allows changing the cards as they are dealt
    final CardSwitchCardManager decks = new CardSwitchCardManager(rules, 25, observer);
    
    final Table table = new Table(new GameInTestMode(), decks, rules);
    table.addObserver(observer);
    table.addObserver(new PrintToConsoleObserver());
    table.playManyRoundsOfBlackJack();
    
    
  }

  @Override
  public String getStudentName() {
    return "Darkwing Duck";
  }
  
}
