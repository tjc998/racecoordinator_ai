package com.antigravity.auth;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

public class AuthUtil {

  public static Role getRole(Context ctx) {
    Role role = ctx.attribute("role");
    return role != null ? role : Role.VIEWER;
  }

  public static void requireRole(Context ctx, Role requiredRole) {
    Role currentRole = getRole(ctx);
    if (!currentRole.isAtLeast(requiredRole)) {
      if (currentRole == Role.VIEWER) {
        throw new UnauthorizedResponse("Authentication required");
      } else {
        throw new ForbiddenResponse("Insufficient permissions");
      }
    }
  }

  public static boolean hasRole(Context ctx, Role requiredRole) {
    return getRole(ctx).isAtLeast(requiredRole);
  }
}
