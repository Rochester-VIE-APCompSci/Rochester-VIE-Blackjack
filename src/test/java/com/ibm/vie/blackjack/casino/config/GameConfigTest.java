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
package com.ibm.vie.blackjack.casino.config;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.vie.blackjack.casino.Table;
import com.ibm.vie.blackjack.player.Card;
import com.ibm.vie.blackjack.player.DealerHand;
import com.ibm.vie.blackjack.player.GameInfo;
import com.ibm.vie.blackjack.player.PlayerDecision;
import com.ibm.vie.blackjack.player.PlayerHand;
import com.ibm.vie.blackjack.player.PlayerPayoutHand;
import com.ibm.vie.blackjack.player.PlayerStrategy;

public class GameConfigTest {

	private static ObjectMapper mapper = new ObjectMapper();

	@Test
	public void when_game_config_loaded_then_tables_are_correct()
			throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		final String configJson = "{ \"tableConfigs\" : ["
				+ "{ \"initialMoney\":100, \"minBet\":50, \"maxBet\":51, \"numDecks\":6, \"deckNumber\":12345, \"numRounds\":5},"
				+ "{ \"initialMoney\":1100, \"minBet\":150, \"maxBet\":151, \"numDecks\":16, \"deckNumber\":112345, \"numRounds\":15}"
				+ "] }";

		CompetitionConfig config = mapper.readValue(configJson, CompetitionConfig.class);

		PlayerStrategy strategy = new PlayerStrategy() {

			@Override
			public int placeInitialBet(GameInfo gameInfo) {
				return gameInfo.getMaxBet();
			}

			@Override
			public PlayerDecision decideHowToPlayHand(GameInfo gameInfo, PlayerHand currentHand, List<PlayerHand> playerHands,
					Card dealerUpCard) {
				return PlayerDecision.STAND;
			}

			@Override
			public boolean decideToWalkAway(GameInfo gameInfo, List<PlayerPayoutHand> playerHands, DealerHand dealerHand) {
				return false;
			}

			@Override
			public String getStudentName() {
				return "Student Name";
			}

		};

		Table table = config.getTableConfigs().get(0).getTable(strategy, config.getCasinoRules());

		Assert.assertTrue(table.getRules().getInitialMoney() == 100);
		Assert.assertTrue(table.getRules().getMaxNumRounds() == 5);
		Assert.assertTrue(table.getRules().getMaxBet() == 51);
		Assert.assertTrue(table.getRules().getMinBet() == 50);
		Assert.assertTrue(table.getRules().getNumberOfDecks() == 6);

		Table table2 = config.getTableConfigs().get(1).getTable(strategy, config.getCasinoRules());

		Assert.assertTrue(table2.getRules().getInitialMoney() == 1100);
		Assert.assertTrue(table2.getRules().getMaxNumRounds() == 15);
		Assert.assertTrue(table2.getRules().getMaxBet() == 151);
		Assert.assertTrue(table2.getRules().getMinBet() == 150);
		Assert.assertTrue(table2.getRules().getNumberOfDecks() == 16);

	}

	
	@Test
    public void when_config_serialized_with_default_rules_then_rules_are_not_included()
            throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String configJson = "{ "
                + "\"tableConfigs\" : ["
                + "{ \"initialMoney\":100, \"minBet\":50, \"maxBet\":51, \"numDecks\":6, \"deckNumber\":12345, \"numRounds\":5},"
                + "{ \"initialMoney\":1100, \"minBet\":150, \"maxBet\":151, \"numDecks\":16, \"deckNumber\":112345, \"numRounds\":15}"
                + "] }";

        CompetitionConfig config = mapper.readValue(configJson, CompetitionConfig.class);

        final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
        Assert.assertFalse(json.contains("casinoRulesClassName") || json.contains("RochesterMnCasinoRules"));

    }
	
}
