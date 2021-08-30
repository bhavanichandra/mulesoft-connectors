package com.themuler.internal.model;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

public class Authentication {

  @Parameter
  @DisplayName("App Id")
  private String appId;

  @Parameter
  @DisplayName("App Secret")
  private String appSecret;

  @Parameter
  @DisplayName("Type")
  @Optional(defaultValue = "application")
  private String type;

  public String getAppId() {
    return appId;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public String getType() {
    return type;
  }
}
