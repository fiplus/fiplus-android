package com.wordnik.client.api;

import com.wordnik.client.ApiException;
import com.wordnik.client.ApiInvoker;
import com.wordnik.client.model.PasswordRequest;
import com.wordnik.client.model.Resources;
import com.wordnik.client.model.Login;
import com.wordnik.client.model.ProfileRequest;
import com.wordnik.client.model.CustomSetting;
import com.wordnik.client.model.Register;
import com.wordnik.client.model.DevicesResponse;
import com.wordnik.client.model.PasswordResponse;
import com.wordnik.client.model.Session;
import com.wordnik.client.model.Success;
import com.wordnik.client.model.ProfileResponse;
import com.wordnik.client.model.CustomSettings;
import com.wordnik.client.model.DeviceRequest;
import java.util.*;
import java.io.File;

public class UserApi {
  String basePath = "http://localhost:8080/rest";
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

  public Resources getResources () throws ApiException {
    // create path and map variables
    String path = "/user".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams, contentType);
      if(response != null){
        return (Resources) ApiInvoker.deserialize(response, "", Resources.class);
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
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 403 reason: "Denied Access - No permission." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public CustomSettings getCustomSettings () throws ApiException {
    // create path and map variables
    String path = "/user/custom".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams, contentType);
      if(response != null){
        return (CustomSettings) ApiInvoker.deserialize(response, "", CustomSettings.class);
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
  //error info- code: 400 reason: "Bad Request - Request does not have a valid format, all required parameters, etc." model: <none>
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 403 reason: "Denied Access - No permission." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public Success setCustomSettings (CustomSettings body) throws ApiException {
    // verify required params are set
    if(body == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/user/custom".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (Success) ApiInvoker.deserialize(response, "", Success.class);
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
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 403 reason: "Denied Access - No permission." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public CustomSetting getCustomSetting (String setting) throws ApiException {
    // verify required params are set
    if(setting == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/user/custom/{setting}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "setting" + "\\}", apiInvoker.escapeString(setting.toString()));

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams, contentType);
      if(response != null){
        return (CustomSetting) ApiInvoker.deserialize(response, "", CustomSetting.class);
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
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 403 reason: "Denied Access - No permission." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public Success deleteCustomSetting (String setting) throws ApiException {
    // verify required params are set
    if(setting == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/user/custom/{setting}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "setting" + "\\}", apiInvoker.escapeString(setting.toString()));

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "DELETE", queryParams, null, headerParams, contentType);
      if(response != null){
        return (Success) ApiInvoker.deserialize(response, "", Success.class);
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
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public DevicesResponse getDevices () throws ApiException {
    // create path and map variables
    String path = "/user/device".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams, contentType);
      if(response != null){
        return (DevicesResponse) ApiInvoker.deserialize(response, "", DevicesResponse.class);
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
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public Success setDevice (DeviceRequest body) throws ApiException {
    // verify required params are set
    if(body == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/user/device".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (Success) ApiInvoker.deserialize(response, "", Success.class);
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
  //error info- code: 400 reason: "Bad Request - Request does not have a valid format, all required parameters, etc." model: <none>
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public PasswordResponse changePassword (Boolean reset, Boolean login, PasswordRequest body) throws ApiException {
    // verify required params are set
    if(body == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/user/password".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    if(!"null".equals(String.valueOf(reset)))
      queryParams.put("reset", String.valueOf(reset));
    if(!"null".equals(String.valueOf(login)))
      queryParams.put("login", String.valueOf(login));
    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (PasswordResponse) ApiInvoker.deserialize(response, "", PasswordResponse.class);
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
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public ProfileResponse getProfile () throws ApiException {
    // create path and map variables
    String path = "/user/profile".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams, contentType);
      if(response != null){
        return (ProfileResponse) ApiInvoker.deserialize(response, "", ProfileResponse.class);
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
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public Success updateProfile (ProfileRequest body) throws ApiException {
    // verify required params are set
    if(body == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/user/profile".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (Success) ApiInvoker.deserialize(response, "", Success.class);
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
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public Success register (Boolean login, Register body) throws ApiException {
    // verify required params are set
    if(body == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/user/register".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    if(!"null".equals(String.valueOf(login)))
      queryParams.put("login", String.valueOf(login));
    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (Success) ApiInvoker.deserialize(response, "", Success.class);
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
  //error info- code: 401 reason: "Unauthorized Access - No currently valid session available." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public Session getSession () throws ApiException {
    // create path and map variables
    String path = "/user/session".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams, contentType);
      if(response != null){
        return (Session) ApiInvoker.deserialize(response, "", Session.class);
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
  //error info- code: 400 reason: "Bad Request - Request does not have a valid format, all required parameters, etc." model: <none>
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public Session login (Login body) throws ApiException {
    // verify required params are set
    if(body == null ) {
       throw new ApiException(400, "missing required params");
    }
    // create path and map variables
    String path = "/user/session".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams, contentType);
      if(response != null){
        return (Session) ApiInvoker.deserialize(response, "", Session.class);
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
  //error info- code: 500 reason: "System Error - Specific reason is included in the error message." model: <none>
  public Success logout () throws ApiException {
    // create path and map variables
    String path = "/user/session".replaceAll("\\{format\\}","json");

    // query params
    Map<String, String> queryParams = new HashMap<String, String>();
    Map<String, String> headerParams = new HashMap<String, String>();

    String contentType = "application/json";

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "DELETE", queryParams, null, headerParams, contentType);
      if(response != null){
        return (Success) ApiInvoker.deserialize(response, "", Success.class);
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

