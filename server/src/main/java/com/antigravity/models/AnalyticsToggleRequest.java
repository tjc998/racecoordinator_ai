package com.antigravity.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnalyticsToggleRequest {
    @JsonProperty("enabled")
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
