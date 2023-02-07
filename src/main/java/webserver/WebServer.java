package webserver;

import webapplication.controller.UserController;
import webserver.domain.WebServerContext;

public class WebServer {

    public static void main(String[] args) {
        try (WebServerContext webServerContext = new WebServerContext()) {
            webServerContext.GET("/user/create", UserController.getInstance()::createUserByGet);
            webServerContext.POST("/user/create", UserController.getInstance()::createUserByPost);

            webServerContext.start();
        }
    }
}
