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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides a thread safe mechanism to reroute all standard output to dev/null
 * 
 * @author ntl
 *
 */
public class StdOutputRerouter {
  private static AtomicInteger reroutes = new AtomicInteger(0);
  private static PrintStream originalStdOut = System.out;
  
  public static synchronized void reroute() {
    int count = reroutes.incrementAndGet();
    
    if (count==1) {
      System.setOut(new PrintStream(new OutputStream() {
        public void write(int b) {
          // DO NOTHING
        }
      }));
    }      
  }
  
  
  public static void println(final String str) {
    synchronized (originalStdOut) {
      originalStdOut.println(str);
    }
  }
  
  public static synchronized void restore() {
    int count = reroutes.decrementAndGet();
    
    if (count == 0) {
      System.setOut(originalStdOut);
    }
  }
  
  
}