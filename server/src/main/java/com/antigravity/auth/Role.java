package com.antigravity.auth;

import io.javalin.core.security.RouteRole;

public enum Role implements RouteRole {
  ADMIN(3),
  DIRECTOR(2),
  VIEWER(1);

  private final int level;

  Role(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

  public boolean isAtLeast(Role requiredRole) {
    return this.level >= requiredRole.getLevel();
  }
}
