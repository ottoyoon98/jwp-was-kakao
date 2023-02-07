package webserver.domain;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    OPTIONS,
    HEAD,
    ;

    private static final Map<String, HttpMethod> enumMapper = new HashMap<>();

    static {
        Arrays.stream(HttpMethod.values())
                .forEach(item -> enumMapper.put(item.toString(), item));
    }

    public static HttpMethod of(String method) {
        return Optional.ofNullable(enumMapper.get(method)).orElseThrow(IllegalArgumentException::new);
    }
}
