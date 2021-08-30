package com.themuler.internal.model;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

public class Authentication {

  @Parameter
  @DisplayName("API Key")
  private String apiKey;

  @Parameter
  @DisplayName("API Secret")
  private String apiSecret;

  @Parameter
  @DisplayName("Type")
  @Optional(defaultValue = "application")
  private String type;


  public String getApiKey() {
    return apiKey;
  }

  public String getApiSecret() {
    return apiSecret;
  }

  public String getType() {
    return type;
  }
}
