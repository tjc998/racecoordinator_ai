package com.antigravity.auth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
  private static final AuthService instance = new AuthService();

  // Store valid tokens.
  // For a simple setup, any token here implies DIRECTOR role (since ADMIN is IP-based).
  private final Map<String, Boolean> activeTokens = new ConcurrentHashMap<>();

  private AuthService() {}

  public static AuthService getInstance() {
    return instance;
  }

  public String generateDirectorToken() {
    String token = UUID.randomUUID().toString();
    activeTokens.put(token, true);
    return token;
  }

  public boolean isValidToken(String token) {
    if (token == null) {
      return false;
    }
    return activeTokens.containsKey(token);
  }

  public void invalidateToken(String token) {
    if (token != null) {
      activeTokens.remove(token);
    }
  }
}
