package com.antigravity.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.antigravity.auth.Role;
import org.junit.Before;
import org.junit.Test;

public class TrackResourceManagerTest {

  private TrackResourceManager manager;

  @Before
  public void setUp() {
    manager = TrackResourceManager.getInstance();
    manager.forceRelease(); // Ensure clean state before each test
  }

  @Test
  public void testRequestLock_SuccessWhenIdle() {
    boolean granted =
        manager.requestLock(TrackResourceLock.LockState.RACE_RUNNING, Role.DIRECTOR, "user1");
    assertTrue("Lock should be granted when idle", granted);
    assertEquals(TrackResourceLock.LockState.RACE_RUNNING, manager.getCurrentState());
  }

  @Test
  public void testRequestLock_SameUserCanChangeState() {
    manager.requestLock(TrackResourceLock.LockState.RACE_RUNNING, Role.DIRECTOR, "user1");
    boolean granted =
        manager.requestLock(TrackResourceLock.LockState.TRACK_EDITOR, Role.DIRECTOR, "user1");
    assertTrue("Same user should be able to change state", granted);
    assertEquals(TrackResourceLock.LockState.TRACK_EDITOR, manager.getCurrentState());
  }

  @Test
  public void testRequestLock_DeniedForDifferentUser() {
    manager.requestLock(TrackResourceLock.LockState.RACE_RUNNING, Role.DIRECTOR, "user1");
    boolean granted =
        manager.requestLock(TrackResourceLock.LockState.TRACK_EDITOR, Role.DIRECTOR, "user2");
    assertFalse("Different user should be denied", granted);
    assertEquals(
        "State should remain unchanged",
        TrackResourceLock.LockState.RACE_RUNNING,
        manager.getCurrentState());
  }

  @Test
  public void testRequestLock_AdminCanPreempt() {
    manager.requestLock(TrackResourceLock.LockState.RACE_RUNNING, Role.DIRECTOR, "user1");
    boolean granted =
        manager.requestLock(TrackResourceLock.LockState.TRACK_EDITOR, Role.ADMIN, "admin1");
    assertTrue("Admin should be able to preempt the lock", granted);
    assertEquals(
        "State should be updated to Admin's requested state",
        TrackResourceLock.LockState.TRACK_EDITOR,
        manager.getCurrentState());
  }

  @Test
  public void testReleaseLock_OwnerCanRelease() {
    manager.requestLock(TrackResourceLock.LockState.RACE_RUNNING, Role.DIRECTOR, "user1");
    boolean released = manager.releaseLock(Role.DIRECTOR, "user1");
    assertTrue("Owner should be able to release lock", released);
    assertEquals(TrackResourceLock.LockState.IDLE, manager.getCurrentState());
  }

  @Test
  public void testReleaseLock_AdminCanRelease() {
    manager.requestLock(TrackResourceLock.LockState.RACE_RUNNING, Role.DIRECTOR, "user1");
    boolean released = manager.releaseLock(Role.ADMIN, "admin1");
    assertTrue("Admin should be able to release anyone's lock", released);
    assertEquals(TrackResourceLock.LockState.IDLE, manager.getCurrentState());
  }

  @Test
  public void testReleaseLock_NonOwnerDenied() {
    manager.requestLock(TrackResourceLock.LockState.RACE_RUNNING, Role.DIRECTOR, "user1");
    boolean released = manager.releaseLock(Role.DIRECTOR, "user2");
    assertFalse("Non-owner should not be able to release lock", released);
    assertEquals(TrackResourceLock.LockState.RACE_RUNNING, manager.getCurrentState());
  }
}
