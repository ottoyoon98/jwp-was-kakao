package webserver.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.StringJoiner;

public class HttpResponseWriter {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private final StringJoiner stringJoiner = new StringJoiner("\r\n");

    private final HttpResponse httpResponse;

    private final byte[] response;

    public HttpResponseWriter(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
        this.response = concat(write(), this.httpResponse.getBody());
    }

    private byte[] concat(byte[] a, byte[] b) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            if (a != null) {
                outputStream.write(a);
            }
            if (b != null) {
                outputStream.write(b);
            }
            outputStream.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return outputStream.toByteArray();
    }

    private byte[] write() {
        stringJoiner
                .add("HTTP/1.1 " + this.httpResponse.getStatus().getValue() + " " + this.httpResponse.getStatus().getReasonPhrase())
                .add("Content-Length: " + this.httpResponse.getContentLength())
        ;
        if (this.httpResponse.getContentType() != null) {
            stringJoiner.add("Content-Type: " + this.httpResponse.getContentType());
        }
        if (this.httpResponse.getLocation() != null) {
            stringJoiner.add("Location: " + this.httpResponse.getLocation());
        }
        this.httpResponse.getCustomHeaders().forEach((k, v) -> stringJoiner.add(k + ": " + v));
        stringJoiner.add("").add("");

        String responseHeaders = stringJoiner.toString();

        return responseHeaders.getBytes();
    }

    public byte[] writeAsByte() {
        return response;
    }
}
