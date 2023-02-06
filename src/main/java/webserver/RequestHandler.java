package webserver;

import db.DataBase;
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

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequest.from(in);

            PathResource pathResource = new PathResource(httpRequest.getURI());
            byte[] body = "Hello world".getBytes();

            // TODO
            //  URL Mapping을 통해 원하는 메소드 호출할 수 있게 만들기
            DataOutputStream dos = new DataOutputStream(out);
            if (pathResource.hasParams()) {
                User user = pathResource.convertParams(User.class);
                DataBase.addUser(user);
                logger.info("New User Signed Up! " + user);
            } else if (pathResource.isFile()) {
                FileExtensions requestedFileExtension = FileExtensions.of(pathResource.getExtension());
                try {
                    body = FileIoUtils.loadFileFromClasspath(requestedFileExtension.getDirectory() + pathResource.getRawPath());

                    HttpResponse response = HttpResponse.builder(HttpStatus.OK)
                            .contentType(requestedFileExtension.getContentType())
                            .contentLength(body.length)
                            .body(body)
                            .build();
                    dos.write(response.getAsByte());
                } catch (IOException | URISyntaxException e) {
                    logger.error(e.getMessage());
                }
            } else {
                HttpResponse response = HttpResponse.builder(HttpStatus.OK)
                        .contentLength(body.length)
                        .body(body)
                        .build();
                dos.write(response.getAsByte());
            }

        } catch (IOException | NullPointerException e) {
            logger.error(e.getMessage());
        }
    }
}
