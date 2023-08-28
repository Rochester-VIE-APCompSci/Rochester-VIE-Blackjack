/*
 * Copyright (c) 2018,2018 IBM Corporation Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.vie.blackjack.casino;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import com.ibm.vie.blackjack.casino.card.Rank;
import com.ibm.vie.blackjack.casino.rules.OrdinaryBlackjackRules;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.CasinoRules;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.Hand;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;
import com.ibm.vie.blackjack.player.TableRules;

public class TableTest {
  private final int defaultInitialBet = 10;
  private TableRules commonRules = new TableRules(new CasinoRules() {

    @Override
    public double getBlackJackPayOut() {
      return 3.0 / 2.0;
    }

    @Override
    public boolean getDealerHitsOnSoft17() {
      return false;
    }

    @Override
    public int getDeckPenetration() {
      return 100;
    }

    @Override
    public boolean getUseRealCasinoRulesWhenOutOfCards() {
      return false;
    }

    @Override
    public double getPayoutForPush() {
      return 0;
    }

    @Override
    public String getDescription() {
      return "Common Casino rules for testcase";
    }
    
  });

  /**
   * Returns a set of rules, setting the appropriate number of decks into the rules and using
   * defaults for other parameters.
   * 
   * @param numDecks - number of decks in the rules
   * 
   * @return a table rules
   */
  private static TableRules getTableRulesForNumDecks(final int numDecks) {
    return new TableRules(BlackjackTableRuleDefaults.INITIAL_MONEY,
        BlackjackTableRuleDefaults.MIN_BET, BlackjackTableRuleDefaults.MAX_BET,
        BlackjackTableRuleDefaults.MAX_NUM_ROUNDS, numDecks);
  }

  /**
   * Method to assert that the correct cards are in the dealer's or player's hand
   *
   * @param hand - the hand to perform assertions on
   * @param ranks - ranks of cards expected to be in the hand.
   */
  private void assertHandIsCorrect(final Hand hand, final int pointScore, final int scoreAceAs1,
      final Rank... ranks) {
    Assert.assertTrue(hand.getPointScore() == pointScore);
    Assert.assertTrue(hand.getScoreAceAs1() == scoreAceAs1);
    Assert.assertTrue(hand.getCards().size() == ranks.length);
    final List<String> cardRanks = new LinkedList<>();
    hand.getCards().forEach((final Card c) -> cardRanks.add(c.getRank()));
    for (final Rank r : ranks) {
      final int index = cardRanks.indexOf(r.toString().toLowerCase());
      Assert.assertTrue("Hand does not have a card of RANK " + r.toString(), index >= 0);
      cardRanks.remove(index);
    }
  }

  /**
   * Abstract class that will be extended to test blackjack scenarios
   *
   * @author ntl
   *
   */
  private abstract class BlackJackPlayerStrategy implements PlayerStrategy {

    @Override
    public int placeInitialBet(final GameInfo gameInfo) {
      return defaultInitialBet;
    }

    @Override
    public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo, final PlayerHand currentHand,
        final List<PlayerHand> playerHands, final Card dealerUpCard) {
      Assert.fail("The player should not get to make a decision when blackjack is dealt");
      return null; // for compiler
    }

  };



  /**
   *
   * Test that things work right when a player is dealt a blackjack
   *
   */
  @Test
  public void when_blackjack_is_dealt_and_player_wins_then_no_player_decision() {

    // player wins
    final CardManager shoe = new CardManager(getTableRulesForNumDecks(1));
    shoe.orderDeckByRank(Rank.ACE, Rank.JACK, Rank.EIGHT, Rank.FIVE);

    final BlackJackPlayerStrategy playerWins = new BlackJackPlayerStrategy() {

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            + gameInfo.getTableRules().getCompetitionRules().getBlackJackPayOut()
                * defaultInitialBet);

        Assert.assertTrue(playerHands.get(0).getBetPaid() == defaultInitialBet);
        assertHandIsCorrect(playerHands.get(0), 21, 11, Rank.ACE, Rank.JACK);
        assertHandIsCorrect(dealerHand, 13, 13, Rank.EIGHT, Rank.FIVE);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };


    final Table playerWinsWithBlackJack = new Table(playerWins, shoe, commonRules);
    playerWinsWithBlackJack.playManyRoundsOfBlackJack();
    Assert.assertTrue(playerWinsWithBlackJack
        .getAvailableMoney() == playerWinsWithBlackJack.getRules().getInitialMoney()
            + defaultInitialBet
                * playerWinsWithBlackJack.getRules().getCompetitionRules().getBlackJackPayOut());


  }

  /**
   * Test that things work right when the dealer is dealt a blackjack
   */
  @Test
  public void when_blackjack_is_dealt_and_dealer_wins_then_no_player_decision() {

    // dealer wins
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.TEN, Rank.TEN, Rank.JACK, Rank.ACE);

    final BlackJackPlayerStrategy dealerWins = new BlackJackPlayerStrategy() {

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            - defaultInitialBet);

        Assert.assertTrue(playerHands.get(0).getBetPaid() == defaultInitialBet);
        assertHandIsCorrect(playerHands.get(0), 20, 20, Rank.TEN, Rank.TEN);
        assertHandIsCorrect(dealerHand, 21, 11, Rank.JACK, Rank.ACE);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };

    final Table dealerWinsWithBlackJack = new Table(dealerWins, shoe, commonRules);
    dealerWinsWithBlackJack.playManyRoundsOfBlackJack();
    Assert.assertTrue(dealerWinsWithBlackJack
        .getAvailableMoney() == dealerWinsWithBlackJack.getRules().getInitialMoney()
            - defaultInitialBet);
  }


  /**
   * Test that things work right when both player and dealer are dealt a blackjack
   */
  @Test
  public void when_blackjack_is_dealt_to_both_then_no_player_decision() {
    // both have blackjack (push)
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.QUEEN, Rank.ACE, Rank.JACK, Rank.ACE);

    final BlackJackPlayerStrategy push = new BlackJackPlayerStrategy() {

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert
            .assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney());

        Assert.assertTrue(playerHands.get(0).getBetPaid() == defaultInitialBet);
        assertHandIsCorrect(playerHands.get(0), 21, 11, Rank.QUEEN, Rank.ACE);
        assertHandIsCorrect(dealerHand, 21, 11, Rank.JACK, Rank.ACE);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };


    final Table pushWithBlackJack = new Table(push, shoe, commonRules);
    pushWithBlackJack.playManyRoundsOfBlackJack();
    Assert.assertTrue(
        pushWithBlackJack.getAvailableMoney() == pushWithBlackJack.getRules().getInitialMoney());

  }


  /**
   * Test the hit function
   */
  @Test
  public void when_player_hits_and_wins_then_hand_has_right_cards_and_score() {
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.ACE, Rank.FIVE, Rank.EIGHT, Rank.FIVE, Rank.FOUR, Rank.ACE, Rank.TEN);


    final PlayerStrategy strategyWinHit = new PlayerStrategy() {

      int turn = 0;

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {

        // True for all turns
        Assert.assertTrue(dealerUpCard.getRank().equals(Rank.EIGHT.toString().toLowerCase()));
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            - defaultInitialBet);
        Assert.assertTrue(currentHand.getBetPaid() == defaultInitialBet);
        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(playerHands.get(0) == currentHand);

        // responses and assertions specific to a turn
        switch (turn++) {
          case 0:
            assertHandIsCorrect(playerHands.get(0), 16, 6, Rank.ACE, Rank.FIVE);
            return PlayerDecision.HIT;
          case 1:
            assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.FIVE, Rank.FOUR);
            return PlayerDecision.HIT;
          default:
            Assert.fail("Should not be called after a hit that creates an exact score of 21!");
            return null; // never happens
        }

      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            + playerHands.get(0).getBetPaid());

        assertHandIsCorrect(playerHands.get(0), 21, 11, Rank.ACE, Rank.FIVE, Rank.FOUR, Rank.ACE);
        assertHandIsCorrect(dealerHand, 23, 23, Rank.EIGHT, Rank.FIVE, Rank.TEN);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table playerWins = new Table(strategyWinHit, shoe, commonRules);
    playerWins.playManyRoundsOfBlackJack();
    Assert.assertTrue(playerWins.getAvailableMoney() == playerWins.getRules().getInitialMoney()
        + defaultInitialBet);


  }


  /**
   * Test the Split Function Note that this game is simpler than real casinos, aces can be split
   * multiple times, and a blackjack on an ace is still a blackjack.
   */
  @Test
  public void when_player_splits_and_wins_all_then_hand_has_right_cards_and_score() {
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.ACE, Rank.ACE, Rank.EIGHT, Rank.FIVE, Rank.FOUR, Rank.ACE, Rank.FIVE,
        Rank.NINE, Rank.TEN, Rank.TEN);


    final PlayerStrategy strategy = new PlayerStrategy() {

      int turn = 0;

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {

        // True for all turns
        Assert.assertTrue(dealerUpCard.getRank().equals(Rank.EIGHT.toString().toLowerCase()));
        Assert.assertTrue(currentHand.getBetPaid() == defaultInitialBet);


        // responses and assertions specific to a turn
        switch (turn++) {
          case 0:
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    - defaultInitialBet);
            Assert.assertTrue(playerHands.get(0) == currentHand);
            Assert.assertTrue(playerHands.size() == 1);
            assertHandIsCorrect(playerHands.get(0), 12, 2, Rank.ACE, Rank.ACE);
            return PlayerDecision.SPLIT;
          case 1:
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    - 2 * defaultInitialBet);
            Assert.assertTrue(playerHands.get(0) == currentHand);
            Assert.assertTrue(playerHands.size() == 2);
            assertHandIsCorrect(playerHands.get(0), 15, 5, Rank.ACE, Rank.FOUR);
            assertHandIsCorrect(playerHands.get(1), 12, 2, Rank.ACE, Rank.ACE);
            return PlayerDecision.HIT;
          case 2:
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    - 2 * defaultInitialBet);
            Assert.assertTrue(playerHands.get(0) == currentHand);
            Assert.assertTrue(playerHands.size() == 2);
            assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.FOUR, Rank.FIVE);
            assertHandIsCorrect(playerHands.get(1), 12, 2, Rank.ACE, Rank.ACE);
            return PlayerDecision.STAND;
          case 3:
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    - 2 * defaultInitialBet);
            Assert.assertTrue(playerHands.get(1) == currentHand);
            Assert.assertTrue(playerHands.size() == 2);
            assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.FOUR, Rank.FIVE);
            assertHandIsCorrect(playerHands.get(1), 12, 2, Rank.ACE, Rank.ACE);
            return PlayerDecision.SPLIT;
          case 4:
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    - 3 * defaultInitialBet);
            Assert.assertTrue(playerHands.get(1) == currentHand);
            Assert.assertTrue(playerHands.size() == 3);
            assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.FOUR, Rank.FIVE);
            assertHandIsCorrect(playerHands.get(1), 20, 10, Rank.ACE, Rank.NINE);
            assertHandIsCorrect(playerHands.get(2), 21, 11, Rank.ACE, Rank.TEN);
            return PlayerDecision.STAND;
          default:
            Assert.fail("Should not be called for the last split because that is a blackjack!");
            return null; // never happens
        }

      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 3);
        Assert.assertTrue("Available Money = " + gameInfo.getAvailableMoney(),
            gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                + 2 * defaultInitialBet + defaultInitialBet
                    * gameInfo.getTableRules().getCompetitionRules().getBlackJackPayOut());

        assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.FOUR, Rank.FIVE);
        assertHandIsCorrect(playerHands.get(1), 20, 10, Rank.ACE, Rank.NINE);
        assertHandIsCorrect(playerHands.get(2), 21, 11, Rank.ACE, Rank.TEN);

        assertHandIsCorrect(dealerHand, 23, 23, Rank.EIGHT, Rank.FIVE, Rank.TEN);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table playerWins = new Table(strategy, shoe, commonRules);
    playerWins.playManyRoundsOfBlackJack();
    Assert.assertTrue(playerWins.getAvailableMoney() == playerWins.getRules().getInitialMoney()
        + 2 * defaultInitialBet
        + defaultInitialBet * playerWins.getRules().getCompetitionRules().getBlackJackPayOut());


  }


  /**
   *
   * Test Double Down
   *
   */
  @Test
  public void when_player_doubles_and_loses_then_hand_has_right_cards_and_score() {
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.ACE, Rank.FIVE, Rank.EIGHT, Rank.FIVE, Rank.FOUR, Rank.EIGHT);


    final PlayerStrategy strategy = new PlayerStrategy() {

      int turn = 0;

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {

        // True for all turns
        Assert.assertTrue(dealerUpCard.getRank().equals(Rank.EIGHT.toString().toLowerCase()));
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            - defaultInitialBet);
        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(currentHand.getBetPaid() == defaultInitialBet);
        Assert.assertTrue(playerHands.get(0) == currentHand);

        // responses and assertions specific to a turn
        switch (turn++) {
          case 0:

            assertHandIsCorrect(playerHands.get(0), 16, 6, Rank.ACE, Rank.FIVE);
            return PlayerDecision.DOUBLE_DOWN;
          default:
            Assert.fail(
                "Should not be called after a double down because no more decisions can be made!");
            return null; // never happens
        }

      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            - 2 * defaultInitialBet);
        Assert.assertTrue(playerHands.get(0).getBetPaid() == 2 * defaultInitialBet);
        assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.FIVE, Rank.FOUR);
        assertHandIsCorrect(dealerHand, 21, 21, Rank.EIGHT, Rank.FIVE, Rank.EIGHT);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table playerLoses = new Table(strategy, shoe, commonRules);
    playerLoses.playManyRoundsOfBlackJack();
    Assert.assertTrue(playerLoses.getAvailableMoney() == playerLoses.getRules().getInitialMoney()
        - 2 * defaultInitialBet);


  }


  /**
   * Test double down and push, to make sure that the player gets the full bet back.
   */
  @Test
  public void when_player_doubles_and_push_then_hand_has_right_cards_and_score() {
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.ACE, Rank.FIVE, Rank.EIGHT, Rank.FIVE, Rank.FOUR, Rank.SEVEN);


    final PlayerStrategy strategy = new PlayerStrategy() {

      int turn = 0;

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {

        // True for all turns
        Assert.assertTrue(dealerUpCard.getRank().equals(Rank.EIGHT.toString().toLowerCase()));
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            - defaultInitialBet);
        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(currentHand.getBetPaid() == defaultInitialBet);
        Assert.assertTrue(playerHands.get(0) == currentHand);

        // responses and assertions specific to a turn
        switch (turn++) {
          case 0:

            assertHandIsCorrect(playerHands.get(0), 16, 6, Rank.ACE, Rank.FIVE);
            return PlayerDecision.DOUBLE_DOWN;
          default:
            Assert.fail(
                "Should not be called after a double down because no more decisions can be made!");
            return null; // never happens
        }

      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert
            .assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney());
        Assert.assertTrue(playerHands.get(0).getBetPaid() == 2 * defaultInitialBet);
        assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.FIVE, Rank.FOUR);
        assertHandIsCorrect(dealerHand, 20, 20, Rank.EIGHT, Rank.FIVE, Rank.SEVEN);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table playerPush = new Table(strategy, shoe, commonRules);
    playerPush.playManyRoundsOfBlackJack();
    Assert.assertTrue(playerPush.getAvailableMoney() == playerPush.getRules().getInitialMoney());


  }


  /**
   * Test Stand decision
   */
  @Test
  public void when_player_stands_and_push_then_hand_has_right_cards_and_score() {
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.FIVE, Rank.THREE, Rank.THREE);


    final PlayerStrategy strategy = new PlayerStrategy() {

      int turn = 0;

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {

        // True for all turns
        Assert.assertTrue(dealerUpCard.getRank().equals(Rank.EIGHT.toString().toLowerCase()));
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            - defaultInitialBet);
        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(currentHand.getBetPaid() == defaultInitialBet);
        Assert.assertTrue(playerHands.get(0) == currentHand);

        // responses and assertions specific to a turn
        switch (turn++) {
          case 0:

            assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
            return PlayerDecision.STAND;
          default:
            Assert.fail("Should not be called after a stand");
            return null; // never happens
        }

      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert
            .assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney());
        Assert.assertTrue(playerHands.get(0).getBetPaid() == defaultInitialBet);
        assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
        assertHandIsCorrect(dealerHand, 19, 19, Rank.EIGHT, Rank.FIVE, Rank.THREE, Rank.THREE);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table playerPush = new Table(strategy, shoe, commonRules);
    playerPush.playManyRoundsOfBlackJack();
    Assert.assertTrue(playerPush.getAvailableMoney() == playerPush.getRules().getInitialMoney());


  }



  /**
   * Player wins two rounds in a row with a different bet in the second round
   *
   */
  @Test
  public void when_player_plays_2_rounds_then_hand_has_right_cards_and_score() {
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.FIVE, Rank.THREE, Rank.TWO, Rank.ACE,
        Rank.NINE, Rank.TWO, Rank.TEN, Rank.TEN);


    final PlayerStrategy strategy = new PlayerStrategy() {

      int turn = 0;
      int decisions = 0;
      int turnBet;

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        turnBet = defaultInitialBet + turn;
        decisions = 0;
        return turnBet;

      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {

        // True for all turns
        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(playerHands.get(0) == currentHand);
        Assert.assertTrue(decisions++ == 0);

        // responses and assertions specific to a turn
        switch (turn) {
          case 0:
            Assert.assertTrue(currentHand.getBetPaid() == defaultInitialBet);
            Assert.assertTrue(dealerUpCard.getRank().equals(Rank.EIGHT.toString().toLowerCase()));
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    - defaultInitialBet);
            assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
            return PlayerDecision.STAND;
          case 1: // round 2
            Assert.assertTrue(currentHand.getBetPaid() == turnBet);
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    + defaultInitialBet - turnBet);
            Assert.assertTrue(dealerUpCard.getRank().equals(Rank.TWO.toString().toLowerCase()));
            assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.NINE);
            return PlayerDecision.STAND;
          default:
            Assert.fail("Should not be called for turn " + turn);
            return null; // never happens
        }

      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(playerHands.get(0).getBetPaid() == turnBet);

        switch (turn++) {
          case 0: // player wins
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    + playerHands.get(0).getBetPaid());
            assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
            assertHandIsCorrect(dealerHand, 18, 18, Rank.EIGHT, Rank.FIVE, Rank.THREE, Rank.TWO);
            return false;
          case 1: // player wins
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney() + turnBet
                    + defaultInitialBet);
            assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.NINE);
            assertHandIsCorrect(dealerHand, 22, 22, Rank.TWO, Rank.TEN, Rank.TEN);
            return true;
          default:
            Assert.fail("Should never get here");
            return true; // never happens
        }


      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table twoRounds = new Table(strategy, shoe, commonRules);
    twoRounds.playManyRoundsOfBlackJack();
    Assert.assertTrue(twoRounds.getAvailableMoney() == twoRounds.getRules().getInitialMoney()
        + 2 * defaultInitialBet + 1);


  }



  private Map<String, Integer> mapDiscardList(final List<Card> actual) {
    final Map<String, Integer> result = new HashMap<>();
    for (final Card card : actual) {
      final int curCount = result.getOrDefault(card.toString(), 0);
      result.put(card.toString(), curCount + 1);
    }
    return result;
  }



  private void updateExpectedDiscardList(final Map<String, Integer> expectedDiscardMap,
      final List<PlayerPayoutHand> payouts, final DealerHand dealerHand) {

    for (final PlayerPayoutHand hand : payouts) {
      for (final Card card : hand.getCards()) {
        final int curExpectCount = expectedDiscardMap.getOrDefault(card.toString(), 0);
        expectedDiscardMap.put(card.toString(), curExpectCount + 1);
      }
    }

    for (final Card card : dealerHand.getCards()) {
      final int curExpectCount = expectedDiscardMap.getOrDefault(card.toString(), 0);
      expectedDiscardMap.put(card.toString(), curExpectCount + 1);
    }
  }



  private void assertDiscardList(final Map<String, Integer> expected, final List<Card> actual) {
    final Map<String, Integer> actualDiscardMap = mapDiscardList(actual);
    Assert.assertTrue("The discard list does not match what was expected",
        actualDiscardMap.equals(expected));
  }

  /**
   * Check the discard list
   *
   */
  @Test
  public void when_player_plays_2_rounds_then_discard_list_is_correct() {
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.FIVE, Rank.THREE, Rank.TWO, Rank.ACE,
        Rank.NINE, Rank.TWO, Rank.TEN, Rank.TEN);

    final Map<String, Integer> expectedDiscardMap = new HashMap<>();

    final PlayerStrategy strategy = new PlayerStrategy() {


      int turn = 0;
      int decisions = 0;
      int turnBet;

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {

        assertDiscardList(expectedDiscardMap, gameInfo.getCardsInDiscardTray());

        turnBet = defaultInitialBet + turn;
        decisions = 0;
        return turnBet;

      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {

        // True for all turns
        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(playerHands.get(0) == currentHand);
        Assert.assertTrue(decisions++ == 0);

        assertDiscardList(expectedDiscardMap, gameInfo.getCardsInDiscardTray());

        // responses and assertions specific to a turn
        switch (turn) {
          case 0:
            Assert.assertTrue(currentHand.getBetPaid() == defaultInitialBet);
            Assert.assertTrue(dealerUpCard.getRank().equals(Rank.EIGHT.toString().toLowerCase()));
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    - defaultInitialBet);
            assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
            return PlayerDecision.STAND;
          case 1: // round 2
            Assert.assertTrue(currentHand.getBetPaid() == turnBet);
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    + defaultInitialBet - turnBet);
            Assert.assertTrue(dealerUpCard.getRank().equals(Rank.TWO.toString().toLowerCase()));
            assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.NINE);
            return PlayerDecision.STAND;
          default:
            Assert.fail("Should not be called for turn " + turn);
            return null; // never happens
        }
      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        assertDiscardList(expectedDiscardMap, gameInfo.getCardsInDiscardTray());
        // update for next round
        updateExpectedDiscardList(expectedDiscardMap, playerHands, dealerHand);

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(playerHands.get(0).getBetPaid() == turnBet);

        switch (turn++) {
          case 0: // player wins
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
                    + playerHands.get(0).getBetPaid());
            assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
            assertHandIsCorrect(dealerHand, 18, 18, Rank.EIGHT, Rank.FIVE, Rank.THREE, Rank.TWO);
            return false;
          case 1: // player wins
            Assert.assertTrue(
                gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney() + turnBet
                    + defaultInitialBet);
            assertHandIsCorrect(playerHands.get(0), 20, 10, Rank.ACE, Rank.NINE);
            assertHandIsCorrect(dealerHand, 22, 22, Rank.TWO, Rank.TEN, Rank.TEN);
            return true;
          default:
            Assert.fail("Should never get here");
            return true; // never happens
        }


      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table twoRounds = new Table(strategy, shoe, commonRules);
    twoRounds.playManyRoundsOfBlackJack();
    Assert.assertTrue(twoRounds.getAvailableMoney() == twoRounds.getRules().getInitialMoney()
        + 2 * defaultInitialBet + 1);


  }



  /**
   * Check the discard list, after playing enough rounds to force a shuffle Also verifies that there
   * are never more occurrence of a card in the discard list than number of decks.
   *
   */
  @Test
  public void when_player_plays_many_rounds_then_discard_list_is_correct() {
    final CardManager shoe = new CardManager(getTableRulesForNumDecks(1));
    final TableRules rules = new TableRules(BlackjackTableRuleDefaults.INITIAL_MONEY,
        BlackjackTableRuleDefaults.MIN_BET, BlackjackTableRuleDefaults.MAX_BET,
        BlackjackTableRuleDefaults.MAX_NUM_ROUNDS, shoe.getNumberOfDecks());

    final Map<String, Integer> expectedDiscardMap = new HashMap<>();

    final PlayerStrategy strategy = new PlayerStrategy() {


      private int countCards(final List<? extends Hand> hands) {
        int cardCount = 0;
        for (final Hand hand : hands) {
          cardCount += hand.getCards().size();
        }
        return cardCount;
      }

      int numRounds = 0;

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {

        assertDiscardList(expectedDiscardMap, gameInfo.getCardsInDiscardTray());
        numRounds++;
        return gameInfo.getMinBet();
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {


        if (countCards(playerHands) + 2
            + gameInfo.getCardsInDiscardTray().size() > gameInfo.getTableRules().getNumberOfDecks()
                * 52) {
          expectedDiscardMap.clear();
        }
        assertDiscardList(expectedDiscardMap, gameInfo.getCardsInDiscardTray());
        for (final Entry<String, Integer> cardCount : expectedDiscardMap.entrySet()) {
          Assert.assertTrue(
              "There are too many (" + cardCount.getValue() + ") " + cardCount.getKey()
                  + " in the deck!",
              cardCount.getValue() < gameInfo.getTableRules().getNumberOfDecks());
        }

        PlayerDecision decision;

        if (currentHand.getPointScore() >= 17) {
          decision = PlayerDecision.STAND;
        } else {
          decision = PlayerDecision.HIT;
        }


        return decision;

      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        if (countCards(playerHands) + dealerHand.getCards().size()
            + gameInfo.getCardsInDiscardTray().size() > gameInfo.getTableRules().getNumberOfDecks()
                * 52) {
          expectedDiscardMap.clear();
        }
        assertDiscardList(expectedDiscardMap, gameInfo.getCardsInDiscardTray());
        for (final Entry<String, Integer> cardCount : expectedDiscardMap.entrySet()) {
          Assert.assertTrue(
              "There are too many (" + cardCount.getValue() + ") " + cardCount.getKey()
                  + " in the deck!",
              cardCount.getValue() < gameInfo.getTableRules().getNumberOfDecks());
        }


        // update for next round
        updateExpectedDiscardList(expectedDiscardMap, playerHands, dealerHand);

        // This assumes that the min bet is 1 and the starting initial value is 100.
        // We can't run out of money in fewer than 95 rounds
        return (numRounds <= 95);


      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table twoRounds = new Table(strategy, shoe, rules);
    twoRounds.playManyRoundsOfBlackJack();

  }



  /**
   * Test dealer stands at 17
   */
  @Test
  public void when_dealer_soft_17_then_dealer_stops() {
    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.TEN, Rank.NINE, Rank.ACE, Rank.SIX, Rank.THREE, Rank.THREE);


    final PlayerStrategy strategy = new PlayerStrategy() {

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {


        Assert.assertTrue(dealerUpCard.getRank().equals(Rank.ACE.toString().toLowerCase()));
        assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
        return PlayerDecision.STAND;
      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            + defaultInitialBet);
        Assert.assertTrue(playerHands.get(0).getBetPaid() == defaultInitialBet);
        Assert.assertTrue(playerHands.get(0).getPayout() == 2 * defaultInitialBet);
        assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
        assertHandIsCorrect(dealerHand, 17, 7, Rank.ACE, Rank.SIX);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table testTable = new Table(strategy, shoe, commonRules);
    testTable.playManyRoundsOfBlackJack();
    Assert.assertTrue(testTable.getAvailableMoney() == testTable.getRules().getInitialMoney()
        + defaultInitialBet);


  }



  @Test
  public void when_dealer_soft_17_then_dealer_hits() {
    final CasinoRules houseRules = new OrdinaryBlackjackRules() {
      @Override
      public boolean getDealerHitsOnSoft17() {
        return true;
      };
    };
    final TableRules hitOnSoft17Rules =
        new TableRules(BlackjackTableRuleDefaults.INITIAL_MONEY, BlackjackTableRuleDefaults.MIN_BET,
            BlackjackTableRuleDefaults.MAX_BET, BlackjackTableRuleDefaults.MAX_NUM_ROUNDS,
            BlackjackTableRuleDefaults.NUMBER_OF_DECKS, houseRules);

    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.TEN, Rank.NINE, Rank.SIX, Rank.ACE, Rank.THREE, Rank.THREE);


    final PlayerStrategy strategy = new PlayerStrategy() {

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {


        Assert.assertTrue(dealerUpCard.getRank().equals(Rank.SIX.toString().toLowerCase()));
        assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
        return PlayerDecision.STAND;
      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            - defaultInitialBet);
        Assert.assertTrue(playerHands.get(0).getBetPaid() == defaultInitialBet);
        Assert.assertTrue(playerHands.get(0).getPayout() == 0);
        assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
        assertHandIsCorrect(dealerHand, 20, 10, Rank.ACE, Rank.SIX, Rank.THREE);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table testTable = new Table(strategy, shoe, hitOnSoft17Rules);
    testTable.playManyRoundsOfBlackJack();
    Assert.assertTrue(testTable.getAvailableMoney() == testTable.getRules().getInitialMoney()
        - defaultInitialBet);


  }


  @Test
  public void when_dealer_hard_17_then_dealer_stays() {

    final CasinoRules houseRules = new OrdinaryBlackjackRules() {
      @Override
      public boolean getDealerHitsOnSoft17() {
        return true;
      };
    };

    final TableRules hitOnSoft17Rules =
        new TableRules(BlackjackTableRuleDefaults.INITIAL_MONEY, BlackjackTableRuleDefaults.MIN_BET,
            BlackjackTableRuleDefaults.MAX_BET, BlackjackTableRuleDefaults.MAX_NUM_ROUNDS,
            BlackjackTableRuleDefaults.NUMBER_OF_DECKS, houseRules);

    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.TEN, Rank.NINE, Rank.TEN, Rank.SEVEN, Rank.THREE, Rank.THREE);


    final PlayerStrategy strategy = new PlayerStrategy() {

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {


        Assert.assertTrue(dealerUpCard.getRank().equals(Rank.TEN.toString().toLowerCase()));
        assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
        return PlayerDecision.STAND;
      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            + defaultInitialBet);
        Assert.assertTrue(playerHands.get(0).getBetPaid() == defaultInitialBet);
        Assert.assertTrue(playerHands.get(0).getPayout() == 2 * defaultInitialBet);
        assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
        assertHandIsCorrect(dealerHand, 17, 17, Rank.TEN, Rank.SEVEN);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table testTable = new Table(strategy, shoe, hitOnSoft17Rules);
    testTable.playManyRoundsOfBlackJack();
    Assert.assertTrue(testTable.getAvailableMoney() == testTable.getRules().getInitialMoney()
        + defaultInitialBet);


  }

  @Test
  public void when_dealer_soft_18_then_dealer_stays() {

    final CasinoRules houseRules = new OrdinaryBlackjackRules() {
      @Override
      public boolean getDealerHitsOnSoft17() {
        return true;
      }

    };

    final TableRules hitOnSoft17Rules =
        new TableRules(BlackjackTableRuleDefaults.INITIAL_MONEY, BlackjackTableRuleDefaults.MIN_BET,
            BlackjackTableRuleDefaults.MAX_BET, BlackjackTableRuleDefaults.MAX_NUM_ROUNDS,
            BlackjackTableRuleDefaults.NUMBER_OF_DECKS, houseRules);

    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.ACE, Rank.THREE, Rank.THREE);


    final PlayerStrategy strategy = new PlayerStrategy() {

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {


        Assert.assertTrue(dealerUpCard.getRank().equals(Rank.SEVEN.toString().toLowerCase()));
        assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
        return PlayerDecision.STAND;
      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        Assert.assertTrue(playerHands.size() == 1);
        Assert.assertTrue(gameInfo.getAvailableMoney() == gameInfo.getTableRules().getInitialMoney()
            + defaultInitialBet);
        Assert.assertTrue(playerHands.get(0).getBetPaid() == defaultInitialBet);
        Assert.assertTrue(playerHands.get(0).getPayout() == defaultInitialBet * 2);
        assertHandIsCorrect(playerHands.get(0), 19, 19, Rank.TEN, Rank.NINE);
        assertHandIsCorrect(dealerHand, 18, 8, Rank.ACE, Rank.SEVEN);

        return true;
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table testTable = new Table(strategy, shoe, hitOnSoft17Rules);
    testTable.playManyRoundsOfBlackJack();
    Assert.assertTrue(testTable.getAvailableMoney() == testTable.getRules().getInitialMoney()
        + defaultInitialBet);


  }


  @Test
  public void when_deck_penetration_then_shuffle_at_end_of_hand() {

    final CasinoRules houseRules = new OrdinaryBlackjackRules() {
      @Override
      public int getDeckPenetration() {
        return 1;
      }

    };

    final TableRules deckPenetration =
        new TableRules(BlackjackTableRuleDefaults.INITIAL_MONEY, BlackjackTableRuleDefaults.MIN_BET,
            BlackjackTableRuleDefaults.MAX_BET, BlackjackTableRuleDefaults.MAX_NUM_ROUNDS,
            BlackjackTableRuleDefaults.NUMBER_OF_DECKS, houseRules);

    final CardManager shoe =
        new CardManager(getTableRulesForNumDecks(BlackjackTableRuleDefaults.NUMBER_OF_DECKS));
    shoe.orderDeckByRank(Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.ACE, Rank.THREE, Rank.THREE);


    final PlayerStrategy strategy = new PlayerStrategy() {

      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        // First time nothing has been discarded
        // Second time enough cards have been dealt we should have shuffled
        assert (gameInfo.getCardsInDiscardTray().isEmpty());

        return defaultInitialBet;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {

        return PlayerDecision.STAND;
      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        if (gameInfo.getRoundNumber() < 2) {
          return true;
        } else {
          return false;
        }
      }

      @Override
      public String getStudentName() {
        return null;
      }

    };



    final Table testTable = new Table(strategy, shoe, deckPenetration);
    testTable.playManyRoundsOfBlackJack();
    Assert.assertTrue(testTable.getAvailableMoney() == testTable.getRules().getInitialMoney()
        + defaultInitialBet);

  }

  @Test
  public void given_real_casino_rules_when_mid_round_shuffle_then_shuffle_before_next_round() {
    final CasinoRules houseRules = new OrdinaryBlackjackRules() {
      @Override
      public int getDeckPenetration() {
        return 100;
      }

      @Override
      public boolean getUseRealCasinoRulesWhenOutOfCards() {
        return true;
      }
      

    };

    final TableRules rules =
        new TableRules(BlackjackTableRuleDefaults.INITIAL_MONEY, BlackjackTableRuleDefaults.MIN_BET,
            BlackjackTableRuleDefaults.MAX_BET, BlackjackTableRuleDefaults.MAX_NUM_ROUNDS,
            BlackjackTableRuleDefaults.NUMBER_OF_DECKS, houseRules);
  

    final CardManager shoe =  new CardManager(rules,12345);
    
    final AtomicBoolean testOK = new AtomicBoolean(false);
    final PlayerStrategy strategy = new PlayerStrategy() {
      
      boolean shouldShuffleBeforeNextDecision = false;
      @Override
      public int placeInitialBet(final GameInfo gameInfo) {
        if (shouldShuffleBeforeNextDecision) {
          shouldShuffleBeforeNextDecision = false;
          Assert.assertFalse(shoe.getLastShuffleDueToEmptyDeck());
          Assert.assertEquals(shoe.getTotalNumCards(), shoe.getRemainingCardsInShoe() );
          Assert.assertTrue(gameInfo.getCardsInDiscardTray().isEmpty());
          testOK.set(true);
        }
        return BlackjackTableRuleDefaults.MIN_BET;
      }

      @Override
      public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo,
          final PlayerHand currentHand, final List<PlayerHand> playerHands,
          final Card dealerUpCard) {

        // checking the discard tray for a shuffle here is wrong
        // the deal could have been a player or dealer blackjack and we
        // would never get a decision
        
        return PlayerDecision.STAND;
      }

      @Override
      public boolean decideToWalkAway(final GameInfo gameInfo,
          final List<PlayerPayoutHand> playerHands, final DealerHand dealerHand) {

        if (shoe.getLastShuffleDueToEmptyDeck()) {
          shouldShuffleBeforeNextDecision = true;
        }
        // shuffles will happen roughly every 11-15 rounds or so, 500 hits the test
        // scenario many times, including cases where blackjacks are dealt
        if (gameInfo.getRoundNumber() > 500) {
          return true;
        } else {
          return false;
        }
      }

      @Override
      public String getStudentName() {
        return "";
      }

    };
    

    final Table testTable = new Table(strategy, shoe, rules);
    testTable.playManyRoundsOfBlackJack();
    Assert.assertTrue(testOK.get());
 
    shoe.shuffle();
    Assert.assertEquals(shoe.getTotalNumCards(), rules.getNumberOfDecks() * 52);
    Assert.assertEquals(rules.getNumberOfDecks() * 52, shoe.getRemainingCardsInShoe());
    
  
  }

}
