package com.antigravity.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.antigravity.auth.Role;
import org.junit.Test;

public class TrackResourceLockTest {

  @Test
  public void testInitialState() {
    TrackResourceLock lock = new TrackResourceLock();
    assertEquals(TrackResourceLock.LockState.IDLE, lock.getState());
    assertNull(lock.getOwnerRole());
    assertNull(lock.getOwnerId());
  }

  @Test
  public void testSetLock() {
    TrackResourceLock lock = new TrackResourceLock();
    lock.setLock(TrackResourceLock.LockState.RACE_RUNNING, Role.DIRECTOR, "user1");

    assertEquals(TrackResourceLock.LockState.RACE_RUNNING, lock.getState());
    assertEquals(Role.DIRECTOR, lock.getOwnerRole());
    assertEquals("user1", lock.getOwnerId());
  }

  @Test
  public void testClearLock() {
    TrackResourceLock lock = new TrackResourceLock();
    lock.setLock(TrackResourceLock.LockState.TRACK_EDITOR, Role.ADMIN, "admin1");
    lock.clear();

    assertEquals(TrackResourceLock.LockState.IDLE, lock.getState());
    assertNull(lock.getOwnerRole());
    assertNull(lock.getOwnerId());
  }
}
