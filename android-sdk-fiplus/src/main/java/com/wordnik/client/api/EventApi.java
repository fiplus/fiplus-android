package com.wordnik.client.api;

import com.wordnik.client.ApiException;
import com.wordnik.client.ApiInvoker;
import com.wordnik.client.model.Event;
import com.wordnik.client.model.Undocumented;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.Comment;
import com.wordnik.client.model.Time;
import com.wordnik.client.model.Icebreaker;
import com.wordnik.client.model.Report;
import com.wordnik.client.model.Answer;
import java.util.*;
import java.io.File;

public class EventApi {
  String basePath = "/dev/extensions";
  ApiInvoker apiInvoker = ApiInvoker.getInstance();

  public void addHeader(String key, String value) {
    getInvoker().addDefaultHeader(key, value);
  }

  public ApiInvoker getInvoker() {
    return apiInvoker;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getBasePath() {
    return basePath;
  }

  public String post_ (Event body) throws ApiException {
    // create path and map variables
    String path = "/event".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String post_icebreaker_answer (Answer body) throws ApiException {
    // create path and map variables
    String path = "/event/icebreaker/answer".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String put_icebreaker (Icebreaker body) throws ApiException {
    // create path and map variables
    String path = "/event/icebreaker".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String put_event_user_user (Undocumented body, String event, String user) throws ApiException {
    // verify required params are set
    if(event == null || user == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/event/{event}/user/{user}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "event" + "\\}", apiInvoker.escapeString(event.toString())).replaceAll("\\{" + "user" + "\\}", apiInvoker.escapeString(user.toString()));

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String delete_event_id_user_user_id (String event_id, String user_id) throws ApiException {
    // verify required params are set
    if(event_id == null || user_id == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/event/{event_id}/user/{user_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "event_id" + "\\}", apiInvoker.escapeString(event_id.toString())).replaceAll("\\{" + "user_id" + "\\}", apiInvoker.escapeString(user_id.toString()));

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "DELETE", queryParams, null, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String put_event_id_time (Time body, String event_id) throws ApiException {
    // verify required params are set
    if(event_id == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/event/{event_id}/time".replaceAll("\\{format\\}","json").replaceAll("\\{" + "event_id" + "\\}", apiInvoker.escapeString(event_id.toString()));

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String post_event_id_time_time_id_user_user_id (Undocumented body, String event_id, String time_id, String user_id) throws ApiException {
    // verify required params are set
    if(event_id == null || time_id == null || user_id == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/event/{event_id}/time/{time_id}/user/{user_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "event_id" + "\\}", apiInvoker.escapeString(event_id.toString())).replaceAll("\\{" + "time_id" + "\\}", apiInvoker.escapeString(time_id.toString())).replaceAll("\\{" + "user_id" + "\\}", apiInvoker.escapeString(user_id.toString()));

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String put_location (Location body) throws ApiException {
    // create path and map variables
    String path = "/event/location".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String post_event_id_location_location_id (Undocumented body, String event_id, String location_id) throws ApiException {
    // verify required params are set
    if(event_id == null || location_id == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/event/{event_id}/location/{location_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "event_id" + "\\}", apiInvoker.escapeString(event_id.toString())).replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString()));

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String put_comment (Comment body) throws ApiException {
    // create path and map variables
    String path = "/event/comment".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  public String put_report (Report body) throws ApiException {
    // create path and map variables
    String path = "/event/report".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams, contentType);
      if(response != null){
        return (String) ApiInvoker.deserialize(response, "", String.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      if(ex.getCode() == 404) {
        return null;
      }
      else {
        throw ex;
      }
    }
  }
  }

