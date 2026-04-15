package com.antigravity.race;

import com.antigravity.models.Driver;
import com.antigravity.protocols.CarLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class DriverHeatData extends ServerToClientObject {

  private RaceParticipant driver;
  private Driver actualDriver;

  public static class LapData {

    private double lapTime;
    private String driverId;
    private ArrayList<Double> segments = new ArrayList<>();
    private boolean isDrift;

    @BsonCreator
    public LapData(
        @BsonProperty("lapTime") double lapTime,
        @BsonProperty("driverId") String driverId,
        @BsonProperty("segments") ArrayList<Double> segments,
        @BsonProperty("isDrift") boolean isDrift) {
      this.lapTime = lapTime;
      this.driverId = driverId;
      if (segments != null) {
        this.segments = segments;
      }
      this.isDrift = isDrift;
    }

    public LapData() {}

    public double getLapTime() {
      return lapTime;
    }

    public String getDriverId() {
      return driverId;
    }

    public List<Double> getSegments() {
      return Collections.unmodifiableList(segments);
    }

    public boolean isDrift() {
      return isDrift;
    }
  }

  private final ArrayList<LapData> laps = new ArrayList<>();
  private double bestLapTime = 0.0f;
  private double reactionTime = 0.0f;
  private double pendingLapTime = 0.0f;
  private double initialFuelLevel = 0.0;
  private double gapLeader = 0.0;
  private double gapPosition = 0.0;
  private final ArrayList<Double> segments = new ArrayList<>();
  private CarLocation currentLocation;

  @BsonCreator
  public DriverHeatData(
      @BsonProperty("driver") RaceParticipant driver,
      @BsonProperty("actualDriver") Driver actualDriver) {
    super();
    this.driver = driver;
    if (actualDriver != null) {
      this.actualDriver = actualDriver;
    } else {
      this.actualDriver = driver.getDriver(); // Default to participant driver (null if team)
    }
  }

  public DriverHeatData(RaceParticipant driver) {
    this(driver, null);
  }

  public DriverHeatData() {
    super();
  }

  public RaceParticipant getDriver() {
    return driver;
  }

  public void setDriver(RaceParticipant driver) {
    this.driver = driver;
  }

  public Driver getActualDriver() {
    return actualDriver;
  }

  public void setActualDriver(Driver actualDriver) {
    this.actualDriver = actualDriver;
  }

  public void addLap(double lapTime, boolean isDrift) {
    laps.add(
        new LapData(
            lapTime,
            actualDriver != null ? actualDriver.getEntityId() : "",
            new ArrayList<>(segments),
            isDrift));
    if (bestLapTime == 0.0f || lapTime < bestLapTime) {
      bestLapTime = lapTime;
    }
    segments.clear();
  }

  public void addSegment(double segmentTime) {
    segments.add(segmentTime);
  }

  public List<Double> getSegments() {
    return Collections.unmodifiableList(segments);
  }

  public int getLapCount() {
    return laps.size();
  }

  public List<LapData> getLaps() {
    return Collections.unmodifiableList(laps);
  }

  public double getLastLapTime() {
    if (laps.isEmpty()) {
      return 0.0f;
    }
    return laps.get(laps.size() - 1).getLapTime();
  }

  public double getAverageLapTime() {
    // TODO(aufderheide): Extract the calculation into a utility class
    if (laps.isEmpty()) {
      return 0.0f;
    }
    double sum = 0.0f;
    for (LapData lap : laps) {
      sum += lap.getLapTime();
    }
    return sum / laps.size();
  }

  public double getMedianLapTime() {
    // TODO(aufderheide): Extract the calculation into a utility class
    if (laps.isEmpty()) {
      return 0.0f;
    }
    ArrayList<Double> sortedLaps = new ArrayList<>();
    for (LapData lap : laps) {
      sortedLaps.add(lap.getLapTime());
    }
    Collections.sort(sortedLaps);
    int middle = sortedLaps.size() / 2;
    if (sortedLaps.size() % 2 == 1) {
      return sortedLaps.get(middle);
    } else {
      return (sortedLaps.get(middle - 1) + sortedLaps.get(middle)) / 2.0f;
    }
  }

  public double getBestLapTime() {
    return bestLapTime;
  }

  public double getReactionTime() {
    return reactionTime;
  }

  public void setReactionTime(double reactionTime) {
    this.reactionTime = reactionTime;
  }

  public double getTotalTime() {
    double sum = 0.0f;
    for (LapData lap : laps) {
      sum += lap.getLapTime();
    }
    return sum;
  }

  public void reset() {
    laps.clear();
    segments.clear();
    bestLapTime = 0.0f;
    reactionTime = 0.0f;
    pendingLapTime = 0.0f;
    gapLeader = 0.0;
    gapPosition = 0.0;
  }

  public double getPendingLapTime() {
    return pendingLapTime;
  }

  public void setPendingLapTime(double pendingLapTime) {
    this.pendingLapTime = pendingLapTime;
  }

  public double getInitialFuelLevel() {
    return initialFuelLevel;
  }

  public void setInitialFuelLevel(double initialFuelLevel) {
    this.initialFuelLevel = initialFuelLevel;
  }

  public void addPendingLapTime(double lapTime) {
    this.pendingLapTime += lapTime;
  }

  public double getGapLeader() {
    return gapLeader;
  }

  public void setGapLeader(double gapLeader) {
    this.gapLeader = gapLeader;
  }

  public double getGapPosition() {
    return gapPosition;
  }

  public void setGapPosition(double gapPosition) {
    this.gapPosition = gapPosition;
  }

  public CarLocation getCurrentLocation() {
    return currentLocation;
  }

  public void setCurrentLocation(CarLocation currentLocation) {
    this.currentLocation = currentLocation;
  }
}
