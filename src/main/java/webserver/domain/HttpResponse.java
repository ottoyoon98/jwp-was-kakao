package webserver.domain;

import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
public class HttpResponse {
    private final HttpStatus status;
    private final String contentType;
    private final long contentLength;
    private final String location;
    private final Map<String, String> customHeaders = new TreeMap<>();
    private final byte[] body;

    private HttpResponse(HttpResponseBuilder builder) {
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.contentLength = builder.contentLength;
        this.location = builder.location;
        this.customHeaders.putAll(builder.customHeaders);
        this.body = builder.body;
    }

    public static HttpResponseBuilder builder(HttpStatus status) {
        return new HttpResponseBuilder(status);
    }

    public static class HttpResponseBuilder {
        private final HttpStatus status;
        private final Map<String, String> customHeaders = new TreeMap<>();
        private String contentType = null;
        private long contentLength;
        private String location = null;
        private byte[] body;

        public HttpResponseBuilder(HttpStatus status) {
            this.status = status;
        }

        public HttpResponseBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpResponseBuilder contentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public HttpResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public HttpResponseBuilder addHeader(String key, String value) {
            this.customHeaders.put(key, value);
            return this;
        }

        public HttpResponseBuilder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponseBuilder body(String body) {
            this.body = body.getBytes();
            return this;
        }

        public HttpResponseBuilder cookie(String sessionId) {
            System.out.println(sessionId);
            if (sessionId == null) { // isEmpty, isBlank 쓰면 에러 발생.
                this.customHeaders.put("Set-Cookie", (new HttpCookie().getCookie("JSESSIONID")));
            }
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }

    }
}
