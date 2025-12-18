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
package aim4.sim.setup;

import aim4.config.Debug;
import aim4.config.SimConfig;
import aim4.im.v2i.batch.RoadBasedReordering;
import aim4.im.v2i.reservation.ReservationGridManager;
import aim4.map.GridMap;
import aim4.map.GridMapUtil;
import aim4.map.Road;
import aim4.map.SpawnPoint;
import aim4.map.destination.SouthOnlyDestinationSelector;
import aim4.sim.AutoDriverOnlySimulator;
import aim4.sim.Simulator;
import java.util.List;

/**
 * The setup for a four-way intersection with traffic only from the south.
 * Vehicles enter from the south and exit to north, east, or west directions.
 */
public class SouthOnlyProtocolSetup extends BasicSimSetup {

  /////////////////////////////////
  // PRIVATE FIELDS
  /////////////////////////////////

  /** Whether the base line mode is on */
  private boolean isBaseLineMode = false;
  /** Whether the batch mode is on */
  private boolean isBatchMode = false;
  /** The static buffer size */
  private double staticBufferSize = 0.25;
  /** The time buffer for internal tiles */
  private double internalTileTimeBufferSize = 0.1;
  /** The time buffer for edge tiles */
  private double edgeTileTimeBufferSize = 0.25;
  /** Whether the edge time buffer is enabled */
  private boolean isEdgeTileTimeBufferEnabled = true;
  /** The granularity of the reservation grid */
  private double granularity = 1.0;
  /** The processing interval for the batch mode */
  private double processingInterval = RoadBasedReordering.DEFAULT_PROCESSING_INTERVAL;
  /** The grid map used for spawn point detection */
  private GridMap layout;

  /////////////////////////////////
  // CONSTRUCTORS
  /////////////////////////////////

  /**
   * Create a setup for the simulator with south-only traffic protocol.
   *
   * @param basicSimSetup  the basic simulator setup
   */
  public SouthOnlyProtocolSetup(BasicSimSetup basicSimSetup) {
    super(basicSimSetup);
  }

  /**
   * Create a setup for the simulator with south-only traffic protocol.
   *
   * @param columns                     the number of columns
   * @param rows                        the number of rows
   * @param laneWidth                   the width of lanes
   * @param speedLimit                  the speed limit
   * @param lanesPerRoad                the number of lanes per road
   * @param medianSize                  the median size
   * @param distanceBetween             the distance between intersections
   * @param trafficLevel                the traffic level
   * @param stopDistBeforeIntersection  the stopping distance before
   *                                    intersections
   */
  public SouthOnlyProtocolSetup(int columns, int rows,
                                double laneWidth,
                                double speedLimit,
                                int lanesPerRoad,
                                double medianSize,
                                double distanceBetween,
                                double trafficLevel,
                                double stopDistBeforeIntersection) {
    super(columns, rows, laneWidth, speedLimit, lanesPerRoad,
          medianSize, distanceBetween, trafficLevel,
          stopDistBeforeIntersection);
  }

  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  /**
   * Turn on or off the base line mode.
   *
   * @param b  Whether the base line mode is on
   */
  public void setIsBaseLineMode(boolean b) {
    isBaseLineMode = b;
  }

  /**
   * Turn on or off the batch mode.
   *
   * @param b  Whether the batch mode is on
   */
  public void setIsBatchMode(boolean b) {
    isBatchMode = b;
  }

  /**
   * Set the processing interval in the batch mode.
   *
   * @param processingInterval  the processing interval
   */
  public void setBatchModeProcessingInterval(double processingInterval) {
    this.processingInterval = processingInterval;
  }

  /**
   * Set the buffer sizes.
   *
   * @param staticBufferSize             the static buffer size
   * @param internalTileTimeBufferSize   the time buffer size of internal tiles
   * @param edgeTileTimeBufferSize       the time buffer size of edge tiles
   * @param isEdgeTileTimeBufferEnabled  whether the edge time buffer is
   *                                     enabled
   * @param granularity                  the granularity of the simulation grid
   */
  public void setBuffers(double staticBufferSize,
                         double internalTileTimeBufferSize,
                         double edgeTileTimeBufferSize,
                         boolean isEdgeTileTimeBufferEnabled,
                         double granularity) {
    this.staticBufferSize = staticBufferSize;
    this.internalTileTimeBufferSize = internalTileTimeBufferSize;
    this.edgeTileTimeBufferSize = edgeTileTimeBufferSize;
    this.isEdgeTileTimeBufferEnabled = isEdgeTileTimeBufferEnabled;
    this.granularity = granularity;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Simulator getSimulator() {
    System.out.println("[DEBUG] SouthOnlyProtocolSetup.getSimulator() called");
    double currentTime = 0.0;
    GridMap layout = new GridMap(currentTime,
                                       numOfColumns,
                                       numOfRows,
                                       laneWidth,
                                       speedLimit,
                                       lanesPerRoad,
                                       medianSize,
                                       distanceBetween);

    ReservationGridManager.Config gridConfig =
      new ReservationGridManager.Config(SimConfig.TIME_STEP,
                                        SimConfig.GRID_TIME_STEP,
                                        staticBufferSize,
                                        internalTileTimeBufferSize,
                                        edgeTileTimeBufferSize,
                                        isEdgeTileTimeBufferEnabled,
                                        granularity);

    Debug.SHOW_VEHICLE_COLOR_BY_MSG_STATE = true;

    if (!isBaseLineMode) {
      System.out.println("[DEBUG] isBaseLineMode = false, setting south-only spawn points");
      if (isBatchMode) {
        GridMapUtil.setBatchManagers(layout, currentTime, gridConfig,
                                        processingInterval);
      } else {
        GridMapUtil.setFCFSManagers(layout, currentTime, gridConfig);
      }

      // Set spawn points only for south direction vehicles
      setSouthOnlySpawnPoints(layout);
    } else {
      System.out.println("[DEBUG] isBaseLineMode = true, using baseline spawn points");
      GridMapUtil.setBaselineSpawnPoints(layout, 1.0);
    }

    return new AutoDriverOnlySimulator(layout);
  }

  /**
   * Set spawn points for south-only traffic protocol.
   * Only the south spawn points are active, vehicles exit to north, east, or west.
   *
   * @param gridMap  the map
   */
  private void setSouthOnlySpawnPoints(GridMap gridMap) {
    // Store the layout for use in isSouthSpawnPoint
    this.layout = gridMap;
    
    // Debug: print all destination roads and their positions
    System.out.println("=== DESTINATION ROADS DEBUG ===");
    List<Road> destRoads = gridMap.getDestinationRoads();
    for (int i = 0; i < destRoads.size(); i++) {
      Road r = destRoads.get(i);
      System.out.println("Road " + i + ": " + destRoads.indexOf(r) + " lanes: " + r.getLanes().size());
    }
    System.out.println("==============================");
    
    // Get all spawn points
    List<SpawnPoint> allSpawnPoints = gridMap.getSpawnPoints();

    System.out.println("=== SPAWN POINT DEBUG ===");
    System.out.println("Total spawn points: " + allSpawnPoints.size());
    
    int activatedCount = 0;
    int deactivatedCount = 0;
    
    for (SpawnPoint sp : allSpawnPoints) {
      boolean isSouth = isSouthSpawnPoint(sp);
      
      // Get lane and road info for debug
      aim4.map.lane.Lane lane = sp.getLane();
      String laneInfo = "Lane ID: " + lane.getId();
      
      System.out.println("SpawnPoint: " + String.format("(%.1f, %.1f)", 
        sp.getPosition().getX(), sp.getPosition().getY()) + 
        " - " + laneInfo + " - South? " + isSouth);
      
      // Check if this spawn point is on a south road (bottom edge)
      if (isSouth) {
        // Activate south spawn points with the south-only destination selector
        sp.setVehicleSpecChooser(
          new GridMapUtil.UniformSpawnSpecGenerator(
            trafficLevel,
            new SouthOnlyDestinationSelector(gridMap)));
        activatedCount++;
      } else {
        // Deactivate all other spawn points (set them to zero traffic)
        sp.setVehicleSpecChooser(
          new GridMapUtil.UniformSpawnSpecGenerator(0.0,
            new SouthOnlyDestinationSelector(gridMap)));
        deactivatedCount++;
      }
    }
    
    System.out.println("Activated: " + activatedCount + ", Deactivated: " + deactivatedCount);
    System.out.println("========================");
  }

  /**
   * Check if a spawn point is on the south road.
   *
   * @param spawnPoint  the spawn point to check
   * @return true if this spawn point is on the south road
   */
  private boolean isSouthSpawnPoint(SpawnPoint spawnPoint) {
    // Get the position of the spawn point
    double spawnY = spawnPoint.getPosition().getY();

    // Find the max Y coordinate of all spawn points
    List<SpawnPoint> allSpawnPoints = layout.getSpawnPoints();

    double maxY = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE;
    for (SpawnPoint sp : allSpawnPoints) {
      double y = sp.getPosition().getY();
      maxY = Math.max(maxY, y);
      minY = Math.min(minY, y);
    }

    // South spawn points should be at maximum Y (bottom in screen coordinates)
    // Check if spawn point is in the bottom 25% of the range
    double range = maxY - minY;
    double threshold = maxY - (range * 0.1); // Top 10% is considered south
    
    return spawnY >= threshold;
  }
}
