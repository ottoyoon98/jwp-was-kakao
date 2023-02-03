package webserver;

import model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String TEMPLATES_DIR = "./templates";

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

            // TODO:
            //  - "/test.png"도 파일임, "/a/test.jpg"도 파일임, "/a/b/test.jpg?params1=1234&params2=2345"도 파일임
            //  - URL과 일치하는 파일 읽기
            //  - 해당 파일을 byte로 변환하기
            URI requestURI = URI.create(response.getURI());
            String path = requestURI.getPath();
            byte[] body = "Hello world".getBytes();
            try {
                body = FileIoUtils.loadFileFromClasspath(TEMPLATES_DIR + path);
            } catch (IOException e) {

            } catch (URISyntaxException e) {

            }

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (
                IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");
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
}
