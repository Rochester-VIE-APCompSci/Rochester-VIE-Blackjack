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
package student.player;

import java.util.List;

import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.Hand;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;

/**
 * <p>
 * This class contains a sample solution that you will modify. The starting solution will decide to
 * hit until a score of 21 is reached, and then will decide to stand. This solution is simple, but
 * not a great performer, since most of the time the algorithm will exceed 21 and lose. Your job is
 * to make it better.
 * </p>
 *
 * <p>
 * Each method has a description for it that will help you understand how you can modify the method
 * to change your algorithm. Methods you will need to modify have the words "TODO" in the comments
 * of the method.
 * </p>
 * <p>
 * Good Luck!
 * </p>
 *
 * <p>
 * Note: Do not change the name of this class, if you do you will also need to update the class name
 * in the {@link #main(String[])} method so that it matches the new name of the class.
 * </p>
 *
 * @author ntl
 *
 */
public class MyPlayer implements PlayerStrategy {

  /**
   * <p>
   * An instance of this class will be created (and the constructor called) prior to the start of
   * playing at a table.
   * </p>
   *
   * <p>
   * The information about the game is not available until you are asked to make your first bet,
   * however you can use the constructor to initialize any instance variables that you might wish to
   * use for your solution.
   * </p>
   *
   */
  public MyPlayer() {
    // Optional TODO: Perform any pre-game preparations
  }

  /**
   * <p>
   * This method returns the amount that you will initially bet that you will win the hand. The bet
   * must be between the minimum and maximum bet for the table AND the bet must be no larger than
   * the amount of available money that you have.
   * </p>
   *
   * <p>
   * You can get information about your available money, and the valid range of the bet from the
   * {@link GameInfo} object that is provided to the method.
   * </p>
   *
   * <p>
   * The initial solution bets either the maximum amount, or the available money (whichever is
   * smaller).
   * </p>
   *
   * <p>
   * see also {@link PlayerStrategy#placeInitialBet(GameInfo)}
   * </p>
   *
   */
  @Override
  public int placeInitialBet(final GameInfo gameInfo) {
    System.out.println("***********************************************");
    System.out.println("Starting round " + gameInfo.getRoundNumber() + "!");
    System.out.println("You have " + gameInfo.getAvailableMoney() + " money.");
    System.out.println("The rules say that your bet must be between " + gameInfo.getMinBet()
        + " and " + gameInfo.getMaxBet());

    // TODO: Find a betting solution that is better than always betting the max amount
    return Math.min(gameInfo.getAvailableMoney(), gameInfo.getMaxBet());
  }


  /**
   * <p>
   * This method is called every time your player has a decision to make. The initial decision
   * strategy (Stand at 21) is described in the class description.
   * </p>
   *
   * <p>
   * Be sure to look at {@link PlayerStrategy#decideHowToPlayHand(GameInfo, PlayerHand, List, Card)}
   * for a description of the parameters of this method.
   * </p>
   *
   * <p>
   * Also be sure to look at {@link PlayerDecision} for a description of when it is legal to make
   * each type of decision. An illegal decision will cause the game to end.
   * </p>
   *
   * <p>
   * The current hand parameter is that hand that you are being asked to make a decision on. The
   * playerHands parameter is a list of all player hands on the table. If your code makes a
   * decision to split, then this list will contain more than one hand, since there is now more than
   * one hand on the table.
   * </p>
   *
   */
  @Override
  public PlayerDecision decideHowToPlayHand(final GameInfo gameInfo, final PlayerHand currentHand,
      final List<PlayerHand> playerHands, final Card dealerUpCard) {

    PlayerDecision decision;

    System.out.println();
    System.out.println(
        "The dealer up card is a " + dealerUpCard + " with rank " + dealerUpCard.getRank());
    System.out.println("This hand has a score of (counting aces as 11 if possible) = "
        + currentHand.getPointScore());
    System.out
        .println("This hand has a score (counting aces as 1) = " + currentHand.getScoreAceAs1());
    System.out.println("You have the following cards in your hand:");
    for (final Card c : currentHand.getCards()) {
      System.out.println("\t" + c.toString() + " which has a rank of " + c.getRank());
    }

    // TODO: This is not the best solution, improve it!!!
    if (currentHand.getPointScore() >= 21) {
      decision = PlayerDecision.STAND;
    } else {
      decision = PlayerDecision.HIT;
    }


    return decision;
  }

  /**
   * <p>
   * This method is called at the end of a round, after bets have been settled. At this time, you
   * can take a look at how you did, and decide whether or not to keep on playing the game or walk
   * away from this table. (Returning a true value walks away)
   * </p>
   *
   * <p>
   * Walking away early might cost you potential future winnings, but playing until after your luck
   * runs out will cost you also.
   * </p>
   *
   * <p>
   * The default behavior never walks away.
   * </p>
   *
   * <p>
   * Be sure to also look at {@link PlayerStrategy#decideToWalkAway(GameInfo, List, DealerHand)}
   * </p>
   */
  @Override
  public boolean decideToWalkAway(final GameInfo gameInfo, final List<PlayerPayoutHand> playerHands,
      final DealerHand dealerHand) {

    System.out.println();
    System.out.println("Round OVER!!!");
    System.out.println("The dealer's turn ended with a hand of " + dealerHand);
    System.out.println("That means that the dealer had a score of " + dealerHand.getPointScore());

    System.out.println("You had " + playerHands.size() + " hands that played against the dealer.");
    for (final Hand hand : playerHands) {
      System.out.println(hand);
    }
    /*
     * Iterate over all your hands, calculate how much each hand won or lost. There will only be
     * more than one hand if you made a decision to SPLIT earlier in the round. The dealer pays back
     * the initial bet, so how much you won or lost can be calculated by subtracting the bet paid
     * from the payout.
     */
    int earnings = 0;
    for (final PlayerPayoutHand hand : playerHands) {
      earnings = earnings + hand.getPayout() - hand.getBetPaid();
    }
    System.out.println("This round earned you " + earnings + "!");

    // TODO returning false means the player never walks away
    //      in other words he or she plays until they cannot play anymore.
    //      Consider adding a stopping condition....
    return false;
  }

  /**
   * This method should return your name. We use this to make sure you get credit for your solution!
   * The name chosen does not impact the flow of the game itself.
   *
   */
  @Override
  public String getStudentName() {
    // TODO: Put your name here
    return "FirstName LastName";
  }

  /**
   * <p>
   * The main method starts the blackjack game with your strategy.
   * </p>
   * <p>
   * DO <B>NOT</B> CHANGE THIS METHOD. The class passed into the playBlackjackInteractive method
   * must match the name of this class.
   * </p>
   *
   * @param args not used
   */
  public static void main(final String[] args) {
    com.ibm.vie.blackjack.casino.Casino.playBlackjackInteractive(MyPlayer.class);
  }
}
