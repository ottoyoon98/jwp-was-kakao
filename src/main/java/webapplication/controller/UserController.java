package webapplication.controller;

import webapplication.domain.Login;
import webapplication.domain.User;
import webapplication.service.MainService;
import webapplication.service.MainServiceImpl;
import webserver.domain.Context;
import webserver.domain.HttpResponse;
import webserver.domain.HttpStatus;

import java.util.UUID;

public class UserController {

    public final MainService mainService;

    private UserController(MainServiceImpl mainService) {
        this.mainService = mainService;
    }

    public static UserController getInstance() {
        return UserControllerHolder.INSTANCE;
    }

    public void createUserByPost(Context c) {
        User user = c.bindBody(User.class);
        mainService.createUser(user);
        c.setHttpResponse(HttpResponse.builder(HttpStatus.FOUND)
                .location("/index.html")
                .build()
        );
    }

    public void createUserByGet(Context c) {
        User user = c.bindQuery(User.class);
        mainService.createUser(user);
        c.setHttpResponse(HttpResponse.builder(HttpStatus.FOUND)
                .location("/index.html")
                .build()
        );
    }

    public void authUserByPost(Context c) {
        Login login = c.bindBody(Login.class);
        if (mainService.isCheckUserByIdAndPassword(login)) {
            // 로그인 성공
            c.setHttpResponse(HttpResponse.builder(HttpStatus.FOUND)
                    .location("/index.html")
                    .cookie(UUID.randomUUID()
                            .toString())
                    .build());
        } else {
            // 로그인 실패
            c.setHttpResponse(HttpResponse.builder(HttpStatus.FOUND)
                    .location("/user/login_failed.html")
                    .build());
        }


    }

    private static class UserControllerHolder {
        private static final UserController INSTANCE = new UserController(new MainServiceImpl());
    }
}
