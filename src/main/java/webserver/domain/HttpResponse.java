package webserver.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private final StringJoiner stringJoiner = new StringJoiner("\r\n");

    private final HttpStatus status;
    private final String contentType;
    private final long contentLength;
    private final String location;
    private final Map<String, String> customHeaders = new TreeMap<>();
    private final byte[] body;

    private final byte[] response;

    private HttpResponse(HttpResponseBuilder builder) {
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.contentLength = builder.contentLength;
        this.location = builder.location;
        this.customHeaders.putAll(builder.customHeaders);
        this.body = builder.body;
        this.response = concat(writeAsByte(), body);
    }

    public static HttpResponseBuilder builder(HttpStatus status) {
        return new HttpResponseBuilder(status);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getLocation() {
        return location;
    }

    public byte[] getBody() {
        return body;
    }

    private byte[] writeAsByte() {
        stringJoiner
                .add("HTTP/1.1 " + status.getValue() + " " + status.getReasonPhrase())
                .add("Content-Length: " + contentLength)
        ;
        if (contentType != null) {
            stringJoiner.add("Content-Type: " + contentType);
        }
        if (location != null) {
            stringJoiner.add("Location: " + location);
        }
        customHeaders.forEach((k, v) -> stringJoiner.add(k + ": " + v));
        stringJoiner.add("").add("");

        String responseHeaders = stringJoiner.toString();

        return responseHeaders.getBytes();
    }

    private byte[] concat(byte[] a, byte[] b) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(a);
            outputStream.write(b);
            outputStream.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return outputStream.toByteArray();
    }

    public byte[] getAsByte() {
        return response;
    }

    public static class HttpResponseBuilder {
        private final HttpStatus status;
        private String contentType = null;
        private long contentLength;
        private String location = null;
        private final Map<String, String> customHeaders = new TreeMap<>();
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

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
