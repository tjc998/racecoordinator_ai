package com.antigravity.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RoleTest {

  @Test
  public void testIsAtLeast() {
    assertTrue(Role.ADMIN.isAtLeast(Role.ADMIN));
    assertTrue(Role.ADMIN.isAtLeast(Role.DIRECTOR));
    assertTrue(Role.ADMIN.isAtLeast(Role.VIEWER));

    assertFalse(Role.DIRECTOR.isAtLeast(Role.ADMIN));
    assertTrue(Role.DIRECTOR.isAtLeast(Role.DIRECTOR));
    assertTrue(Role.DIRECTOR.isAtLeast(Role.VIEWER));

    assertFalse(Role.VIEWER.isAtLeast(Role.ADMIN));
    assertFalse(Role.VIEWER.isAtLeast(Role.DIRECTOR));
    assertTrue(Role.VIEWER.isAtLeast(Role.VIEWER));
  }
}
