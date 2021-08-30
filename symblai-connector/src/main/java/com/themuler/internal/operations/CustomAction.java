package com.themuler.internal.operations;

import com.google.gson.Gson;
import com.themuler.api.ActionParameters;
import com.themuler.api.Method;
import com.themuler.internal.connection.SymblAIConnection;
import com.themuler.internal.utils.Constants;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.entity.InputStreamHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.themuler.internal.utils.Constants.SYMBLAI_BASE_URL;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;
import static org.mule.runtime.http.api.domain.message.request.HttpRequest.builder;

@Alias("AsyncAPI")
public class CustomAction {

  private static final Logger log = LoggerFactory.getLogger(CustomAction.class);
  private final Gson gson = new Gson();

  @Alias(value = "CustomAction", description = "Execute all SymblAI APIs")
  @MediaType(APPLICATION_JSON)
  public Result<InputStream, Void> sendAPIRequest(@Connection SymblAIConnection connection,
                                                  @ParameterGroup(name = "Action Parameters") ActionParameters parameters,
                                                  @Content InputStream payload) throws Exception {


    HttpRequestBuilder builder = builder();
    String url = SYMBLAI_BASE_URL + parameters.getUrl();
    builder.uri(new URI(url));
    Set<Map.Entry<String, Object>> headersEntrySet = parameters.getHeaders().entrySet();
    Set<Map.Entry<String, Object>> queryParamsEntrySet = parameters.getQueryParams().entrySet();
    for (Map.Entry<String, Object> entry : headersEntrySet) {
      builder.addHeader(entry.getKey(), entry.getValue().toString());
    }
    for (Map.Entry<String, Object> entry : queryParamsEntrySet) {
      builder.addQueryParam(entry.getKey(), entry.getValue().toString());
    }
    log.info("Making a call to {}", url);
    Method method = parameters.getMethod();
    switch (method) {
      case GET:
        builder.method("GET");
        break;
      case PUT:
      case PATCH:
      case POST:
        HttpEntity httpEntity = new InputStreamHttpEntity(payload);
        builder.method(method.name()).entity(httpEntity);
        break;
      case DELETE:
        builder.method("DELETE");
        break;
    }
    builder.addHeader("Content-Type", parameters.getContentType());
    HttpResponse httpResponse = connection.executeHttpRequest(builder);
    log.info("HttpResponse:=> ReasonPhrase {}, StatusCode {}", httpResponse.getReasonPhrase(), httpResponse.getStatusCode());
    return Result.<InputStream, Void>builder().output(httpResponse.getEntity().getContent()).mediaType(Constants.CONTENT_TYPE).build();
  }

}
