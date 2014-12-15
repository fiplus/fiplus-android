package com.wordnik.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Comment {
  @JsonProperty("event_id")
  private String event_id = null;
  @JsonProperty("user_id")
  private String user_id = null;
  @JsonProperty("comment")
  private String comment = null;
  public String getEvent_id() {
    return event_id;
  }
  public void setEvent_id(String event_id) {
    this.event_id = event_id;
  }

  public String getUser_id() {
    return user_id;
  }
  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getComment() {
    return comment;
  }
  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Comment {\n");
    sb.append("  event_id: ").append(event_id).append("\n");
    sb.append("  user_id: ").append(user_id).append("\n");
    sb.append("  comment: ").append(comment).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

