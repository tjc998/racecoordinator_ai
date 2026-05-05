package com.antigravity.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class CustomRotation {
  @BsonProperty("num_drivers")
  @JsonProperty("num_drivers")
  @JsonAlias("numDrivers")
  private final int numDrivers;

  @BsonProperty("heats")
  @JsonProperty("heats")
  private final List<CustomHeat> heats;

  public CustomRotation() {
    this.numDrivers = 0;
    this.heats = new ArrayList<>();
  }

  @BsonCreator
  @JsonCreator
  public CustomRotation(
      @BsonProperty("num_drivers") @JsonProperty("num_drivers") @JsonAlias("numDrivers")
          int numDrivers,
      @BsonProperty("heats") @JsonProperty("heats") @JsonAlias("heats") List<CustomHeat> heats) {
    this.numDrivers = numDrivers;
    this.heats = heats != null ? heats : new ArrayList<>();
  }

  public int getNumDrivers() {
    return numDrivers;
  }

  public List<CustomHeat> getHeats() {
    return heats;
  }
}
