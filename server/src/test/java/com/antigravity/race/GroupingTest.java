package com.antigravity.race;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.antigravity.models.Driver;
import com.antigravity.models.GroupOptions;
import com.antigravity.models.HeatRotationType;
import com.antigravity.models.HeatScoring;
import com.antigravity.models.Lane;
import com.antigravity.models.Race;
import com.antigravity.models.Track;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GroupingTest {

  private com.antigravity.race.Race race;
  private Race raceModel;
  private Track track;
  private HeatScoring heatScoring;

  @Before
  public void setUp() {
    race = mock(com.antigravity.race.Race.class);
    raceModel = mock(Race.class);
    track = mock(Track.class);
    heatScoring = mock(HeatScoring.class);

    when(race.getRaceModel()).thenReturn(raceModel);
    when(race.getTrack()).thenReturn(track);
    when(raceModel.getHeatScoring()).thenReturn(heatScoring);

    // Mock 4 lanes
    List<Lane> lanes = new ArrayList<>();
    lanes.add(new Lane("Blue", "blue", 1));
    lanes.add(new Lane("Red", "red", 2));
    lanes.add(new Lane("White", "white", 3));
    lanes.add(new Lane("Yellow", "yellow", 4));
    when(track.getLanes()).thenReturn(lanes);

    // Default scoring
    when(heatScoring.getHeatRanking()).thenReturn(HeatScoring.HeatRanking.LAP_COUNT);
    when(heatScoring.getHeatRankingTiebreaker())
        .thenReturn(HeatScoring.HeatRankingTiebreaker.FASTEST_LAP_TIME);

    // Default rotation
    when(raceModel.getHeatRotationType()).thenReturn(HeatRotationType.RoundRobin);
  }

  @Test
  public void testGrouping_Isolation() {
    // 8 drivers, 4 lanes, 2 groups of 4
    GroupOptions groupOptions = new GroupOptions(true, 2, false, true, false, false, 0);
    when(raceModel.getGroupOptions()).thenReturn(groupOptions);

    List<RaceParticipant> participants = createDrivers(8);

    List<Heat> heats = HeatBuilder.buildHeats(race, participants, new ArrayList<>());

    // 2 groups of 4 drivers. Each group of 4 on 4 lanes = 4 heats per group. Total 8 heats.
    assertEquals(8, heats.size());

    // Verify drivers in each heat belong to the same group
    for (Heat heat : heats) {
      int group = -1;
      for (DriverHeatData dhd : heat.getDrivers()) {
        if (!dhd.getDriver().getDriver().isEmpty()) {
          int driverIdx = Integer.parseInt(dhd.getDriver().getDriver().getEntityId()) - 1;
          int expectedGroup = (driverIdx < 4) ? 0 : 1;
          if (group == -1) {
            group = expectedGroup;
          }
          assertEquals("Drivers from different groups in the same heat!", group, expectedGroup);
          assertEquals("Heat group label mismatch!", group, heat.getGroup());
        }
      }
    }
  }

  @Test
  public void testGrouping_RotateHeats() {
    // 8 drivers, 4 lanes, 2 groups of 4, rotateGroupHeats = true
    GroupOptions groupOptions = new GroupOptions(true, 2, false, true, false, true, 0);
    when(raceModel.getGroupOptions()).thenReturn(groupOptions);

    List<RaceParticipant> participants = createDrivers(8);

    List<Heat> heats = HeatBuilder.buildHeats(race, participants, new ArrayList<>());

    assertEquals(8, heats.size());

    // Verify interleaving: G0, G1, G0, G1, G0, G1, G0, G1
    for (int i = 0; i < heats.size(); i++) {
      assertEquals(
          "Heat " + i + " should be from group " + (i % 2), i % 2, heats.get(i).getGroup());
    }
  }

  @Test
  public void testGrouping_BalanceSeeds() {
    // 8 drivers, 4 lanes, 2 groups, balance = true
    // Seeds: 1, 2, 3, 4, 5, 6, 7, 8
    // G0: 1, 3, 5, 7
    // G1: 2, 4, 6, 8
    GroupOptions groupOptions = new GroupOptions(true, 2, true, true, false, false, 0);
    when(raceModel.getGroupOptions()).thenReturn(groupOptions);

    List<RaceParticipant> participants = createDrivers(8);

    List<Heat> heats = HeatBuilder.buildHeats(race, participants, new ArrayList<>());

    // Verify group 0 drivers
    for (Heat heat : heats) {
      if (heat.getGroup() == 0) {
        for (DriverHeatData dhd : heat.getDrivers()) {
          if (!dhd.getDriver().getDriver().isEmpty()) {
            int seed = Integer.parseInt(dhd.getDriver().getDriver().getEntityId());
            assertEquals("Balanced seed should be odd in group 0", 1, seed % 2);
          }
        }
      } else {
        for (DriverHeatData dhd : heat.getDrivers()) {
          if (!dhd.getDriver().getDriver().isEmpty()) {
            int seed = Integer.parseInt(dhd.getDriver().getDriver().getEntityId());
            assertEquals("Balanced seed should be even in group 1", 0, seed % 2);
          }
        }
      }
    }
  }

  private List<RaceParticipant> createDrivers(int count) {
    List<RaceParticipant> participants = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      participants.add(
          new RaceParticipant(
              new Driver(
                  "D" + i,
                  "d" + i,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  String.valueOf(i),
                  null)));
    }
    return participants;
  }
}
