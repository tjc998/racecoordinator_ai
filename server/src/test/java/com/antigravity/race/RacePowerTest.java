package com.antigravity.race;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antigravity.models.Track;
import com.antigravity.protocols.ProtocolDelegate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class RacePowerTest {

  private Race race;
  private ProtocolDelegate mockProtocols;
  private com.antigravity.models.Race raceModel;
  private Track track;

  @Before
  public void setUp() throws Exception {
    raceModel = mock(com.antigravity.models.Race.class);
    com.antigravity.models.HeatScoring heatScoring = mock(com.antigravity.models.HeatScoring.class);
    when(heatScoring.getHeatRanking())
        .thenReturn(com.antigravity.models.HeatScoring.HeatRanking.LAP_COUNT);
    when(heatScoring.getHeatRankingTiebreaker())
        .thenReturn(com.antigravity.models.HeatScoring.HeatRankingTiebreaker.AVERAGE_LAP_TIME);
    when(raceModel.getHeatRotationType())
        .thenReturn(com.antigravity.models.HeatRotationType.RoundRobin);
    when(raceModel.getHeatScoring()).thenReturn(heatScoring);
    track = mock(Track.class);
    ArrayList<com.antigravity.models.Lane> lanes = new ArrayList<>();
    lanes.add(mock(com.antigravity.models.Lane.class));
    when(track.getLanes()).thenReturn(lanes);

    List<com.antigravity.race.RaceParticipant> drivers = new ArrayList<>();
    drivers.add(
        new com.antigravity.race.RaceParticipant(com.antigravity.models.Driver.EMPTY_DRIVER));

    // Create a Race instance using the builder
    race =
        new Race.Builder().model(raceModel).track(track).drivers(drivers).isDemoMode(true).build();

    // Inject a mock ProtocolDelegate
    mockProtocols = mock(ProtocolDelegate.class);
    race.injectProtocols(mockProtocols);
  }

  @Test
  public void testSetMainPowerCallsProtocols() {
    // Even if we set it to the same value twice, it should call protocols each time
    // because we removed the redundancy check in Race.java (letting PowerManager handle it)

    race.setMainPower(true);
    verify(mockProtocols, times(1)).setMainPower(true);

    race.setMainPower(true);
    verify(mockProtocols, times(2)).setMainPower(true);

    race.setMainPower(false);
    verify(mockProtocols, times(1)).setMainPower(false);
  }

  @Test
  public void testSetLanePowerLoopsCorrectly() {
    // Mock track to have 2 lanes
    com.antigravity.models.Lane lane0 = mock(com.antigravity.models.Lane.class);
    com.antigravity.models.Lane lane1 = mock(com.antigravity.models.Lane.class);
    ArrayList<com.antigravity.models.Lane> lanes = new ArrayList<>();
    lanes.add(lane0);
    lanes.add(lane1);
    when(track.getLanes()).thenReturn(lanes);

    // Call setLanePower with -1 (all lanes)
    race.setLanePower(true, -1);

    // Verify it called protocols for each lane
    verify(mockProtocols).setLanePower(true, 0);
    verify(mockProtocols).setLanePower(true, 1);
  }

  @Test
  public void testInitializeHardwareState() {
    race.initializeHardwareState();
    verify(mockProtocols, times(1)).initializeHardwareState();
  }
}
