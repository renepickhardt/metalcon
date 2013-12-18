package de.metalcon.middleware.controller.nav;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.metalcon.middleware.controller.MetalconController;

@Controller
public class HomeController extends MetalconController {

    @RequestMapping({
        "", "/"
    })
    public String handleHome() {
        return "nav/home";
    }

}
