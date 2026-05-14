package com.antigravity.race;

import com.antigravity.models.Driver;
import com.antigravity.models.GlobalStatistics;
import com.antigravity.models.OverallScoring.OverallRanking;
import com.antigravity.proto.CurrentRecords;
import com.antigravity.proto.OverallRecords;
import com.antigravity.proto.RaceData;
import com.antigravity.proto.RecordData;
import com.antigravity.proto.RecordEntry;
import com.antigravity.race.states.RaceOver;
import com.antigravity.service.DatabaseService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaceRecords {
  private static final Logger logger = LoggerFactory.getLogger(RaceRecords.class);

  private final Race race;

  // Record tracking - Overall (All-time)
  private double overallFastestLap = Double.MAX_VALUE;
  private String overallFastestLapHolder = "";
  private String overallFastestLapHolderNickname = "";
  private String overallFastestLapHolderTeamName = "";
  private long overallFastestLapDate = 0;

  private double overallHighestScore = 0;
  private String overallHighestScoreHolder = "";
  private String overallHighestScoreHolderNickname = "";
  private String overallHighestScoreHolderTeamName = "";
  private long overallHighestScoreDate = 0;

  private List<Double> overallLaneFastestLapTimes = new ArrayList<>();
  private List<String> overallLaneFastestLapHolders = new ArrayList<>();
  private List<String> overallLaneFastestLapHolderNicknames = new ArrayList<>();
  private List<String> overallLaneFastestLapHolderTeamNames = new ArrayList<>();
  private List<Long> overallLaneFastestLapDates = new ArrayList<>();

  private List<Double> overallLaneHighestScores = new ArrayList<>();
  private List<String> overallLaneHighestScoreHolders = new ArrayList<>();
  private List<String> overallLaneHighestScoreHolderNicknames = new ArrayList<>();
  private List<String> overallLaneHighestScoreHolderTeamNames = new ArrayList<>();
  private List<Long> overallLaneHighestScoreDates = new ArrayList<>();

  // Record tracking - Current Race
  private double raceFastestLap = Double.MAX_VALUE;
  private String raceFastestLapHolder = "";
  private String raceFastestLapHolderNickname = "";
  private String raceFastestLapHolderTeamName = "";

  private double raceHighestScore = 0;
  private String raceHighestScoreHolder = "";
  private String raceHighestScoreHolderNickname = "";
  private String raceHighestScoreHolderTeamName = "";

  private List<Double> raceLaneFastestLapTimes = new ArrayList<>();
  private List<String> raceLaneFastestLapHolders = new ArrayList<>();
  private List<String> raceLaneFastestLapHolderNicknames = new ArrayList<>();
  private List<String> raceLaneFastestLapHolderTeamNames = new ArrayList<>();

  private List<Double> raceLaneHighestScores = new ArrayList<>();
  private List<String> raceLaneHighestScoreHolders = new ArrayList<>();
  private List<String> raceLaneHighestScoreHolderNicknames = new ArrayList<>();
  private List<String> raceLaneHighestScoreHolderTeamNames = new ArrayList<>();

  // Record tracking - Current Heat
  private double heatFastestLap = Double.MAX_VALUE;
  private String heatFastestLapHolder = "";
  private String heatFastestLapHolderNickname = "";
  private String heatFastestLapHolderTeamName = "";

  private GlobalStatistics baseStatistics;

  public RaceRecords(Race race) {
    this.race = race;
  }

  public void resetHeatRecords() {
    this.heatFastestLap = Double.MAX_VALUE;
    this.heatFastestLapHolder = "";
    this.heatFastestLapHolderNickname = "";
    this.heatFastestLapHolderTeamName = "";
  }

  public void initializeLaneRecords() {
    int laneCount = race.getTrack().getLanes().size();
    boolean isTimeBased = race.isTimeBasedRanking();

    overallLaneFastestLapTimes = new ArrayList<>(laneCount);
    overallLaneFastestLapHolders = new ArrayList<>(laneCount);
    overallLaneFastestLapHolderNicknames = new ArrayList<>(laneCount);
    overallLaneFastestLapHolderTeamNames = new ArrayList<>(laneCount);
    overallLaneFastestLapDates = new ArrayList<>(laneCount);
    overallLaneHighestScores = new ArrayList<>(laneCount);
    overallLaneHighestScoreHolders = new ArrayList<>(laneCount);
    overallLaneHighestScoreHolderNicknames = new ArrayList<>(laneCount);
    overallLaneHighestScoreHolderTeamNames = new ArrayList<>(laneCount);
    overallLaneHighestScoreDates = new ArrayList<>(laneCount);

    raceLaneFastestLapTimes = new ArrayList<>(laneCount);
    raceLaneFastestLapHolders = new ArrayList<>(laneCount);
    raceLaneFastestLapHolderNicknames = new ArrayList<>(laneCount);
    raceLaneFastestLapHolderTeamNames = new ArrayList<>(laneCount);
    raceLaneHighestScores = new ArrayList<>(laneCount);
    raceLaneHighestScoreHolders = new ArrayList<>(laneCount);
    raceLaneHighestScoreHolderNicknames = new ArrayList<>(laneCount);
    raceLaneHighestScoreHolderTeamNames = new ArrayList<>(laneCount);

    for (int i = 0; i < laneCount; i++) {
      overallLaneFastestLapTimes.add(Double.MAX_VALUE);
      overallLaneFastestLapHolders.add("");
      overallLaneFastestLapHolderNicknames.add("");
      overallLaneFastestLapHolderTeamNames.add("");
      overallLaneFastestLapDates.add(0L);
      overallLaneHighestScores.add(isTimeBased ? Double.MAX_VALUE : 0.0);
      overallLaneHighestScoreHolders.add("");
      overallLaneHighestScoreHolderNicknames.add("");
      overallLaneHighestScoreHolderTeamNames.add("");
      overallLaneHighestScoreDates.add(0L);

      raceLaneFastestLapTimes.add(Double.MAX_VALUE);
      raceLaneFastestLapHolders.add("");
      raceLaneFastestLapHolderNicknames.add("");
      raceLaneFastestLapHolderTeamNames.add("");
      raceLaneHighestScores.add(isTimeBased ? Double.MAX_VALUE : 0.0);
      raceLaneHighestScoreHolders.add("");
      raceLaneHighestScoreHolderNicknames.add("");
      raceLaneHighestScoreHolderTeamNames.add("");
    }
  }

  public void loadGlobalRecords() {
    boolean isTimeBased = race.isTimeBasedRanking();
    if (race.getDatabaseContext() == null) {
      initializeLaneRecords();
      return;
    }

    try {
      DatabaseService dbService = DatabaseService.getInstance();
      GlobalStatistics stats =
          dbService.getGlobalStatistics(
              race.getDatabaseContext().getDatabase(),
              race.getRaceModel().getEntityId(),
              race.isDemoMode());
      this.baseStatistics = stats;
      if (stats != null) {
        this.overallFastestLap = stats.getFastestLapTime();
        this.overallFastestLapHolder = stats.getFastestLapDriverName();
        this.overallFastestLapHolderNickname = stats.getFastestLapDriverNickname();
        this.overallFastestLapHolderTeamName = stats.getFastestLapTeamName();
        this.overallFastestLapDate = stats.getFastestLapDate();
        this.overallHighestScore = stats.getHighestScore();
        if (isTimeBased && this.overallHighestScore == 0)
          this.overallHighestScore = Double.MAX_VALUE;
        this.overallHighestScoreHolder = stats.getHighestScoreHolderName();
        this.overallHighestScoreHolderNickname = stats.getHighestScoreHolderNickname();
        this.overallHighestScoreHolderTeamName = stats.getHighestScoreTeamName();
        this.overallHighestScoreDate = stats.getHighestScoreDate();
        initializeLaneRecords();
        updateOverallLaneRecordsFromStats(stats);
      } else {
        initializeLaneRecords();
      }
    } catch (Exception e) {
      logger.error("Failed to load global statistics", e);
      initializeLaneRecords();
    }
  }

  private void updateOverallLaneRecordsFromStats(GlobalStatistics stats) {
    int laneCount = race.getTrack().getLanes().size();
    for (int i = 0; i < laneCount; i++) {
      if (stats.getLaneFastestLapTimes() != null && i < stats.getLaneFastestLapTimes().size())
        overallLaneFastestLapTimes.set(i, stats.getLaneFastestLapTimes().get(i));
      if (stats.getLaneFastestLapDriverNames() != null
          && i < stats.getLaneFastestLapDriverNames().size())
        overallLaneFastestLapHolders.set(i, stats.getLaneFastestLapDriverNames().get(i));
      if (stats.getLaneFastestLapDriverNicknames() != null
          && i < stats.getLaneFastestLapDriverNicknames().size())
        overallLaneFastestLapHolderNicknames.set(
            i, stats.getLaneFastestLapDriverNicknames().get(i));
      if (stats.getLaneFastestLapTeamNames() != null
          && i < stats.getLaneFastestLapTeamNames().size())
        overallLaneFastestLapHolderTeamNames.set(i, stats.getLaneFastestLapTeamNames().get(i));
      if (stats.getLaneFastestLapDates() != null && i < stats.getLaneFastestLapDates().size())
        overallLaneFastestLapDates.set(i, stats.getLaneFastestLapDates().get(i));
      if (stats.getLaneHighestScores() != null && i < stats.getLaneHighestScores().size())
        overallLaneHighestScores.set(i, stats.getLaneHighestScores().get(i));
      if (stats.getLaneHighestScoreHolderNames() != null
          && i < stats.getLaneHighestScoreHolderNames().size())
        overallLaneHighestScoreHolders.set(i, stats.getLaneHighestScoreHolderNames().get(i));
      if (stats.getLaneHighestScoreHolderNicknames() != null
          && i < stats.getLaneHighestScoreHolderNicknames().size())
        overallLaneHighestScoreHolderNicknames.set(
            i, stats.getLaneHighestScoreHolderNicknames().get(i));
      if (stats.getLaneHighestScoreTeamNames() != null
          && i < stats.getLaneHighestScoreTeamNames().size())
        overallLaneHighestScoreHolderTeamNames.set(i, stats.getLaneHighestScoreTeamNames().get(i));
      if (stats.getLaneHighestScoreDates() != null && i < stats.getLaneHighestScoreDates().size())
        overallLaneHighestScoreDates.set(i, stats.getLaneHighestScoreDates().get(i));
    }
  }

  public void recalculateScoreRecords() {
    long timestamp = System.currentTimeMillis();
    boolean isTimeBased = race.isTimeBasedRanking();
    resetRaceSessionRecords(isTimeBased);
    recalculateRaceBestScore(isTimeBased);
    recalculateRaceLaneBestScores(isTimeBased);
    resetRaceFastestLapRecords();
    recalculateRaceBestLap();
    recalculateRaceLaneBestLaps();
    recalculateOverallRecords(timestamp);
  }

  private void resetRaceSessionRecords(boolean isTimeBased) {
    raceHighestScore = isTimeBased ? Double.MAX_VALUE : 0;
    raceHighestScoreHolder = "";
    raceHighestScoreHolderNickname = "";
    raceHighestScoreHolderTeamName = "";
    for (int i = 0; i < raceLaneHighestScores.size(); i++) {
      raceLaneHighestScores.set(i, isTimeBased ? Double.MAX_VALUE : 0.0);
      raceLaneHighestScoreHolders.set(i, "");
      raceLaneHighestScoreHolderNicknames.set(i, "");
      raceLaneHighestScoreHolderTeamNames.set(i, "");
    }
  }

  private void recalculateRaceBestScore(boolean isTimeBased) {
    for (RaceParticipant p : race.getDrivers()) {
      double score = p.getRankValue();
      if (score <= 0) continue;
      if (isTimeBased ? (score < raceHighestScore) : (score > raceHighestScore))
        updateRaceBestScore(p, score);
    }
  }

  private void updateRaceBestScore(RaceParticipant p, double score) {
    raceHighestScore = score;
    raceHighestScoreHolder = p.getDriver().getName();
    raceHighestScoreHolderNickname = p.getDriver().getNickname();
    if (race.getCurrentHeat() != null) {
      for (DriverHeatData dhd : race.getCurrentHeat().getDrivers()) {
        if (dhd.getDriver() == p) {
          Driver actualDriver = dhd.getActualDriver();
          if (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER) {
            raceHighestScoreHolder = actualDriver.getName();
            raceHighestScoreHolderNickname = actualDriver.getNickname();
          }
          break;
        }
      }
    }
    if (raceHighestScoreHolderNickname == null || raceHighestScoreHolderNickname.isEmpty())
      raceHighestScoreHolderNickname = raceHighestScoreHolder;
    raceHighestScoreHolderTeamName = p.getTeam() != null ? p.getTeam().getName() : "";
  }

  private void recalculateRaceLaneBestScores(boolean isTimeBased) {
    for (Heat heat : race.getHeats()) {
      for (int i = 0; i < heat.getDrivers().size(); i++) {
        DriverHeatData dhd = heat.getDrivers().get(i);
        RaceParticipant p = dhd.getDriver();
        if (p == null || p.getDriver() == Driver.EMPTY_DRIVER) continue;
        double score = getParticipantScore(dhd);
        if (score <= 0) continue;
        if (isTimeBased
            ? (score < raceLaneHighestScores.get(i))
            : (score > raceLaneHighestScores.get(i))) updateRaceLaneBestScore(i, dhd, score);
      }
    }
  }

  private double getParticipantScore(DriverHeatData dhd) {
    OverallRanking method = race.getRaceModel().getOverallScoring().getRankingMethod();
    if (method == OverallRanking.LAP_COUNT) return dhd.getAdjustedLapCount();
    if (method == OverallRanking.FASTEST_LAP) return dhd.getBestLapTime();
    if (method == OverallRanking.TOTAL_TIME) return dhd.getTotalTime();
    if (method == OverallRanking.AVERAGE_LAP) return dhd.getAverageLapTime();
    return 0;
  }

  private void updateRaceLaneBestScore(int lane, DriverHeatData dhd, double score) {
    raceLaneHighestScores.set(lane, score);
    Driver actualDriver = dhd.getActualDriver();
    if (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER) {
      raceLaneHighestScoreHolders.set(lane, actualDriver.getName());
      raceLaneHighestScoreHolderNicknames.set(lane, actualDriver.getNickname());
    } else {
      raceLaneHighestScoreHolders.set(lane, dhd.getDriver().getDriver().getName());
      raceLaneHighestScoreHolderNicknames.set(lane, dhd.getDriver().getDriver().getNickname());
    }
    if (raceLaneHighestScoreHolderNicknames.get(lane) == null
        || raceLaneHighestScoreHolderNicknames.get(lane).isEmpty())
      raceLaneHighestScoreHolderNicknames.set(lane, raceLaneHighestScoreHolders.get(lane));
    raceLaneHighestScoreHolderTeamNames.set(
        lane, dhd.getDriver().getTeam() != null ? dhd.getDriver().getTeam().getName() : "");
  }

  private void resetRaceFastestLapRecords() {
    raceFastestLap = Double.MAX_VALUE;
    raceFastestLapHolder = "";
    raceFastestLapHolderNickname = "";
    raceFastestLapHolderTeamName = "";
    for (int i = 0; i < raceLaneFastestLapTimes.size(); i++) {
      raceLaneFastestLapTimes.set(i, Double.MAX_VALUE);
      raceLaneFastestLapHolders.set(i, "");
      raceLaneFastestLapHolderNicknames.set(i, "");
      raceLaneFastestLapHolderTeamNames.set(i, "");
    }
  }

  private void recalculateRaceBestLap() {
    for (Heat heat : race.getHeats()) {
      for (DriverHeatData dhd : heat.getDrivers()) {
        double lapTime = dhd.getBestLapTime();
        if (lapTime > 0 && lapTime < raceFastestLap) updateRaceBestLap(dhd, lapTime);
      }
    }
  }

  private void updateRaceBestLap(DriverHeatData dhd, double lapTime) {
    raceFastestLap = lapTime;
    Driver actualDriver = dhd.getActualDriver();
    if (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER) {
      raceFastestLapHolder = actualDriver.getName();
      raceFastestLapHolderNickname = actualDriver.getNickname();
    } else {
      raceFastestLapHolder = dhd.getDriver().getDriver().getName();
      raceFastestLapHolderNickname = dhd.getDriver().getDriver().getNickname();
    }
    if (raceFastestLapHolderNickname == null || raceFastestLapHolderNickname.isEmpty())
      raceFastestLapHolderNickname = raceFastestLapHolder;
    raceFastestLapHolderTeamName =
        dhd.getDriver().getTeam() != null ? dhd.getDriver().getTeam().getName() : "";
  }

  private void recalculateRaceLaneBestLaps() {
    for (Heat heat : race.getHeats()) {
      for (int i = 0; i < heat.getDrivers().size(); i++) {
        DriverHeatData dhd = heat.getDrivers().get(i);
        double lapTime = dhd.getBestLapTime();
        if (lapTime > 0 && lapTime < raceLaneFastestLapTimes.get(i))
          updateRaceLaneBestLap(i, dhd, lapTime);
      }
    }
  }

  private void updateRaceLaneBestLap(int lane, DriverHeatData dhd, double lapTime) {
    raceLaneFastestLapTimes.set(lane, lapTime);
    Driver actualDriver = dhd.getActualDriver();
    if (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER) {
      raceLaneFastestLapHolders.set(lane, actualDriver.getName());
      raceLaneFastestLapHolderNicknames.set(lane, actualDriver.getNickname());
    } else {
      raceLaneFastestLapHolders.set(lane, dhd.getDriver().getDriver().getName());
      raceLaneFastestLapHolderNicknames.set(lane, dhd.getDriver().getDriver().getNickname());
    }
    if (raceLaneFastestLapHolderNicknames.get(lane) == null
        || raceLaneFastestLapHolderNicknames.get(lane).isEmpty())
      raceLaneFastestLapHolderNicknames.set(lane, raceLaneFastestLapHolders.get(lane));
    raceLaneFastestLapHolderTeamNames.set(
        lane, dhd.getDriver().getTeam() != null ? dhd.getDriver().getTeam().getName() : "");
  }

  private void recalculateOverallRecords(long timestamp) {
    if (baseStatistics == null) {
      if (race.getState() instanceof RaceOver) copyRaceToOverallRecords(timestamp);
      else resetOverallRecords();
      return;
    }
    boolean isTimeBased = race.isTimeBasedRanking();
    updateOverallBestScore(isTimeBased, timestamp);
    updateOverallLaneBestScores(isTimeBased, timestamp);
    updateOverallBestLap(timestamp);
    updateOverallLaneBestLaps(timestamp);
  }

  private void copyRaceToOverallRecords(long timestamp) {
    overallHighestScore = raceHighestScore;
    overallHighestScoreHolder = raceHighestScoreHolder;
    overallHighestScoreHolderNickname = raceHighestScoreHolderNickname;
    overallHighestScoreHolderTeamName = raceHighestScoreHolderTeamName;
    overallHighestScoreDate = timestamp;
    for (int i = 0; i < overallLaneHighestScores.size(); i++) {
      overallLaneHighestScores.set(i, raceLaneHighestScores.get(i));
      overallLaneHighestScoreHolders.set(i, raceLaneHighestScoreHolders.get(i));
      overallLaneHighestScoreHolderNicknames.set(i, raceLaneHighestScoreHolderNicknames.get(i));
      overallLaneHighestScoreHolderTeamNames.set(i, raceLaneHighestScoreHolderTeamNames.get(i));
      overallLaneHighestScoreDates.set(i, timestamp);
    }
    overallFastestLap = raceFastestLap;
    overallFastestLapHolder = raceFastestLapHolder;
    overallFastestLapHolderNickname = raceFastestLapHolderNickname;
    overallFastestLapHolderTeamName = raceFastestLapHolderTeamName;
    overallFastestLapDate = timestamp;
    for (int i = 0; i < overallLaneFastestLapTimes.size(); i++) {
      overallLaneFastestLapTimes.set(i, raceLaneFastestLapTimes.get(i));
      overallLaneFastestLapHolders.set(i, raceLaneFastestLapHolders.get(i));
      overallLaneFastestLapHolderNicknames.set(i, raceLaneFastestLapHolderNicknames.get(i));
      overallLaneFastestLapHolderTeamNames.set(i, raceLaneFastestLapHolderTeamNames.get(i));
      overallLaneFastestLapDates.set(i, timestamp);
    }
  }

  private void resetOverallRecords() {
    boolean isTimeBased = race.isTimeBasedRanking();
    overallHighestScore = isTimeBased ? Double.MAX_VALUE : 0;
    overallHighestScoreHolder = "";
    overallHighestScoreHolderNickname = "";
    overallHighestScoreHolderTeamName = "";
    overallHighestScoreDate = 0L;
    for (int i = 0; i < overallLaneHighestScores.size(); i++) {
      overallLaneHighestScores.set(i, isTimeBased ? Double.MAX_VALUE : 0.0);
      overallLaneHighestScoreHolders.set(i, "");
      overallLaneHighestScoreHolderNicknames.set(i, "");
      overallLaneHighestScoreHolderTeamNames.set(i, "");
      overallLaneHighestScoreDates.set(i, 0L);
    }
    overallFastestLap = Double.MAX_VALUE;
    overallFastestLapHolder = "";
    overallFastestLapHolderNickname = "";
    overallFastestLapHolderTeamName = "";
    overallFastestLapDate = 0L;
    for (int i = 0; i < overallLaneFastestLapTimes.size(); i++) {
      overallLaneFastestLapTimes.set(i, Double.MAX_VALUE);
      overallLaneFastestLapHolders.set(i, "");
      overallLaneFastestLapHolderNicknames.set(i, "");
      overallLaneFastestLapHolderTeamNames.set(i, "");
      overallLaneFastestLapDates.set(i, 0L);
    }
  }

  private void updateOverallBestScore(boolean isTimeBased, long timestamp) {
    double baseScore = baseStatistics.getHighestScore();
    boolean isRaceBetter = false;
    if (race.getState() instanceof RaceOver
        && raceHighestScore > 0
        && raceHighestScore != Double.MAX_VALUE) {
      if (baseScore == 0 || baseScore == Double.MAX_VALUE) isRaceBetter = true;
      else
        isRaceBetter =
            isTimeBased ? (raceHighestScore < baseScore) : (raceHighestScore > baseScore);
    }
    if (isRaceBetter) {
      overallHighestScore = raceHighestScore;
      overallHighestScoreHolder = raceHighestScoreHolder;
      overallHighestScoreHolderNickname = raceHighestScoreHolderNickname;
      overallHighestScoreHolderTeamName = raceHighestScoreHolderTeamName;
      overallHighestScoreDate = timestamp;
    } else {
      overallHighestScore = baseScore;
      overallHighestScoreHolder = baseStatistics.getHighestScoreHolderName();
      overallHighestScoreHolderNickname = baseStatistics.getHighestScoreHolderNickname();
      overallHighestScoreHolderTeamName = baseStatistics.getHighestScoreTeamName();
      overallHighestScoreDate = baseStatistics.getHighestScoreDate();
    }
  }

  private void updateOverallLaneBestScores(boolean isTimeBased, long timestamp) {
    for (int i = 0; i < overallLaneHighestScores.size(); i++) {
      double baseLaneScore =
          (baseStatistics.getLaneHighestScores() != null
                  && i < baseStatistics.getLaneHighestScores().size())
              ? baseStatistics.getLaneHighestScores().get(i)
              : (isTimeBased ? Double.MAX_VALUE : 0.0);
      boolean isLaneBetter = false;
      double raceLaneScore = raceLaneHighestScores.get(i);
      if (race.getState() instanceof RaceOver
          && raceLaneScore > 0
          && raceLaneScore != Double.MAX_VALUE) {
        if (baseLaneScore == 0 || baseLaneScore == Double.MAX_VALUE) isLaneBetter = true;
        else
          isLaneBetter =
              isTimeBased ? (raceLaneScore < baseLaneScore) : (raceLaneScore > baseLaneScore);
      }
      if (isLaneBetter) {
        overallLaneHighestScores.set(i, raceLaneScore);
        overallLaneHighestScoreHolders.set(i, raceLaneHighestScoreHolders.get(i));
        overallLaneHighestScoreHolderNicknames.set(i, raceLaneHighestScoreHolderNicknames.get(i));
        overallLaneHighestScoreHolderTeamNames.set(i, raceLaneHighestScoreHolderTeamNames.get(i));
        overallLaneHighestScoreDates.set(i, timestamp);
      } else {
        overallLaneHighestScores.set(i, baseLaneScore);
        overallLaneHighestScoreHolders.set(
            i,
            (baseStatistics.getLaneHighestScoreHolderNames() != null
                    && i < baseStatistics.getLaneHighestScoreHolderNames().size())
                ? baseStatistics.getLaneHighestScoreHolderNames().get(i)
                : "");
        overallLaneHighestScoreHolderNicknames.set(
            i,
            (baseStatistics.getLaneHighestScoreHolderNicknames() != null
                    && i < baseStatistics.getLaneHighestScoreHolderNicknames().size())
                ? baseStatistics.getLaneHighestScoreHolderNicknames().get(i)
                : "");
        overallLaneHighestScoreHolderTeamNames.set(
            i,
            (baseStatistics.getLaneHighestScoreTeamNames() != null
                    && i < baseStatistics.getLaneHighestScoreTeamNames().size())
                ? baseStatistics.getLaneHighestScoreTeamNames().get(i)
                : "");
        overallLaneHighestScoreDates.set(
            i,
            (baseStatistics.getLaneHighestScoreDates() != null
                    && i < baseStatistics.getLaneHighestScoreDates().size())
                ? baseStatistics.getLaneHighestScoreDates().get(i)
                : 0L);
      }
    }
  }

  private void updateOverallBestLap(long timestamp) {
    double baseFastestLap = baseStatistics.getFastestLapTime();
    boolean isRaceLapBetter = false;
    if (race.getState() instanceof RaceOver
        && raceFastestLap > 0
        && raceFastestLap != Double.MAX_VALUE) {
      if (baseFastestLap == 0 || baseFastestLap == Double.MAX_VALUE) isRaceLapBetter = true;
      else isRaceLapBetter = raceFastestLap < baseFastestLap;
    }
    if (isRaceLapBetter) {
      overallFastestLap = raceFastestLap;
      overallFastestLapHolder = raceFastestLapHolder;
      overallFastestLapHolderNickname = raceFastestLapHolderNickname;
      overallFastestLapHolderTeamName = raceFastestLapHolderTeamName;
      overallFastestLapDate = timestamp;
    } else {
      overallFastestLap = baseFastestLap;
      overallFastestLapHolder = baseStatistics.getFastestLapDriverName();
      overallFastestLapHolderNickname = baseStatistics.getFastestLapDriverNickname();
      overallFastestLapHolderTeamName = baseStatistics.getFastestLapTeamName();
      overallFastestLapDate = baseStatistics.getFastestLapDate();
    }
  }

  private void updateOverallLaneBestLaps(long timestamp) {
    for (int i = 0; i < overallLaneFastestLapTimes.size(); i++) {
      double baseLaneLap =
          (baseStatistics.getLaneFastestLapTimes() != null
                  && i < baseStatistics.getLaneFastestLapTimes().size())
              ? baseStatistics.getLaneFastestLapTimes().get(i)
              : Double.MAX_VALUE;
      boolean isLaneLapBetter = false;
      double raceLaneLap = raceLaneFastestLapTimes.get(i);
      if (race.getState() instanceof RaceOver
          && raceLaneLap > 0
          && raceLaneLap != Double.MAX_VALUE) {
        if (baseLaneLap == 0 || baseLaneLap == Double.MAX_VALUE) isLaneLapBetter = true;
        else isLaneLapBetter = raceLaneLap < baseLaneLap;
      }
      if (isLaneLapBetter) {
        overallLaneFastestLapTimes.set(i, raceLaneLap);
        overallLaneFastestLapHolders.set(i, raceLaneFastestLapHolders.get(i));
        overallLaneFastestLapHolderNicknames.set(i, raceLaneFastestLapHolderNicknames.get(i));
        overallLaneFastestLapHolderTeamNames.set(i, raceLaneFastestLapHolderTeamNames.get(i));
        overallLaneFastestLapDates.set(i, timestamp);
      } else {
        overallLaneFastestLapTimes.set(i, baseLaneLap);
        overallLaneFastestLapHolders.set(
            i,
            (baseStatistics.getLaneFastestLapDriverNames() != null
                    && i < baseStatistics.getLaneFastestLapDriverNames().size())
                ? baseStatistics.getLaneFastestLapDriverNames().get(i)
                : "");
        overallLaneFastestLapHolderNicknames.set(
            i,
            (baseStatistics.getLaneFastestLapDriverNicknames() != null
                    && i < baseStatistics.getLaneFastestLapDriverNicknames().size())
                ? baseStatistics.getLaneFastestLapDriverNicknames().get(i)
                : "");
        overallLaneFastestLapHolderTeamNames.set(
            i,
            (baseStatistics.getLaneFastestLapTeamNames() != null
                    && i < baseStatistics.getLaneFastestLapTeamNames().size())
                ? baseStatistics.getLaneFastestLapTeamNames().get(i)
                : "");
        overallLaneFastestLapDates.set(
            i,
            (baseStatistics.getLaneFastestLapDates() != null
                    && i < baseStatistics.getLaneFastestLapDates().size())
                ? baseStatistics.getLaneFastestLapDates().get(i)
                : 0L);
      }
    }
  }

  public RecordData getRecordData() {
    recalculateScoreRecords();
    OverallRecords.Builder overallBuilder =
        OverallRecords.newBuilder()
            .setFastestLap(
                RecordEntry.newBuilder()
                    .setValue(overallFastestLap == Double.MAX_VALUE ? 0 : overallFastestLap)
                    .setHolderName(nonNull(overallFastestLapHolder))
                    .setHolderNickname(nonNull(overallFastestLapHolderNickname))
                    .setHolderTeamName(nonNull(overallFastestLapHolderTeamName))
                    .setDate(overallFastestLapDate)
                    .build())
            .setHighestScore(
                RecordEntry.newBuilder()
                    .setValue(overallHighestScore == Double.MAX_VALUE ? 0 : overallHighestScore)
                    .setHolderName(nonNull(overallHighestScoreHolder))
                    .setHolderNickname(nonNull(overallHighestScoreHolderNickname))
                    .setHolderTeamName(nonNull(overallHighestScoreHolderTeamName))
                    .setDate(overallHighestScoreDate)
                    .build());
    for (int i = 0; i < overallLaneFastestLapTimes.size(); i++) {
      overallBuilder.addLaneFastestLap(
          RecordEntry.newBuilder()
              .setValue(
                  overallLaneFastestLapTimes.get(i) == Double.MAX_VALUE
                      ? 0
                      : overallLaneFastestLapTimes.get(i))
              .setHolderName(nonNull(overallLaneFastestLapHolders.get(i)))
              .setHolderNickname(nonNull(overallLaneFastestLapHolderNicknames.get(i)))
              .setHolderTeamName(nonNull(overallLaneFastestLapHolderTeamNames.get(i)))
              .setDate(overallLaneFastestLapDates.get(i))
              .build());
      overallBuilder.addLaneHighestScore(
          RecordEntry.newBuilder()
              .setValue(
                  overallLaneHighestScores.get(i) == Double.MAX_VALUE
                      ? 0
                      : overallLaneHighestScores.get(i))
              .setHolderName(nonNull(overallLaneHighestScoreHolders.get(i)))
              .setHolderNickname(nonNull(overallLaneHighestScoreHolderNicknames.get(i)))
              .setHolderTeamName(nonNull(overallLaneHighestScoreHolderTeamNames.get(i)))
              .setDate(overallLaneHighestScoreDates.get(i))
              .build());
    }
    CurrentRecords.Builder currentBuilder =
        CurrentRecords.newBuilder()
            .setFastestLap(
                RecordEntry.newBuilder()
                    .setValue(raceFastestLap == Double.MAX_VALUE ? 0 : raceFastestLap)
                    .setHolderName(nonNull(raceFastestLapHolder))
                    .setHolderNickname(nonNull(raceFastestLapHolderNickname))
                    .setHolderTeamName(nonNull(raceFastestLapHolderTeamName))
                    .build())
            .setHighestScore(
                RecordEntry.newBuilder()
                    .setValue(raceHighestScore == Double.MAX_VALUE ? 0 : raceHighestScore)
                    .setHolderName(nonNull(raceHighestScoreHolder))
                    .setHolderNickname(nonNull(raceHighestScoreHolderNickname))
                    .setHolderTeamName(nonNull(raceHighestScoreHolderTeamName))
                    .build())
            .setHeatFastestLap(
                RecordEntry.newBuilder()
                    .setValue(heatFastestLap == Double.MAX_VALUE ? 0 : heatFastestLap)
                    .setHolderName(nonNull(heatFastestLapHolder))
                    .setHolderNickname(nonNull(heatFastestLapHolderNickname))
                    .setHolderTeamName(nonNull(heatFastestLapHolderTeamName))
                    .build());
    for (int i = 0; i < raceLaneFastestLapTimes.size(); i++) {
      currentBuilder.addLaneFastestLap(
          RecordEntry.newBuilder()
              .setValue(
                  raceLaneFastestLapTimes.get(i) == Double.MAX_VALUE
                      ? 0
                      : raceLaneFastestLapTimes.get(i))
              .setHolderName(nonNull(raceLaneFastestLapHolders.get(i)))
              .setHolderNickname(nonNull(raceLaneFastestLapHolderNicknames.get(i)))
              .setHolderTeamName(nonNull(raceLaneFastestLapHolderTeamNames.get(i)))
              .build());
      currentBuilder.addLaneHighestScore(
          RecordEntry.newBuilder()
              .setValue(raceLaneHighestScores.get(i))
              .setHolderName(nonNull(raceLaneHighestScoreHolders.get(i)))
              .setHolderNickname(nonNull(raceLaneHighestScoreHolderNicknames.get(i)))
              .setHolderTeamName(nonNull(raceLaneHighestScoreHolderTeamNames.get(i)))
              .build());
    }
    return RecordData.newBuilder()
        .setOverall(overallBuilder.build())
        .setCurrent(currentBuilder.build())
        .build();
  }

  public void broadcastRecords() {
    race.broadcast(RaceData.newBuilder().setRecordData(getRecordData()).build());
  }

  public void updateScoreRecords() {
    recalculateScoreRecords();
    broadcastRecords();
  }

  public void onLap(DriverHeatData driverData, double lapTime, int lane) {
    boolean changed = updateHeatFastestLap(driverData, lapTime);
    if (updateSessionFastestLap(driverData, lapTime)) changed = true;
    if (updateLaneFastestLap(driverData, lapTime, lane)) changed = true;
    if (updateOverallFastestLap(driverData, lapTime)) changed = true;
    if (updateOverallLaneFastestLap(driverData, lapTime, lane)) changed = true;
    if (changed) broadcastRecords();
  }

  private boolean updateHeatFastestLap(DriverHeatData driverData, double lapTime) {
    if (lapTime >= heatFastestLap) return false;
    heatFastestLap = lapTime;
    Driver actualDriver = driverData.getActualDriver();
    heatFastestLapHolder =
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getName()
            : driverData.getDriver().getDriver().getName();
    heatFastestLapHolderNickname =
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getNickname()
            : driverData.getDriver().getDriver().getNickname();
    if (heatFastestLapHolderNickname == null || heatFastestLapHolderNickname.isEmpty())
      heatFastestLapHolderNickname = heatFastestLapHolder;
    heatFastestLapHolderTeamName =
        driverData.getDriver().getTeam() != null ? driverData.getDriver().getTeam().getName() : "";
    return true;
  }

  private boolean updateSessionFastestLap(DriverHeatData driverData, double lapTime) {
    if (lapTime >= raceFastestLap) return false;
    raceFastestLap = lapTime;
    Driver actualDriver = driverData.getActualDriver();
    raceFastestLapHolder =
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getName()
            : driverData.getDriver().getDriver().getName();
    raceFastestLapHolderNickname =
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getNickname()
            : driverData.getDriver().getDriver().getNickname();
    if (raceFastestLapHolderNickname == null || raceFastestLapHolderNickname.isEmpty())
      raceFastestLapHolderNickname = raceFastestLapHolder;
    raceFastestLapHolderTeamName =
        driverData.getDriver().getTeam() != null ? driverData.getDriver().getTeam().getName() : "";
    return true;
  }

  private boolean updateLaneFastestLap(DriverHeatData driverData, double lapTime, int lane) {
    if (lane < 0
        || lane >= raceLaneFastestLapTimes.size()
        || lapTime >= raceLaneFastestLapTimes.get(lane)) return false;
    raceLaneFastestLapTimes.set(lane, lapTime);
    Driver actualDriver = driverData.getActualDriver();
    raceLaneFastestLapHolders.set(
        lane,
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getName()
            : driverData.getDriver().getDriver().getName());
    raceLaneFastestLapHolderNicknames.set(
        lane,
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getNickname()
            : driverData.getDriver().getDriver().getNickname());
    if (raceLaneFastestLapHolderNicknames.get(lane) == null
        || raceLaneFastestLapHolderNicknames.get(lane).isEmpty())
      raceLaneFastestLapHolderNicknames.set(lane, raceLaneFastestLapHolders.get(lane));
    raceLaneFastestLapHolderTeamNames.set(
        lane,
        driverData.getDriver().getTeam() != null ? driverData.getDriver().getTeam().getName() : "");
    return true;
  }

  private boolean updateOverallFastestLap(DriverHeatData driverData, double lapTime) {
    if (lapTime >= overallFastestLap) return false;
    overallFastestLap = lapTime;
    overallFastestLapDate = System.currentTimeMillis();
    Driver actualDriver = driverData.getActualDriver();
    overallFastestLapHolder =
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getName()
            : driverData.getDriver().getDriver().getName();
    overallFastestLapHolderNickname =
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getNickname()
            : driverData.getDriver().getDriver().getNickname();
    if (overallFastestLapHolderNickname == null || overallFastestLapHolderNickname.isEmpty())
      overallFastestLapHolderNickname = overallFastestLapHolder;
    overallFastestLapHolderTeamName =
        driverData.getDriver().getTeam() != null ? driverData.getDriver().getTeam().getName() : "";
    return true;
  }

  private boolean updateOverallLaneFastestLap(DriverHeatData driverData, double lapTime, int lane) {
    if (lane < 0
        || lane >= overallLaneFastestLapTimes.size()
        || lapTime >= overallLaneFastestLapTimes.get(lane)) return false;
    overallLaneFastestLapTimes.set(lane, lapTime);
    overallLaneFastestLapDates.set(lane, System.currentTimeMillis());
    Driver actualDriver = driverData.getActualDriver();
    overallLaneFastestLapHolders.set(
        lane,
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getName()
            : driverData.getDriver().getDriver().getName());
    overallLaneFastestLapHolderNicknames.set(
        lane,
        (actualDriver != null && actualDriver != Driver.EMPTY_DRIVER)
            ? actualDriver.getNickname()
            : driverData.getDriver().getDriver().getNickname());
    if (overallLaneFastestLapHolderNicknames.get(lane) == null
        || overallLaneFastestLapHolderNicknames.get(lane).isEmpty())
      overallLaneFastestLapHolderNicknames.set(lane, overallLaneFastestLapHolders.get(lane));
    overallLaneFastestLapHolderTeamNames.set(
        lane,
        driverData.getDriver().getTeam() != null ? driverData.getDriver().getTeam().getName() : "");
    return true;
  }

  public void saveGlobalRecords() {
    if (race.getDatabaseContext() == null) return;
    try {
      DatabaseService dbService = DatabaseService.getInstance();
      GlobalStatistics stats = new GlobalStatistics();
      stats.setFastestLapTime(overallFastestLap);
      stats.setFastestLapDriverName(overallFastestLapHolder);
      stats.setFastestLapDriverNickname(overallFastestLapHolderNickname);
      stats.setFastestLapTeamName(overallFastestLapHolderTeamName);
      stats.setFastestLapDate(overallFastestLapDate);
      stats.setHighestScore(overallHighestScore);
      stats.setHighestScoreHolderName(overallHighestScoreHolder);
      stats.setHighestScoreHolderNickname(overallHighestScoreHolderNickname);
      stats.setHighestScoreTeamName(overallHighestScoreHolderTeamName);
      stats.setHighestScoreDate(overallHighestScoreDate);
      stats.setLaneFastestLapTimes(new ArrayList<>(overallLaneFastestLapTimes));
      stats.setLaneFastestLapDriverNames(new ArrayList<>(overallLaneFastestLapHolders));
      stats.setLaneFastestLapDriverNicknames(new ArrayList<>(overallLaneFastestLapHolderNicknames));
      stats.setLaneFastestLapTeamNames(new ArrayList<>(overallLaneFastestLapHolderTeamNames));
      stats.setLaneFastestLapDates(new ArrayList<>(overallLaneFastestLapDates));
      stats.setLaneHighestScores(new ArrayList<>(overallLaneHighestScores));
      stats.setLaneHighestScoreHolderNames(new ArrayList<>(overallLaneHighestScoreHolders));
      stats.setLaneHighestScoreHolderNicknames(
          new ArrayList<>(overallLaneHighestScoreHolderNicknames));
      stats.setLaneHighestScoreTeamNames(new ArrayList<>(overallLaneHighestScoreHolderTeamNames));
      stats.setLaneHighestScoreDates(new ArrayList<>(overallLaneHighestScoreDates));
      new Thread(
              () -> dbService.updateGlobalStatistics(race.getDatabaseContext().getDatabase(), race))
          .start();
    } catch (Exception e) {
      logger.error("Failed to save global statistics", e);
    }
  }

  public GlobalStatistics getBaseStatistics() {
    return baseStatistics;
  }

  private String nonNull(String s) {
    return s == null ? "" : s;
  }
}
