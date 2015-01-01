package com.wordnik.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
public class Time {
  @JsonProperty("start")
  private Integer start = null;
  @JsonProperty("end")
  private Integer end = null;
  public Integer getStart() {
    return start;
  }
  public void setStart(Integer start) {
    this.start = start;
  }

  public Integer getEnd() {
    return end;
  }
  public void setEnd(Integer end) {
    this.end = end;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Time {\n");
    sb.append("  start: ").append(start).append("\n");
    sb.append("  end: ").append(end).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

