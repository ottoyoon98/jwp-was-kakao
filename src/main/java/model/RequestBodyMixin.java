package model;

import java.util.Map;

public interface RequestBodyMixin {
    default Map<String, String> parseBody(String body) {
        return null;
    }
}
