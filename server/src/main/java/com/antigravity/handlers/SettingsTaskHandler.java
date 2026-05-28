package com.antigravity.handlers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.antigravity.auth.Role;
import com.antigravity.service.ServerConfigService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.LoggerFactory;

/**
 * Handler for system-wide settings that are not persisted in the database but affect the runtime
 * behavior of the server.
 */
public class SettingsTaskHandler {

  private final ServerConfigService configService;

  public SettingsTaskHandler(Javalin app, ServerConfigService configService) {
    this.configService = configService;
    app.post("/api/settings/log-level", this::setLogLevel, Role.ADMIN);
    app.post("/api/settings/director-password", this::setDirectorPassword, Role.ADMIN);
    app.get("/api/settings/auth", this::getAuthSettings, Role.ADMIN);
  }

  /**
   * Sets the log level for the com.antigravity package at runtime.
   *
   * @param ctx Javalin context
   */
  private void setLogLevel(Context ctx) {
    String level = getQueryParam(ctx, "level");
    if (level == null) {
      setStatus(ctx, 400);
      setResult(ctx, "Level parameter is required");
      return;
    }

    try {
      LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
      // We target our main package to avoid overwhelming the logs with third-party library output
      ch.qos.logback.classic.Logger logger = loggerContext.getLogger("com.antigravity");
      logger.setLevel(Level.toLevel(level.toUpperCase()));

      setStatus(ctx, 200);
      setResult(ctx, "Server log level updated to " + level);
    } catch (Exception e) {
      setStatus(ctx, 500);
      setResult(ctx, "Error updating log level: " + e.getMessage());
    }
  }

  private void setDirectorPassword(Context ctx) {
    PasswordRequest req = ctx.bodyAsClass(PasswordRequest.class);
    configService.setDirectorPassword(req.password);
    setStatus(ctx, 200);
    setResult(ctx, "Director password updated");
  }

  private void getAuthSettings(Context ctx) {
    boolean hasDirectorPassword =
        configService.getDirectorPassword() != null
            && !configService.getDirectorPassword().isEmpty();
    ctx.json(new AuthSettingsResponse(hasDirectorPassword));
  }

  private static class PasswordRequest {
    @JsonProperty("password")
    public String password;
  }

  private static class AuthSettingsResponse {
    public boolean hasDirectorPassword;

    public AuthSettingsResponse(boolean hasDirectorPassword) {
      this.hasDirectorPassword = hasDirectorPassword;
    }
  }

  void setStatus(Context ctx, int status) {
    ctx.status(status);
  }

  void setResult(Context ctx, String result) {
    ctx.result(result);
  }

  String getQueryParam(Context ctx, String key) {
    return ctx.queryParam(key);
  }
}
