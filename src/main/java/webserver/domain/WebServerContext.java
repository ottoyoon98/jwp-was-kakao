package webserver.domain;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static webserver.domain.HttpMethod.*;

@NoArgsConstructor
public class WebServerContext implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(WebServerContext.class);
    private static final int DEFAULT_PORT = 8080;
    private final RoutingHandler routingHandler = new RoutingHandler();

    private int port = DEFAULT_PORT;

    public WebServerContext(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                Thread thread = new Thread(new RequestHandler(connection, routingHandler));
                thread.start();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void close() {
        logger.info("Web Application Server closed.");
    }

    public void GET(String relativePath, Consumer<Context> handler) {
        routingHandler.addHandler(GET, relativePath, handler);
    }

    public void POST(String relativePath, Consumer<Context> handler) {
        routingHandler.addHandler(POST, relativePath, handler);
    }

    public void PUT(String relativePath, Consumer<Context> handler) {
        routingHandler.addHandler(PUT, relativePath, handler);
    }

    public void DELETE(String relativePath, Consumer<Context> handler) {
        routingHandler.addHandler(DELETE, relativePath, handler);
    }

    public void PATCH(String uri, Consumer<Context> handler) {
        routingHandler.addHandler(PATCH, uri, handler);
    }

    public static class RoutingHandler {
        private static final Map<HttpEndpoint, Consumer<Context>> handlerMapper = new ConcurrentHashMap<>();

        public void addHandler(HttpMethod method, String relativePath, Consumer<Context> handler) {
            handlerMapper.put(new HttpEndpoint(method, relativePath), handler);
        }

        public boolean canHandle(HttpMethod method, String relativePath) {
            return handlerMapper.containsKey(new HttpEndpoint(method, relativePath));
        }

        public Consumer<Context> getHandler(HttpMethod method, String relativePath) {
            return handlerMapper.get(new HttpEndpoint(method, relativePath));
        }
    }
}
