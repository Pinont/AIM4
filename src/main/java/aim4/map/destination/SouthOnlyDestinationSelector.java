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
package aim4.map.destination;

import java.util.List;
import java.util.ArrayList;

import aim4.map.BasicMap;
import aim4.map.GridMap;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.util.Util;

/**
 * A destination selector that only allows vehicles from the south
 * and distributes them equally to north, east, and west destinations.
 */
public class SouthOnlyDestinationSelector implements DestinationSelector {

  /////////////////////////////////
  // PRIVATE FIELDS
  /////////////////////////////////

  /**
   * The set of roads that a vehicle can use as an ultimate destination.
   */
  private List<Road> destinationRoads;
  
  /**
   * The grid map to access roads for better filtering.
   */
  private GridMap gridMap;

  /////////////////////////////////
  // CLASS CONSTRUCTORS
  /////////////////////////////////

  /**
   * Create a new SouthOnlyDestinationSelector.
   *
   * @param map  the map from which to create the selector
   */
  public SouthOnlyDestinationSelector(BasicMap map) {
    destinationRoads = map.getDestinationRoads();
    // If it's a GridMap, store it for road-based filtering
    if (map instanceof GridMap) {
      this.gridMap = (GridMap) map;
    }
  }

  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public Road selectDestination(Lane currentLane) {
    // Only allow vehicles from the south (from the bottom road)
    // and distribute them to the other three directions (north, east, west)
    
    System.out.println("[DEBUG] selectDestination called for lane: " + currentLane.getId());
    System.out.println("[DEBUG] Available destination roads: " + destinationRoads.size());
    
    if (destinationRoads == null || destinationRoads.isEmpty()) {
      System.out.println("[DEBUG] No destination roads available!");
      return null;
    }
    
    // If we have GridMap, use it to find current road and exclude it + dual
    if (gridMap != null) {
      List<Road> availableRoads = new ArrayList<>();
      List<Road> allRoads = gridMap.getRoads();
      
      // Get the lane's current road by checking which road contains this lane
      Road currentRoad = null;
      for (Road road : allRoads) {
        if (road.getLanes().contains(currentLane)) {
          currentRoad = road;
          break;
        }
      }
      
      System.out.println("[DEBUG] Current road found: " + (currentRoad != null ? "yes" : "no"));
      
      // Add all destination roads except the current one and its dual
      for (Road road : destinationRoads) {
        boolean isCurrent = (currentRoad != null && road.equals(currentRoad));
        boolean isDual = (currentRoad != null && currentRoad.getDual() != null && 
                         road.equals(currentRoad.getDual()));
        
        if (!isCurrent && !isDual) {
          availableRoads.add(road);
          System.out.println("[DEBUG] Added destination road");
        }
      }
      
      System.out.println("[DEBUG] Available roads after filtering current/dual: " + availableRoads.size());
      
      if (!availableRoads.isEmpty()) {
        // Randomly select one of the available destinations
        int randomIndex = Util.random.nextInt(availableRoads.size());
        Road selected = availableRoads.get(randomIndex);
        System.out.println("[DEBUG] Selected road from " + availableRoads.size() + " available");
        return selected;
      }
    }
    
    // Fallback: return random destination road
    int randomIndex = Util.random.nextInt(destinationRoads.size());
    System.out.println("[DEBUG] Using fallback, selected road index: " + randomIndex);
    return destinationRoads.get(randomIndex);
  }

}

