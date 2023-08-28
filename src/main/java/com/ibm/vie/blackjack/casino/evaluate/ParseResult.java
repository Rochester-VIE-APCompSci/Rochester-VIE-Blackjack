/*

  Copyright (c) 2017, 2018 IBM Corporation
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

 */
package com.ibm.vie.blackjack.casino.evaluate;

import java.io.File;
import com.ibm.vie.blackjack.casino.config.CompetitionConfig;

public class ParseResult {
  private final File competitionConfigFile;
  private final File directoryOfJars;
  private final File resultCsv;

  protected ParseResult(final File directoryOfJars, final File resultCsv,
      final File competitionConfig) {
    this.directoryOfJars = directoryOfJars;
    this.resultCsv = resultCsv;
    this.competitionConfigFile = competitionConfig;
  }

  public File getConfigFile() {
    return competitionConfigFile;
  }

  public File getDirectoryOfJars() {
    return this.directoryOfJars;
  }

  public File getResultCsv() {
    return resultCsv;
  }
}