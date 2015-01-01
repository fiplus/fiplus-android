package com.wordnik.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Icebreaker {
  @JsonProperty("event_id")
  private String event_id = null;
  @JsonProperty("question")
  private String question = null;
  @JsonProperty("answer")
  private String answer = null;
  public String getEvent_id() {
    return event_id;
  }
  public void setEvent_id(String event_id) {
    this.event_id = event_id;
  }

  public String getQuestion() {
    return question;
  }
  public void setQuestion(String question) {
    this.question = question;
  }

  public String getAnswer() {
    return answer;
  }
  public void setAnswer(String answer) {
    this.answer = answer;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Icebreaker {\n");
    sb.append("  event_id: ").append(event_id).append("\n");
    sb.append("  question: ").append(question).append("\n");
    sb.append("  answer: ").append(answer).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

