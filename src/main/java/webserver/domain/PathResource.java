package webserver.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PathResource {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final URI uri;
    private final File pathFile;
    private final Map<String, String> queryParams;

    private final String rawPath;
    private final String extension;


    private PathResource(URI uri, File pathFile) {
        this.uri = uri;
        this.pathFile = pathFile;
        this.queryParams = extractParams();
        this.extension = extractExtension();
        this.rawPath = uri.getPath();
    }

    public PathResource(String uri) {
        this(URI.create(uri), new File(uri));
    }

    private String extractExtension() {
        String fileName = pathFile.getName();
        String[] parsedFileName = fileName.split("\\.");
        return parsedFileName.length <= 1 ? null : parsedFileName[parsedFileName.length - 1];
    }

    private Map<String, String> extractParams() {
        Optional<String> queryParams = Optional.ofNullable(uri.getQuery());
        return queryParams
                .map(p -> Arrays.stream(p.split("&"))
                        .map(item -> item.split("="))
                        .collect(Collectors.toMap(k -> k[0], v -> v[1]))
                )
                .orElse(new HashMap<>());
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

    public String getRawPath() {
        return rawPath;
    }

    public <T> T convertParams(Class<T> clazz) {
        return objectMapper.convertValue(queryParams, clazz);
    }
}
