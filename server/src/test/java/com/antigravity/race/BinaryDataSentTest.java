package com.antigravity.race;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.antigravity.proto.RaceData;
import io.javalin.websocket.WsContext;
import java.nio.ByteBuffer;
import org.junit.Before;
import org.junit.Test;

public class BinaryDataSentTest {

  private ClientSubscriptionManager manager;

  @Before
  public void setUp() {
    manager = ClientSubscriptionManager.getInstance();
    manager.setRace(null);
    // Clear subscribers if possible (via internal state reset if needed,
    // but here we just ensure we start fresh in our tests)
  }

  @Test
  public void testSnapshotSentAsBinaryOnSessionAdd() {
    WsContext mockContext = mock(WsContext.class);
    Race mockRace = mock(Race.class);

    RaceData snapshot =
        RaceData.newBuilder().setRace(com.antigravity.proto.Race.newBuilder().build()).build();
    when(mockRace.createSnapshot()).thenReturn(snapshot);

    manager.setRace(mockRace);

    // This triggers the snapshot send
    manager.addSession(mockContext);

    // Verify binary send
    verify(mockContext).send(any(ByteBuffer.class));
    // Verify NO string send
    verify(mockContext, never()).send(anyString());
  }

  @Test
  public void testBroadcastSentAsBinary() {
    WsContext mockContext = mock(WsContext.class);

    // Register session as a subscriber
    manager.addSession(mockContext);
    // We need to call subscribe to add to raceDataSubscribers
    manager.handleRaceSubscription(
        mockContext,
        com.antigravity.proto.RaceSubscriptionRequest.newBuilder().setSubscribe(true).build());

    // Clear previous interactions from the subscription snapshot
    reset(mockContext);

    RaceData update = RaceData.newBuilder().build();
    manager.broadcast(update);

    // Verify binary send (may be called twice due to subscription logic, but must be binary)
    verify(mockContext, atLeastOnce()).send(any(ByteBuffer.class));
    // Verify NO string send
    verify(mockContext, never()).send(anyString());
  }

  @Test
  public void testBroadcastInterfaceEventSentAsBinary() {
    WsContext mockContext = mock(WsContext.class);

    // Register session as an interface subscriber
    manager.addInterfaceSession(mockContext);

    // Clear previous interactions (if any)
    reset(mockContext);

    com.antigravity.proto.InterfaceEvent event =
        com.antigravity.proto.InterfaceEvent.newBuilder().build();
    manager.broadcastInterfaceEvent(event);

    // Verify binary send
    verify(mockContext, atLeastOnce()).send(any(ByteBuffer.class));
    // Verify NO string send
    verify(mockContext, never()).send(anyString());
  }
}
