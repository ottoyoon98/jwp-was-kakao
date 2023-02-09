package webserver.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie() {
        UUID uuid = UUID.randomUUID();
        cookies.put("JSESSIONID", uuid.toString());
    }

    public HttpCookie(String cookieString) {
        Arrays.stream(cookieString.split(";"))
                .map(item -> item.split("="))
                .forEach((item) -> {
                    if (item.length > 1) {
                        cookies.put(item[0], item[1]);
                    }
                });
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }
}
