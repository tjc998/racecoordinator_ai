package com.antigravity.race;

import static org.mockito.Mockito.*;

import com.antigravity.models.Race;
import com.antigravity.race.states.Paused;
import com.antigravity.race.states.Racing;
import org.junit.Before;
import org.junit.Test;

public class DriftTimeTest {
  private com.antigravity.race.Race mockRace;
  private Race mockRaceModel;
  private HeatExecutionManager mockExecutionManager;
  private Paused pausedState;
  private Racing racingState;

  @Before
  public void setUp() {
    mockRace = mock(com.antigravity.race.Race.class);
    mockRaceModel = mock(Race.class);
    mockExecutionManager = mock(HeatExecutionManager.class);
    RaceStatistics mockRaceStats = mock(RaceStatistics.class);
    Heat mockHeat = mock(Heat.class);
    RaceHeatStatistics mockHeatStats = mock(RaceHeatStatistics.class);

    when(mockRace.getRaceModel()).thenReturn(mockRaceModel);
    when(mockRace.getHeatExecutionManager()).thenReturn(mockExecutionManager);
    when(mockRace.getStatistics()).thenReturn(mockRaceStats);
    when(mockRace.getCurrentHeat()).thenReturn(mockHeat);
    when(mockHeat.getStatistics()).thenReturn(mockHeatStats);

    pausedState = new Paused();
    racingState = new Racing();
    // Racing state stores race in enter() as well
    racingState.enter(mockRace);
  }

  @Test
  public void testLapsCountedWithinDriftTime() throws InterruptedException {
    when(mockRaceModel.getDriftTime()).thenReturn(1.0); // 1.0 second drift time
    pausedState.enter(mockRace);

    // Immediate lap after pause
    pausedState.onLap(1, 10.5, 1, false);
    verify(mockExecutionManager, times(1)).onLap(1, 10.5, 1, false, true, true);

    // Lap after 500ms (still within 1s drift)
    Thread.sleep(500);
    pausedState.onLap(2, 11.5, 1, false);
    verify(mockExecutionManager, times(1)).onLap(2, 11.5, 1, false, true, true);
  }

  @Test
  public void testLapsIgnoredAfterDriftTime() throws InterruptedException {
    when(mockRaceModel.getDriftTime()).thenReturn(0.2); // 0.2 second drift time
    pausedState.enter(mockRace);

    // Wait 300ms (> 200ms drift)
    Thread.sleep(300);
    pausedState.onLap(1, 10.5, 1, false);
    verify(mockExecutionManager, never())
        .onLap(anyInt(), anyDouble(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean());
  }

  @Test
  public void testLapsIgnoredWithZeroDriftTime() {
    when(mockRaceModel.getDriftTime()).thenReturn(0.0); // 0.0 drift time
    pausedState.enter(mockRace);

    pausedState.onLap(1, 10.5, 1, false);
    verify(mockExecutionManager, never())
        .onLap(anyInt(), anyDouble(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean());
  }

  @Test
  public void testRacingStateAlwaysCountsLapsRegardlessOfDriftTime() {
    when(mockRaceModel.getDriftTime()).thenReturn(0.0);

    // Racing state should always count laps
    racingState.onLap(1, 10.5, 1, false);
    verify(mockExecutionManager, times(1)).onLap(1, 10.5, 1, false, true, false);

    when(mockRaceModel.getDriftTime()).thenReturn(100.0);
    racingState.onLap(2, 11.5, 1, false);
    verify(mockExecutionManager, times(1)).onLap(2, 11.5, 1, false, true, false);
  }
}
