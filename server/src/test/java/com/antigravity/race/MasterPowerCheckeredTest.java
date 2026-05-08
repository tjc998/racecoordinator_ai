package com.antigravity.race;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import com.antigravity.models.HeatScoring;
import com.antigravity.models.Lane;
import com.antigravity.models.Race;
import com.antigravity.models.Track;
import com.antigravity.proto.RaceFlag;
import com.antigravity.protocols.ProtocolDelegate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class MasterPowerCheckeredTest {

  private com.antigravity.race.Race race;
  private ProtocolDelegate protocols;

  @Before
  public void setUp() {
    protocols = mock(ProtocolDelegate.class);

    Race model =
        new Race.Builder()
            .withHeatScoring(
                new HeatScoring(
                    HeatScoring.FinishMethod.Lap,
                    1L,
                    HeatScoring.HeatRanking.LAP_COUNT,
                    HeatScoring.HeatRankingTiebreaker.FASTEST_LAP_TIME,
                    HeatScoring.AllowFinish.Allow))
            .build();

    List<Lane> lanes = new ArrayList<>();
    lanes.add(new Lane("red", "white", 100));
    Track track = new Track("Track", lanes, new ArrayList<>(), "t1", null);

    race =
        new com.antigravity.race.Race.Builder()
            .model(model)
            .track(track)
            .drivers(new ArrayList<>())
            .isDemoMode(true)
            .build();

    // Use reflection to set the protocols field since it's private and created in
    // constructor
    try {
      java.lang.reflect.Field field = com.antigravity.race.Race.class.getDeclaredField("protocols");
      field.setAccessible(true);
      field.set(race, protocols);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testMasterPowerDuringCheckeredFlag() {
    // Initially it's NotStarted, flag is RED, power is OFF
    race.updatePowerForFlag(RaceFlag.RED);
    assertFalse("Power should be OFF for RED flag", race.isMainPower());

    // When flag is CHECKERED, power should be OFF
    race.updatePowerForFlag(RaceFlag.CHECKERED);
    assertFalse("Power should be OFF for CHECKERED flag", race.isMainPower());
  }
}
