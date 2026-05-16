package com.antigravity.race;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.antigravity.models.GroupOptions;
import com.antigravity.models.Race;
import com.antigravity.proto.Heat;
import com.antigravity.proto.ModifyHeatsRequest;
import org.junit.Before;
import org.junit.Test;

public class RaceHeatManagerGroupSequenceTest {

  private com.antigravity.race.Race race;
  private Race raceModel;
  private RaceHeatManager manager;

  @Before
  public void setUp() {
    race = mock(com.antigravity.race.Race.class);
    raceModel = mock(Race.class);
    when(race.getRaceModel()).thenReturn(raceModel);
    manager = new RaceHeatManager(race);
  }

  @Test
  public void testValidateGroups_Sequential() {
    GroupOptions options = new GroupOptions(true, 10, true, true, false, false, 0);
    when(raceModel.getGroupOptions()).thenReturn(options);

    ModifyHeatsRequest request =
        ModifyHeatsRequest.newBuilder()
            .addHeats(Heat.newBuilder().setGroup(0).build())
            .addHeats(Heat.newBuilder().setGroup(1).build())
            .build();

    String error = manager.validateGroups(request);
    assertNull("Should be valid", error);
  }

  @Test
  public void testValidateGroups_Gap() {
    GroupOptions options = new GroupOptions(true, 10, true, true, false, false, 0);
    when(raceModel.getGroupOptions()).thenReturn(options);

    ModifyHeatsRequest request =
        ModifyHeatsRequest.newBuilder()
            .addHeats(Heat.newBuilder().setGroup(0).build())
            .addHeats(Heat.newBuilder().setGroup(2).build())
            .build();

    String error = manager.validateGroups(request);
    assertEquals("RD_ERR_GROUP_NON_SEQUENTIAL|2|3", error);
  }

  @Test
  public void testValidateGroups_WrongStart() {
    GroupOptions options = new GroupOptions(true, 10, true, true, false, false, 0);
    when(raceModel.getGroupOptions()).thenReturn(options);

    ModifyHeatsRequest request =
        ModifyHeatsRequest.newBuilder().addHeats(Heat.newBuilder().setGroup(1).build()).build();

    String error = manager.validateGroups(request);
    assertEquals("RD_ERR_GROUP_NON_SEQUENTIAL|1|2", error);
  }

  @Test
  public void testValidateGroups_Negative() {
    GroupOptions options = new GroupOptions(true, 10, true, true, false, false, 0);
    when(raceModel.getGroupOptions()).thenReturn(options);

    ModifyHeatsRequest request =
        ModifyHeatsRequest.newBuilder().addHeats(Heat.newBuilder().setGroup(-1).build()).build();

    String error = manager.validateGroups(request);
    assertEquals("RD_ERR_GROUP_MIN_VALUE", error);
  }

  @Test
  public void testValidateGroups_Disabled() {
    GroupOptions options = new GroupOptions(false, 10, true, true, false, false, 0);
    when(raceModel.getGroupOptions()).thenReturn(options);

    ModifyHeatsRequest request =
        ModifyHeatsRequest.newBuilder()
            .addHeats(Heat.newBuilder().setGroup(5).build()) // Gap doesn't matter if disabled
            .build();

    String error = manager.validateGroups(request);
    assertNull("Should be valid when groups disabled", error);
  }
}
