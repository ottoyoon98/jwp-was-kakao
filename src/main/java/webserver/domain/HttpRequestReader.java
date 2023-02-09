package webserver.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpRequestReader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpRequest httpRequest;

    private final URI uri;
    private final File pathFile;
    private final Map<String, String> queryParams;

    private final String path;
    private final String extension;
    private final HttpCookie HttpCookie;

    public HttpRequestReader(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        this.uri = URI.create(this.httpRequest.getURI());
        this.pathFile = new File(uri.getPath());
        this.queryParams = parseQueryParams();
        this.path = uri.getPath();
        this.extension = parseExtension();
        this.HttpCookie = parseCookie();
    }

    private HttpCookie parseCookie() {
        return new HttpCookie(httpRequest.getHeader("Cookie")
                .orElse(""));
    }

    private String parseExtension() {
        String fileName = pathFile.getName();
        String[] parsedFileName = fileName.split("\\.");
        return parsedFileName.length <= 1 ? null : parsedFileName[parsedFileName.length - 1];
    }

    private Map<String, String> parseQueryParams() {
        return Optional.ofNullable(uri.getQuery())
                .map(this::parseQuery)
                .orElse(new HashMap<>());
    }

    private Map<String, String> parseQuery(String query) {
        return Arrays.stream(query.split("&"))
                .map(item -> item.split("="))
                .collect(Collectors.toMap(k -> k[0], v -> v[1]));
    }

    public boolean hasParams() {
        return !queryParams.isEmpty();
    }

    public boolean isFile() {
        return this.extension != null;
    }

    public boolean isDirectory() {
        return this.extension == null;
    }

    public String getExtension() {
        return extension;
    }

    public String getPath() {
        return path;
    }

    public HttpCookie getHttpCookie() {
        return HttpCookie;
    }

    public <T> T bindBody(Class<T> clazz) {
        return objectMapper.convertValue(parseQuery(httpRequest.getBody()), clazz);
    }

    public <T> T bindQuery(Class<T> clazz) {
        return objectMapper.convertValue(queryParams, clazz);
    }
}
