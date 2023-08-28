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
 /** This package defines interfaces and classes that you will use to interact with the IBM VIE
 * Blackjack framework.
 * 
 * 
 * <p>
 * Your strategy will implement the {@link com.ibm.vie.blackjack.player.PlayerStrategy
 * PlayerStrategy} interface. See {@link student.player.MyPlayer} for a starting implementation
 * that you can improve with your own solution.
 * </p>
 * <p>
 * Other classes are provided to your strategy via the game framework; these classes provide
 * essential information about the current state of the game.
 * </p>
 * 
 * <p>
 * An important class is {@link com.ibm.vie.blackjack.player.GameInfo GameInfo}, which defines
 * current information about the game. This includes the amount of available money, and the
 * important rules of the game, such as the minimum and maximum bet amounts.
 * </p>
 * <p>
 * The framework also provides information about the player and dealer hands.
 * </p>
 * <p>
 * The {@link com.ibm.vie.blackjack.player.Hand Hand} class provides information about
 * the cards in a hand, including the point score for the hand. This class has a number of
 * subclasses that provide context specific information; for example a
 * {@link com.ibm.vie.blackjack.player.PlayerHand PlayerHand} includes how much bet was paid by the
 * player, and a {@link com.ibm.vie.blackjack.player.PlayerPayoutHand PlayerPayoutHand} includes
 * information about how much the player won for the hand. Instances of these subclasses are
 * provided at points in the game where they make sense. For example PlayerPayoutHand will not be
 * provided until the end of the round. The method signatures of the PlayerStrategy will clearly
 * define which specific type of hand is being provided to you at each point in the game.
 * </p>
 * <H2>Class Hierarchy for the Hand class</H2>
 * <CENTER>
 * <img src="doc-files/hand-class-diag.jpg" alt="class diagram for Hand">
 * </CENTER> 
 * 
 * 
 * 

 */
package com.ibm.vie.blackjack.player;
