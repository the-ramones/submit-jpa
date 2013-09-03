package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Login controller
 * 
 * @author paul
 */
@Controller
public class LoginController {
    
    /*
     * 'login', 'login?login=true', 'login/lohout=true'
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(Model model) {
        model.addAttribute("view", "login");
        return "login";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginHome(Model model) {
        return "home";
    }
}
