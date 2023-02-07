package webapplication.service;

import db.DataBase;
import webapplication.domain.User;

public class MainServiceImpl implements MainService {
    public void createUser(User user) {
        DataBase.addUser(user);
    }
}
