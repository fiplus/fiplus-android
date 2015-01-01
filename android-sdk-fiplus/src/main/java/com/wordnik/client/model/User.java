package com.wordnik.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
  @JsonProperty("email")
  private String email = null;
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    sb.append("  email: ").append(email).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

