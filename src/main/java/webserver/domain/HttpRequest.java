package webserver.domain;

import lombok.Builder;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ToString
public class HttpRequest {
    private final Headers headers = new Headers();
    private StatusLine statusLine;

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setStatusLine(reader.readLine());

        while (true) {
            String line = reader.readLine();
            if (line == null || line.isEmpty()) break;
            httpRequest.addHeader(line);
        }

        return httpRequest;
    }

    private void setStatusLine(final String statusLine) {
        String[] parsedStatusLine = statusLine.split(" ");
        this.statusLine = StatusLine.builder()
                .method(parsedStatusLine[0])
                .path(parsedStatusLine[1])
                .protocolVersion(parsedStatusLine[2])
                .build();
    }

    private void addHeader(final String header) {
        String[] parsedHeader = header.split(": ");
        headers.put(parsedHeader[0], parsedHeader[1]);
    }

    public String getMethod() {
        return this.statusLine.method;
    }

    public String getURI() {
        return this.statusLine.path;
    }

    public String getVersion() {
        return this.statusLine.protocolVersion;
    }

    public Optional<String> getHeader(final String key) {
        return Optional.ofNullable(headers.get(key));
    }

    @ToString
    @Builder
    private static class StatusLine {
        private final String method;
        private final String path;
        private final String protocolVersion;
    }

    @ToString
    private static class Headers {
        private final Map<String, String> headers = new HashMap<>();

        public void put(String key, String value) {
            headers.put(key, value);
        }

        public String get(String key) {
            return headers.get(key);
        }
    }
}
