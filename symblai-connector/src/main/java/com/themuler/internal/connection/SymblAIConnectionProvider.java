package com.themuler.internal.connection;

import com.themuler.internal.model.Authentication;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.http.api.HttpService;

import javax.inject.Inject;

import static org.mule.runtime.api.connection.ConnectionValidationResult.failure;
import static org.mule.runtime.api.connection.ConnectionValidationResult.success;

public class SymblAIConnectionProvider implements CachedConnectionProvider<SymblAIConnection> {

  @Inject
  private HttpService httpService;

  @ParameterGroup(name = "Authentication")
  private Authentication authentication;

  public Authentication getAuthentication() {
    return authentication;
  }

  @Override
  public SymblAIConnection connect() throws ConnectionException {
    return new SymblAIConnection(httpService, authentication);
  }

  @Override
  public void disconnect(SymblAIConnection connection) {
    connection.closeConnection();
  }

  @Override
  public ConnectionValidationResult validate(SymblAIConnection connection) {
    ConnectionValidationResult result;
    try {
      if (connection.isConnected(getAuthentication())) {
        result = success();
      } else {
        result = failure("Invalid Credentials", new ConnectionException("Invalid SymblAI Credentials"));
      }
    } catch (Exception ex) {
      result = failure("Internal Server Failure", ex);
    }
    return result;
  }
}
