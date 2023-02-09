package webapplication.service;

import webapplication.domain.Login;
import webapplication.domain.User;

public interface MainService {
    void createUser(User user);

    boolean isCheckUserByIdAndPassword(Login login);

}
