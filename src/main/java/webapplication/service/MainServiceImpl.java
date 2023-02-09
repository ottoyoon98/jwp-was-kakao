package webapplication.service;

import db.DataBase;
import webapplication.domain.Login;
import webapplication.domain.User;

public class MainServiceImpl implements MainService {
    public void createUser(User user) {
        DataBase.addUser(user);
    }

    public boolean isCheckUserByIdAndPassword(Login login) {
        User user = DataBase.findUserById(login.getUserId());
        return user.getPassword()
                .equals(login.getPassword());
    }
}
