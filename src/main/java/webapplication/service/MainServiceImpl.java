package webapplication.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import db.DataBase;
import webapplication.domain.Login;
import webapplication.domain.User;

import java.util.Collection;

public class MainServiceImpl implements MainService {
    public void createUser(User user) {
        DataBase.addUser(user);
    }

    public boolean isCheckUserByIdAndPassword(Login login) {
        User user = DataBase.findUserById(login.getUserId());
        return user.getPassword()
                .equals(login.getPassword());
    }

    public String getUserList() throws Exception {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");
        loader.setSuffix(".html");
        Handlebars handlebars = new Handlebars(loader);

        Template template = handlebars.compile("user/list");
        Collection<User> userList = DataBase.findAll();
        for (User user : userList) {
            System.out.println("");
        }
        return template.apply(userList);
        
    }
}
