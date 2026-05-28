package com.antigravity.handlers;

import com.antigravity.auth.AuthService;
import com.antigravity.auth.Role;
import com.antigravity.service.ServerConfigService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AuthTaskHandler {

  private final ServerConfigService configService;

  public AuthTaskHandler(Javalin app, ServerConfigService configService) {
    this.configService = configService;
    app.post("/api/auth/login", this::handleLogin, Role.VIEWER);
  }

  private void handleLogin(Context ctx) {
    LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
    String configuredPassword = configService.getDirectorPassword();

    // If no password is set, or password matches
    if (configuredPassword == null || configuredPassword.isEmpty()) {
      // Security decision: if no password configured, do we allow everyone?
      // Probably safest to deny, or require setting it first.
      // Let's assume an empty password means it's not setup, so login fails until setup.
      ctx.status(401).json(new LoginResponse(false, null, "Director password is not configured."));
      return;
    }

    if (configuredPassword.equals(request.password)) {
      String token = AuthService.getInstance().generateDirectorToken();
      ctx.json(new LoginResponse(true, token, null));
    } else {
      ctx.status(401).json(new LoginResponse(false, null, "Invalid password"));
    }
  }

  private static class LoginRequest {
    @JsonProperty("password")
    public String password;
  }

  private static class LoginResponse {
    public boolean success;
    public String token;
    public String error;

    public LoginResponse(boolean success, String token, String error) {
      this.success = success;
      this.token = token;
      this.error = error;
    }
  }
}
