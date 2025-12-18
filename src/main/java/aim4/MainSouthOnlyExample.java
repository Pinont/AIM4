/*
Copyright (c) 2011 Tsz-Chiu Au, Peter Stone
University of Texas at Austin
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the University of Texas at Austin nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package aim4;

import aim4.gui.Viewer;
import aim4.sim.setup.BasicSimSetup;
import aim4.sim.setup.SouthOnlyProtocolSetup;

/**
 * Example main class to demonstrate the South-Only Traffic Protocol.
 * Vehicles enter from the south and exit to north, east, or west directions.
 */
public class MainSouthOnlyExample {

  /**
   * The main function to launch the simulator with south-only protocol.
   *
   * @param args  the command-line arguments
   */
  public static void main(String[] args) {

    // Create basic setup for a 1x1 four-way intersection
    BasicSimSetup basicSetup
      = new BasicSimSetup(1, // columns
                          1, // rows
                          4, // lane width
                          25.0, // speed limit
                          3, // lanes per road
                          1, // median size
                          150, // distance between
                          0.28, // traffic level
                          1.0 // stop distance before intersection
                          );

    // Create the south-only protocol setup
    SouthOnlyProtocolSetup simSetup = new SouthOnlyProtocolSetup(basicSetup);
    
    // Launch the GUI viewer
    new Viewer(simSetup);
  }
}
