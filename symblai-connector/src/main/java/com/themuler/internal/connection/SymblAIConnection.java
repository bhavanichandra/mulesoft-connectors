package com.themuler.internal.connection;

import com.google.gson.Gson;
import com.themuler.internal.model.Authentication;
import com.themuler.internal.model.AuthenticationResponse;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.domain.entity.ByteArrayHttpEntity;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.themuler.internal.utils.Constants.SYMBLAI_BASE_URL;
import static com.themuler.internal.utils.Constants.SYMBLAI_TOKEN_URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mule.runtime.http.api.HttpConstants.Method.POST;
import static org.mule.runtime.http.api.domain.message.request.HttpRequest.builder;

public class SymblAIConnection {
  private static final Logger log = LoggerFactory.getLogger(SymblAIConnection.class);
  private final int timeout = 40000;
  private final Gson gson;
  private String token;
  private HttpClient httpClient;
  private Authentication authentication;

  public SymblAIConnection(HttpService httpService, Authentication authentication) {
    init(httpService);
    this.authentication = authentication;
    gson = new Gson();
  }

  void init(HttpService httpService) {
    HttpClientConfiguration.Builder builder = new HttpClientConfiguration.Builder();
    builder.setName("SymblAI");
    httpClient = httpService.getClientFactory().create(builder.build());
    httpClient.start();
  }

  public boolean isConnected(Authentication authentication) throws Exception {
    String authenticationAsJSON = gson.toJson(authentication);
    HttpEntity requestEntity = new ByteArrayHttpEntity(authenticationAsJSON.getBytes());
    String url = SYMBLAI_BASE_URL + SYMBLAI_TOKEN_URL;
    HttpRequest request = builder().method(POST)
            .uri(url)
            .entity(requestEntity)
            .addHeader("Authorization", "Bearer " + getToken())
            .addHeader("Content-Type", "application/json").build();
    try {
      HttpResponse httpResponse = httpClient.send(request, timeout, true, null);
      log.info("Response:=> ReasonPhrase {}, StatusCode: {}", httpResponse.getReasonPhrase(), httpResponse.getStatusCode());
      HttpEntity responseEntity = httpResponse.getEntity();
      byte[] bytes = responseEntity.getBytes();
      String responseStr = new String(bytes, UTF_8);
      AuthenticationResponse authenticationResponse = gson.fromJson(responseStr, AuthenticationResponse.class);
      setToken(authenticationResponse.getAccessToken());
      return this.token != null && !this.token.isEmpty();
    } catch (Exception ex) {
      throw new Exception(ex.getMessage());
    }
  }


  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void closeConnection() {
    httpClient.stop();
  }

  public HttpResponse executeHttpRequest(HttpRequestBuilder requestBuilder) throws IOException, TimeoutException {
    HttpResponse response;
    String authorizationHeader = "Bearer " + getToken();
    HttpRequest httpRequest = requestBuilder
            .addHeader("Authorization", authorizationHeader)
            .build();
    response = httpClient.send(httpRequest, timeout, true, null);
    return response;
  }


}
