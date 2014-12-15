package com.wordnik.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HistoryRequest {
  @JsonProperty("duration")
  private Integer duration = null;
  @JsonProperty("targetuser")
  private String targetuser = null;
  public Integer getDuration() {
    return duration;
  }
  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public String getTargetuser() {
    return targetuser;
  }
  public void setTargetuser(String targetuser) {
    this.targetuser = targetuser;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class HistoryRequest {\n");
    sb.append("  duration: ").append(duration).append("\n");
    sb.append("  targetuser: ").append(targetuser).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

