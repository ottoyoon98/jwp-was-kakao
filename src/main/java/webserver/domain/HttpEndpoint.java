package webserver.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class HttpEndpoint {
    private final HttpMethod method;
    private final String uri;
}
