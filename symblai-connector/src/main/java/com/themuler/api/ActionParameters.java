package com.themuler.api;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.util.Map;

public class ActionParameters {


  @Parameter
  @DisplayName("Url")
  private String url;
  @Parameter
  @DisplayName("HttpMethod")
  private Method method;
  @Parameter
  @DisplayName("Headers")
  private Map<String, Object> headers;
  @Parameter
  @DisplayName("QueryParams")
  private Map<String, Object> queryParams;

  @Parameter
  @DisplayName("Body Content Type")
  @Optional(defaultValue = "application/json")
  private String contentType;

  public String getContentType() {
    return contentType;
  }

  public String getUrl() {
    return url;
  }

  public Method getMethod() {
    return method;
  }

  public Map<String, Object> getHeaders() {
    return headers;
  }

  public Map<String, Object> getQueryParams() {
    return queryParams;
  }

}
