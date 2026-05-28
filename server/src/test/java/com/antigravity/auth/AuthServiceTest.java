package com.antigravity.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AuthServiceTest {

  @Test
  public void testGenerateAndValidateToken() {
    AuthService authService = AuthService.getInstance();

    String token = authService.generateDirectorToken();
    assertNotNull(token);
    assertTrue(authService.isValidToken(token));
  }

  @Test
  public void testInvalidateToken() {
    AuthService authService = AuthService.getInstance();

    String token = authService.generateDirectorToken();
    assertTrue(authService.isValidToken(token));

    authService.invalidateToken(token);
    assertFalse(authService.isValidToken(token));
  }

  @Test
  public void testInvalidToken() {
    AuthService authService = AuthService.getInstance();
    assertFalse(authService.isValidToken(null));
    assertFalse(authService.isValidToken("some-random-token"));
  }
}
