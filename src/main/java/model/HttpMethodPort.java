package model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface HttpMethodPort {
    default Map<String, String> parseQueryParams(String queryParams) {
        String[] parsedParams = queryParams.split("&");
        return Arrays.stream(parsedParams)
                .map(item -> item.split("="))
                .collect(Collectors.toMap(k -> k[0], v -> v[1]));
    }

    default List<String> parsePathVariables(String pathVariables) {
        String[] parsedVariables = pathVariables.split("/");
        return Arrays.asList(parsedVariables);
    }

    HttpResponse getResponse();
}
