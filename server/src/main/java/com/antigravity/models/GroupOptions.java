package com.antigravity.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class GroupOptions {

  @BsonProperty("enabled")
  @JsonProperty("enabled")
  private final boolean enabled;

  @BsonProperty("max_groups")
  @JsonProperty("max_groups")
  private final int maxGroups;

  @BsonProperty("balance")
  @JsonProperty("balance")
  private final boolean balance;

  @BsonProperty("allow_empty_lanes")
  @JsonProperty("allow_empty_lanes")
  private final boolean allowEmptyLanes;

  @BsonProperty("force_multiple_of_max")
  @JsonProperty("force_multiple_of_max")
  private final boolean forceMultipleOfMax;

  @BsonProperty("rotate_group_heats")
  @JsonProperty("rotate_group_heats")
  private final boolean rotateGroupHeats;

  @BsonProperty("min_advancing")
  @JsonProperty("min_advancing")
  private final int minAdvancing;

  public GroupOptions() {
    this.enabled = false;
    this.maxGroups = 1;
    this.balance = false;
    this.allowEmptyLanes = true;
    this.forceMultipleOfMax = false;
    this.rotateGroupHeats = true;
    this.minAdvancing = 0;
  }

  @BsonCreator
  @JsonCreator
  public GroupOptions(
      @BsonProperty("enabled") @JsonProperty("enabled") Boolean enabled,
      @BsonProperty("max_groups") @JsonProperty("max_groups") Integer maxGroups,
      @BsonProperty("balance") @JsonProperty("balance") Boolean balance,
      @BsonProperty("allow_empty_lanes") @JsonProperty("allow_empty_lanes") Boolean allowEmptyLanes,
      @BsonProperty("force_multiple_of_max") @JsonProperty("force_multiple_of_max")
          Boolean forceMultipleOfMax,
      @BsonProperty("rotate_group_heats") @JsonProperty("rotate_group_heats")
          Boolean rotateGroupHeats,
      @BsonProperty("min_advancing") @JsonProperty("min_advancing") Integer minAdvancing) {
    this.enabled = enabled != null ? enabled : false;
    this.maxGroups = maxGroups != null ? maxGroups : 1;
    this.balance = balance != null ? balance : false;
    this.allowEmptyLanes = allowEmptyLanes != null ? allowEmptyLanes : true;
    this.forceMultipleOfMax = forceMultipleOfMax != null ? forceMultipleOfMax : false;
    this.rotateGroupHeats = rotateGroupHeats != null ? rotateGroupHeats : true;
    this.minAdvancing = minAdvancing != null ? minAdvancing : 0;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public int getMaxGroups() {
    return maxGroups;
  }

  public boolean isBalance() {
    return balance;
  }

  public boolean isAllowEmptyLanes() {
    return allowEmptyLanes;
  }

  public boolean isForceMultipleOfMax() {
    return forceMultipleOfMax;
  }

  public boolean isRotateGroupHeats() {
    return rotateGroupHeats;
  }

  public int getMinAdvancing() {
    return minAdvancing;
  }
}
