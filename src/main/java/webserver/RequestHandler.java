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
            HttpRequestReader requestReader = new HttpRequestReader(httpRequest);

            byte[] body = "Hello world".getBytes();

            DataOutputStream dos = new DataOutputStream(out);
            HttpResponse response = HttpResponse.builder(HttpStatus.OK)
                    .contentLength(body.length)
                    .body(body)
                    .build();

            if (requestReader.isFile()) {
                FileExtensions requestedFileExtension = FileExtensions.of(requestReader.getExtension());
                try {
                    body = FileIoUtils.loadFileFromClasspath(requestedFileExtension.getDirectory() + requestReader.getPath());
                    response = HttpResponse.builder(HttpStatus.OK)
                            .contentType(requestedFileExtension.getContentType())
                            .contentLength(body.length)
                            .body(body)
                            .build();
                } catch (IOException | URISyntaxException e) {
                    logger.error(e.getMessage());
                    logger.error(Arrays.toString(e.getStackTrace()));
                }
            } else if (routingHandler.canHandle(httpRequest.getMethod(), httpRequest.getPath())) {
                Context context = new Context(requestReader);
                routingHandler.getHandler(httpRequest.getMethod(), httpRequest.getPath())
                        .accept(context);
                response = context.getHttpResponse();
            }

            HttpResponseWriter writer = new HttpResponseWriter(response);
            dos.write(writer.writeAsByte());

        } catch (IOException | NullPointerException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }
}
