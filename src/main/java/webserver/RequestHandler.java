package webserver;

import model.FileExtensions;
import model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            Response response = new Response()
                    .setStatusLine(reader.readLine());

            while (true) {
                String line = reader.readLine();
                if (line == null || line.isEmpty()) break;
                response.addHeader(line);
            }
            System.out.println(response);

            URI requestURI = URI.create(response.getURI());
            String path = requestURI.getPath();
            byte[] body = "Hello world".getBytes();

            DataOutputStream dos = new DataOutputStream(out);
            try {
                Optional<String> ext = getExtension(path);
                FileExtensions requestedFileExtension = FileExtensions.of(ext.orElse(""));
                body = FileIoUtils.loadFileFromClasspath((requestedFileExtension.getDirectory()) + path);
                response200Header(dos, requestedFileExtension.getContentType(), body.length);
            } catch (IOException e) {

            } catch (URISyntaxException e) {

            }
            responseBody(dos, body);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, String accept, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + accept + " \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private Optional<String> getExtension(String path) {
        File file = new File(path);
        String fileName = file.getName();
        String[] parsedFileName = fileName.split("\\.");
        return parsedFileName.length < 1 ? Optional.empty() : Optional.of(parsedFileName[parsedFileName.length - 1]);
    }
}
