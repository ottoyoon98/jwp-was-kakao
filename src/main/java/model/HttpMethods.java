package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum HttpMethods {
    GET {
        public HttpMethodPort createPort(HttpRequest httpRequest) {
            return new GetMethodAdapter(httpRequest);
        }
    },
    POST {
        public HttpMethodPort createPort(HttpRequest httpRequest) {
            return new PostMethodAdapter(httpRequest);
        }
    },
//    PUT,
//    DELETE,
//    PATCH,
    ;

    abstract protected HttpMethodPort createPort(HttpRequest httpRequest);

    private static final Map<String, HttpMethods> enumMapper = new HashMap<>();

    static {
        Arrays.stream(HttpMethods.values())
                .forEach(item -> enumMapper.putIfAbsent(item.toString(), item));
    }

    public static HttpMethodPort getAdapterFrom(HttpRequest httpRequest) {
        return enumMapper.get(httpRequest.getMethod()).createPort(httpRequest);
    }
}
