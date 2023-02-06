package model;

public class GetMethodAdapter implements HttpMethodPort {
    private final HttpRequest httpRequest;

    public GetMethodAdapter(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void test() {

    }

    public HttpResponse getResponse() {
        return null;
    }
}
