package com.wordnik.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {
  @JsonProperty("event_id")
  private String event_id = null;
  @JsonProperty("latitude")
  private Float latitude = null;
  @JsonProperty("longitude")
  private Float longitude = null;
  public String getEvent_id() {
    return event_id;
  }
  public void setEvent_id(String event_id) {
    this.event_id = event_id;
  }

  public Float getLatitude() {
    return latitude;
  }
  public void setLatitude(Float latitude) {
    this.latitude = latitude;
  }

  public Float getLongitude() {
    return longitude;
  }
  public void setLongitude(Float longitude) {
    this.longitude = longitude;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Location {\n");
    sb.append("  event_id: ").append(event_id).append("\n");
    sb.append("  latitude: ").append(latitude).append("\n");
    sb.append("  longitude: ").append(longitude).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

