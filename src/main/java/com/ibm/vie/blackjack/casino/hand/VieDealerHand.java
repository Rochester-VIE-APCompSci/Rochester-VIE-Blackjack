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

import com.ibm.vie.blackjack.casino.card.VieCard;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.Hand;

/**
 * Represents the dealer's hand.
 *
 * In the US version of blackjack, the dealer is dealt two cards, one of which is face down. This
 * class represents the dealer's view of the hand, which includes the face down card.
 * {@link #getDealerUpCardInfo()} can be used to create a {@link Hand} object that represents the
 * dealer's hand considering only the up card.
 *
 * @author ntl
 *
 */
public class VieDealerHand extends VieHand {

  /**
   * Build's the dealers hand from cards
   *
   * @param cards
   */
  public VieDealerHand(final VieCard... cards) {
    super(cards);
  }


  /**
   *
   * @return information about the dealer's up card.
   */
  public Card getDealerUpCardInfo() {
    // the up card is always the first card in the dealer's hand
    return getCards().get(0).toCard();
  }


  /**
   * 
   * @return - the dealer's up card
   */
  public VieCard getDealerUpCard() {
    return getCards().get(0);
  }
  
  
  /**
   * Returns information about the entire dealer's hand. Useful for the end of the round after the
   * dealer has played.
   *
   * @return information about the dealer's hand
   */
  public DealerHand toDealerHand() {
    return new DealerHand(getScoreAceAs1(), getScore(), VieCard.toCardList(getCards()));
  }


}
