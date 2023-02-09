package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.domain.*;
import webserver.utils.FileIoUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Arrays;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final WebServerContext.RoutingHandler routingHandler;

    public RequestHandler(Socket connectionSocket, WebServerContext.RoutingHandler routingHandler) {
        this.connection = connectionSocket;
        this.routingHandler = routingHandler;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequest.from(in);
            HttpResponse httpResponse = generateResponse(httpRequest);
            writeResponse(out, httpResponse);
        } catch (IOException | NullPointerException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    private HttpResponse generateResponse(HttpRequest httpRequest) {
        HttpRequestReader requestReader = new HttpRequestReader(httpRequest);
        byte[] body = "Hello world".getBytes();
        HttpResponse response = HttpResponse.builder(HttpStatus.OK)
                .contentLength(body.length)
                .body(body)
                .build();

        if (requestReader.isFile()) {
            response = getHttpResponseByFile(requestReader);
        } else if (routingHandler.canHandle(httpRequest.getMethod(), httpRequest.getPath())) {
            response = getHttpResponseByHandler(requestReader, httpRequest);
        }
        return response;
    }

    private HttpResponse getHttpResponseByFile(HttpRequestReader requestReader) {
        FileExtensions requestedFileExtension = FileExtensions.of(requestReader.getExtension());
        byte[] body;
        HttpResponse response;
        try {
            body = FileIoUtils.loadFileFromClasspath(requestedFileExtension.getDirectory() + requestReader.getPath());
            response = HttpResponse.builder(HttpStatus.OK)
                    .contentType(requestedFileExtension.getContentType())
                    .contentLength(body.length)
                    .cookie(requestReader.getHttpCookie()
                            .getCookie("JSESSIONID"))
                    .body(body)
                    .build();
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            response = HttpResponse.builder(HttpStatus.NOT_FOUND)
                    .build();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            response = HttpResponse.builder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
        return response;
    }


    private HttpResponse getHttpResponseByHandler(HttpRequestReader requestReader, HttpRequest httpRequest) {
        Context context = new Context(requestReader);
        routingHandler.getHandler(httpRequest.getMethod(), httpRequest.getPath())
                .accept(context);
        return context.getHttpResponse();
    }

    private void writeResponse(OutputStream out, HttpResponse response) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        HttpResponseWriter writer = new HttpResponseWriter(response);
        dos.write(writer.writeAsByte());
    }

}
