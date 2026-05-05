package com.antigravity.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class CustomHeat {
  @BsonProperty("driver_indices")
  @JsonProperty("driver_indices")
  @JsonAlias("driverIndices")
  private final List<Integer> driverIndices;

  public CustomHeat() {
    this.driverIndices = new ArrayList<>();
  }

  @BsonCreator
  @JsonCreator
  public CustomHeat(
      @BsonProperty("driver_indices") @JsonProperty("driver_indices") @JsonAlias("driverIndices")
          List<Integer> driverIndices) {
    this.driverIndices = driverIndices != null ? driverIndices : new ArrayList<>();
  }

  public List<Integer> getDriverIndices() {
    return driverIndices;
  }
}
