package webserver.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.StringJoiner;

public class HttpResponseWriter {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    private static final String CONTENT_TYPE_HEADER = "Content-type: ";
    private static final String LOCATION_HEADER = "Location: ";
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
                .add(HTTP_VERSION + this.httpResponse.getStatus()
                        .getValue() + " " + this.httpResponse.getStatus()
                        .getReasonPhrase())
                .add(CONTENT_LENGTH_HEADER + this.httpResponse.getContentLength())
        ;
        if (this.httpResponse.getContentType() != null) {
            stringJoiner.add(CONTENT_TYPE_HEADER + this.httpResponse.getContentType());
        }
        if (this.httpResponse.getLocation() != null) {
            stringJoiner.add(LOCATION_HEADER + this.httpResponse.getLocation());
        }
        this.httpResponse.getCustomHeaders()
                .forEach((k, v) -> stringJoiner.add(k + ": " + v));
        stringJoiner.add("")
                .add("");

        String responseHeaders = stringJoiner.toString();

        return responseHeaders.getBytes();
    }

    public byte[] writeAsByte() {
        return response;
    }
}
