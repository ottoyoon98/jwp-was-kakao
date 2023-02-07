package webserver.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FileExtensions {
    JPG(FileDirectory.STATIC, "image/jpeg"),
    JPEG(FileDirectory.STATIC, "image/jpeg"),
    PNG(FileDirectory.STATIC, "image/png"),
    GIF(FileDirectory.STATIC, "image/gif"),
    ICO(FileDirectory.STATIC, "image/x-icon"),
    TXT(FileDirectory.STATIC, "text/plain"),
    HTML(FileDirectory.TEMPLATES, "text/html"),
    CSS(FileDirectory.STATIC, "text/css"),
    JS(FileDirectory.STATIC, "text/javascript"),
    XML(FileDirectory.STATIC, "application/xml"),
    JSON(FileDirectory.STATIC, "application/json"),
    TTF(FileDirectory.STATIC, "application/x-font-ttf"),
    WOFF(FileDirectory.STATIC, "application/x-font-woff"),
    WOFF2(FileDirectory.STATIC, "application/x-font-woff"),
    EOT(FileDirectory.STATIC, "application/vnd.ms-fontobject"),
    SVG(FileDirectory.STATIC, "image/svg+xml"),
    ZIP(FileDirectory.STATIC, "application/zip"),

    DEFAULT(null, "text/plain"),
    ;

    private static final Map<String, FileExtensions> enumMapper = new HashMap<>();

    static {
        Arrays.stream(FileExtensions.values())
                .forEach(item -> enumMapper.putIfAbsent(item.toString(), item));
    }

    private final FileDirectory directory;
    private final String contentType;

    public static FileExtensions of(String fileExtension) {
        return enumMapper.getOrDefault(fileExtension.toUpperCase(), FileExtensions.DEFAULT);
    }

    public String getDirectory() {
        return directory.getDirectory();
    }
}
