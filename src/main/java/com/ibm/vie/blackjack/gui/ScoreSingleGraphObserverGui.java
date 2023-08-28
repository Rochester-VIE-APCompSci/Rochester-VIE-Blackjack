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
package com.ibm.vie.blackjack.gui;

/*
 * 
 * Copyright (c) 2017, 2018 IBM Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

import java.util.LinkedList;
import java.util.List;
import com.ibm.vie.blackjack.casino.hand.VieDealerHand;
import com.ibm.vie.blackjack.casino.hand.ViePlayerHandPayout;
import com.ibm.vie.blackjack.casino.observer.TableObserver;
import com.ibm.vie.blackjack.player.GameInfo;

/**
 * Real time graph for available money. This is just for demo, the result is of
 * very poor quality
 *
 * The observer updates the graph at the end of each round
 *
 * @author ntl
 *
 */
public class ScoreSingleGraphObserverGui implements TableObserver {
	protected StatsLoadingScreen statsLoadingScreen;
	
	private final List<Integer> points = new LinkedList<>();
	private PlotScore scoreGraph;
	private String tableName;

	public ScoreSingleGraphObserverGui(StatsLoadingScreen statsLoadingScreen, String tableName) {
		this.statsLoadingScreen = statsLoadingScreen;
		this.tableName = tableName;
		scoreGraph = null;
	}

	@Override
	public void observeEndOfRound(final GameInfo gameInfo, final VieDealerHand vieDealerHand, final List<ViePlayerHandPayout> hands) {
		if (scoreGraph == null) {
			scoreGraph = new PlotScore(gameInfo.getTableRules(), gameInfo.getTableRules().getMaxNumRounds(), this.statsLoadingScreen);
			scoreGraph.setName(tableName);
			
			scoreGraph.setBounds(35, 100, 950, 600);
			this.statsLoadingScreen.statsGraph = scoreGraph;
			this.statsLoadingScreen.update();
		}

		points.add(gameInfo.getAvailableMoney());
		scoreGraph.addScoreAndRepaint(gameInfo.getAvailableMoney());
	}
}
