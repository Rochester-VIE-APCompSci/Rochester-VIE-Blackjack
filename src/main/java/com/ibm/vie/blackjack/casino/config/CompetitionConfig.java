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

import java.util.LinkedList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.vie.blackjack.casino.BlackjackTableRuleDefaults;
import com.ibm.vie.blackjack.casino.rules.RochesterMnCasinoRules;
import com.ibm.vie.blackjack.player.CasinoRules;

/**
 * Configuration settings for a competition that involves one or more tables (games).
 * 
 * @author ntl
 *
 */
@JsonInclude(Include.NON_DEFAULT)
public class CompetitionConfig {
  private final List<TableConfig> tableConfigs;
  private final CasinoRules casinoRules;
  
  /**
   * 
   * @param tableConfigs - list of table configs for the overall game
   */
  @JsonCreator
  public CompetitionConfig(@JsonProperty("tableConfigs") List<TableConfig> tableConfigs,
                           @JsonProperty("casinoRules") CustomCasinoRules casinoRules) {
    this.tableConfigs = tableConfigs;
    this.casinoRules = (casinoRules == null ? new RochesterMnCasinoRules() : casinoRules);
  }
  
  
  /**
   * Default constructor so that jackson knows what the default values for properties are
   * 
   */
  public CompetitionConfig() {
    tableConfigs = new LinkedList<>();
    casinoRules = new RochesterMnCasinoRules();
  }
  
  /**
   * 
   * @return the casino rules that are in effect for the competition
   * 
   */
  public CasinoRules getCasinoRules()  {
    return casinoRules;
  }
  
  
  /**
   * 
   * @return list of table configuration objects
   */
  public List<TableConfig> getTableConfigs() {
    return tableConfigs;
  }
  
  
}
