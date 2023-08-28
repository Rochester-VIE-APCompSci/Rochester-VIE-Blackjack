/*
 *
 * Copyright (c) 2019 IBM Corporation
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
package com.ibm.vie.blackjack.casino.evaluate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.vie.blackjack.casino.config.CompetitionConfig;

public class Parser {
  private final Options options;
  private final String [] args;


  public Parser(final String [] args) {
    this.args = args;
    options = new Options();
    options.addRequiredOption("d", "directory", true, "root directory of jars") //
        .addRequiredOption("r", "resultFile", true, "path to the output csv file") //
        .addRequiredOption("c", "configFile", true, "path to the competition config file");
  }

  /**
   * Loads the configuration to evaluate from a file
   *
   * @param fileName
   * @return the config
   *
   * @throws IOException
   */
  private final CompetitionConfig loadCompetitionConfig(final String fileName) throws IOException {
    try (InputStream configAsJsonStream = new FileInputStream(fileName)) {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(configAsJsonStream, CompetitionConfig.class);
    }
  }

  public ParseResult parse() throws ParseException, IOException {
    final CommandLineParser cmdLineParser = new DefaultParser();

    final CommandLine line = cmdLineParser.parse(options, args);
    final File jarDirectory = new File(line.getOptionValue('d'));
    if (!jarDirectory.exists() || !jarDirectory.isDirectory()) {
      throw new FileNotFoundException(
          "The file " + jarDirectory.getAbsolutePath() + " does not exist or is not a directory");
    }
    
    final File competitionConfig = new File(line.getOptionValue('c'));
    if (!competitionConfig.exists() || !competitionConfig.getName().endsWith(".json")) {
      throw new FileNotFoundException(
          "The file " + competitionConfig.getAbsolutePath() + " does not exist or is not a json file"
          );
    } else {
      // this forces a parse of the config to ensure that the json is valid
      try {
        loadCompetitionConfig(competitionConfig.getAbsolutePath());
      } catch (IOException e) {
        throw new IllegalArgumentException("The competition config is not valid", e);
      }
    }
    
    
    final File resultFile = new File(line.getOptionValue('r'));

    return new ParseResult(jarDirectory, resultFile, competitionConfig);

  }


  public void printUsage(final PrintWriter pw, final int width, final String cmdLineSyntax) {
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(pw, width, cmdLineSyntax, "Run blackjack and produce csv", this.options, 2, 2, "");
  }


}
