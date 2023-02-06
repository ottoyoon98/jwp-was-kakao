package model;

public class PostMethodAdapter implements HttpMethodPort, RequestBodyMixin {
    private final HttpRequest httpRequest;

    public PostMethodAdapter(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpResponse getResponse() {
        return null;
    }
}
