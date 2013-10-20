package sp.controller;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Login controller
 *
 * @author Paul Kulitski
 */
@Controller
public class LoginController {

    protected static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /*
     * 'login', 'login?login=true', 'login/lohout=true'
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(@RequestParam(value = "login", required = false) boolean isLogin,
            @RequestParam(value = "logout", required = false) boolean isLogout,
            Model model) {
        if (isLogin) {
            model.addAttribute("view", "login");
            return "login";
        } else if (isLogout) {
            model.addAttribute("view", "logout");
            return "logout";
        }
        /*
         * Default to render 'User Info' page
         */
        model.addAttribute("view", "logout");
        return "logout";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginHome(Model model) {
        // TODO:
        return "home";
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public String logoutPage(HttpSession session, Model model) {
        try {
            session.invalidate();
        } catch (IllegalStateException ex) {
            logger.warn("User session has been already invalidated", ex);
        } finally {
            model.addAttribute("login", "true");
            return "login";
        }
    }
}
