package com.antigravity.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import org.junit.Before;
import org.junit.Test;

public class AuthUtilTest {

  private Context ctx;

  @Before
  public void setUp() {
    ctx = mock(Context.class);
  }

  @Test
  public void testGetRole_DefaultViewer() {
    when(ctx.attribute("role")).thenReturn(null);
    assertEquals(Role.VIEWER, AuthUtil.getRole(ctx));
  }

  @Test
  public void testGetRole_ExistingRole() {
    when(ctx.attribute("role")).thenReturn(Role.DIRECTOR);
    assertEquals(Role.DIRECTOR, AuthUtil.getRole(ctx));
  }

  @Test
  public void testHasRole() {
    when(ctx.attribute("role")).thenReturn(Role.DIRECTOR);
    assertTrue(AuthUtil.hasRole(ctx, Role.VIEWER));
    assertTrue(AuthUtil.hasRole(ctx, Role.DIRECTOR));
    assertFalse(AuthUtil.hasRole(ctx, Role.ADMIN));
  }

  @Test(expected = UnauthorizedResponse.class)
  public void testRequireRole_ViewerThrowsUnauthorized() {
    when(ctx.attribute("role")).thenReturn(Role.VIEWER);
    AuthUtil.requireRole(ctx, Role.DIRECTOR);
  }

  @Test(expected = ForbiddenResponse.class)
  public void testRequireRole_DirectorThrowsForbidden() {
    when(ctx.attribute("role")).thenReturn(Role.DIRECTOR);
    AuthUtil.requireRole(ctx, Role.ADMIN);
  }

  @Test
  public void testRequireRole_Success() {
    when(ctx.attribute("role")).thenReturn(Role.ADMIN);
    AuthUtil.requireRole(ctx, Role.DIRECTOR); // Should not throw
  }
}
