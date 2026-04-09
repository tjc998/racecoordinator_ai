package com.antigravity.race;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public abstract class ServerToClientObject {

  private String objectId;

  public ServerToClientObject() {
    this.objectId = UUID.randomUUID().toString();
  }

  public ServerToClientObject(String objectId) {
    if (objectId == null) {
      this.objectId = UUID.randomUUID().toString();
    } else {
      this.objectId = objectId;
    }
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }
}
