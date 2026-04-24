package com.antigravity.race;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.antigravity.proto.RaceData;
import io.javalin.websocket.WsContext;
import java.nio.ByteBuffer;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
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

  private WsContext createMockContext(RemoteEndpoint mockRemote) {
    Session mockSession = mock(Session.class);
    when(mockSession.getRemote()).thenReturn(mockRemote);
    return new WsContext("id", mockSession) {};
  }

  @Test
  public void testSnapshotSentAsBinaryOnSessionAdd() {
    RemoteEndpoint mockRemote = mock(RemoteEndpoint.class);
    WsContext context = createMockContext(mockRemote);
    Race mockRace = mock(Race.class);

    RaceData snapshot =
        RaceData.newBuilder().setRace(com.antigravity.proto.Race.newBuilder().build()).build();
    when(mockRace.createSnapshot()).thenReturn(snapshot);

    manager.setRace(mockRace);

    // This triggers the snapshot send
    manager.addSession(context);

    // Verify binary send on remote
    verify(mockRemote).sendBytesByFuture(any(ByteBuffer.class));
    // Verify NO string send
    verify(mockRemote, never()).sendStringByFuture(anyString());
  }

  @Test
  public void testBroadcastSentAsBinary() {
    RemoteEndpoint mockRemote = mock(RemoteEndpoint.class);
    WsContext context = createMockContext(mockRemote);

    // Register session as a subscriber
    manager.addSession(context);
    // We need to call subscribe to add to raceDataSubscribers
    manager.handleRaceSubscription(
        context,
        com.antigravity.proto.RaceSubscriptionRequest.newBuilder().setSubscribe(true).build());

    // Clear previous interactions from the subscription snapshot
    reset(mockRemote);

    RaceData update = RaceData.newBuilder().build();
    manager.broadcast(update);

    // Verify binary send on remote
    verify(mockRemote, atLeastOnce()).sendBytesByFuture(any(ByteBuffer.class));
    // Verify NO string send
    verify(mockRemote, never()).sendStringByFuture(anyString());
  }

  @Test
  public void testBroadcastInterfaceEventSentAsBinary() {
    RemoteEndpoint mockRemote = mock(RemoteEndpoint.class);
    WsContext context = createMockContext(mockRemote);

    // Register session as an interface subscriber
    manager.addInterfaceSession(context);

    // Clear previous interactions (if any)
    reset(mockRemote);

    com.antigravity.proto.InterfaceEvent event =
        com.antigravity.proto.InterfaceEvent.newBuilder().build();
    manager.broadcastInterfaceEvent(event);

    // Verify binary send on remote
    verify(mockRemote, atLeastOnce()).sendBytesByFuture(any(ByteBuffer.class));
    // Verify NO string send
    verify(mockRemote, never()).sendStringByFuture(anyString());
  }
}
