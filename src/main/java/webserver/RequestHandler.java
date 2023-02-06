package webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.DataBase;
import model.FileExtensions;
import model.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

            HttpRequest httpRequest = new HttpRequest()
                    .setStatusLine(reader.readLine());

            while (true) {
                String line = reader.readLine();
                if (line == null || line.isEmpty()) break;
                httpRequest.addHeader(line);
            }

            URI requestURI = URI.create(httpRequest.getURI());
            String path = requestURI.getPath();
            byte[] body = "Hello world".getBytes();

            DataOutputStream dos = new DataOutputStream(out);

            Optional<String> ext = getExtension(path);
            if (ext.isEmpty()) {
                String queryParams = requestURI.getQuery();
                String[] parsedParams = queryParams.split("&");
                Map<String, String> mappedParams = Arrays.stream(parsedParams)
                        .map(item -> item.split("="))
                        .collect(Collectors.toMap(k -> k[0], v -> v[1]));

                ObjectMapper objectMapper = new ObjectMapper();
                User user = objectMapper.convertValue(mappedParams, User.class);

                DataBase.addUser(user);
            } else {
                FileExtensions requestedFileExtension = FileExtensions.of(ext.get());
                try {
                    body = FileIoUtils.loadFileFromClasspath((requestedFileExtension.getDirectory()) + path);
                    response200Header(dos, requestedFileExtension.getContentType(), body.length);
                } catch (IOException e) {

                } catch (URISyntaxException e) {

                }
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
        return parsedFileName.length <= 1 ? Optional.empty() : Optional.of(parsedFileName[parsedFileName.length - 1]);
    }
}
