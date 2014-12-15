package com.wordnik.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Report {
  @JsonProperty("reported_user_id")
  private String reported_user_id = null;
  @JsonProperty("reported_comment_id")
  private String reported_comment_id = null;
  public String getReported_user_id() {
    return reported_user_id;
  }
  public void setReported_user_id(String reported_user_id) {
    this.reported_user_id = reported_user_id;
  }

  public String getReported_comment_id() {
    return reported_comment_id;
  }
  public void setReported_comment_id(String reported_comment_id) {
    this.reported_comment_id = reported_comment_id;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Report {\n");
    sb.append("  reported_user_id: ").append(reported_user_id).append("\n");
    sb.append("  reported_comment_id: ").append(reported_comment_id).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

