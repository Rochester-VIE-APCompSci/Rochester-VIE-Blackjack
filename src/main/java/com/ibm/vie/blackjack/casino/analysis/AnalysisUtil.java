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
package com.ibm.vie.blackjack.casino.analysis;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Utilities for analysis routines
 * 
 * @author ntl
 *
 */
public class AnalysisUtil {

  /**
   * Calculate a confidence interval
   *
   * @param stats
   * @param level
   * 
   * @return the value to add or remove to the mean to determine the confidence interval
   */
  public static double calcMeanCI(final DescriptiveStatistics stats, final double level) {
    try {
      final TDistribution tDist = new TDistribution(stats.getN() - 1);
      final double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - level) / 2);
      return critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN());
    } catch (final MathIllegalArgumentException e) {
      return Double.NaN;
    }
  
  }

}
