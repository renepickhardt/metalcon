package de.metalcon.middleware.controller.test;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.metalcon.middleware.util.User;

@Controller
@RequestMapping(value = "/test/session", method = RequestMethod.GET)
public class SessionController {
    
    @Autowired
    private BeanFactory beanFactory;

    @RequestMapping(value = "", produces = "text/plain")
    public @ResponseBody String handle() {
        User user = beanFactory.getBean(User.class);
        return user.getDate();
    }
    
}
