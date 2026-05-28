package com.antigravity.resource;

import com.antigravity.auth.Role;

/** Represents the current locking state of the track hardware singleton. */
public class TrackResourceLock {

  public enum LockState {
    IDLE,
    RACE_RUNNING,
    TRACK_EDITOR
  }

  private LockState state;
  private Role ownerRole;
  private String ownerId; // Can be a JWT subject, IP, or "SYSTEM"
  private long lockedAt;

  public TrackResourceLock() {
    this.state = LockState.IDLE;
    this.ownerRole = null;
    this.ownerId = null;
    this.lockedAt = 0;
  }

  public synchronized LockState getState() {
    return state;
  }

  public synchronized Role getOwnerRole() {
    return ownerRole;
  }

  public synchronized String getOwnerId() {
    return ownerId;
  }

  public synchronized long getLockedAt() {
    return lockedAt;
  }

  public synchronized void setLock(LockState state, Role role, String id) {
    this.state = state;
    this.ownerRole = role;
    this.ownerId = id;
    this.lockedAt = System.currentTimeMillis();
  }

  public synchronized void clear() {
    this.state = LockState.IDLE;
    this.ownerRole = null;
    this.ownerId = null;
    this.lockedAt = 0;
  }
}
