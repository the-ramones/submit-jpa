package sp.controller;

import java.security.Principal;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @ModelAttribute("page_key")
    public String referenceData() {
        return "title.login";
    }

    /**
     * Renders login form
     *
     * @param isLogin is login request
     * @param isLogout is logout request
     * @param isInfoRequest is user info request
     * @param auth Authentication instance
     * @param principal principal instance
     * @param model model object
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(@RequestParam(value = "login", required = false) boolean isLogin,
            @RequestParam(value = "logout", required = false) boolean isLogout,
            @RequestParam(value = "info", required = false) boolean isInfoRequest,
            Authentication auth, Principal principal, Model model) {
        if (isLogin) {
            model.addAttribute("view", "login");
            return "login";
        } else if (isLogout) {
            model.addAttribute("view", "logout");
            return "logout";
        }
        if (isInfoRequest) {
            model.addAttribute("info", true);
        }
        AuthenticationTrustResolver anonymousResolver =
                new AuthenticationTrustResolverImpl();
        if (anonymousResolver.isAnonymous(auth)) {
            model.addAttribute("anonymous", true);
        }
        /*
         * Default to render 'User Info' page
         */
        model.addAttribute("view", "logout");
        return "logout";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginHome(Model model) {
        // TODO: custom login handler
        return "home";
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public String logoutPage(HttpSession session, Model model) {
        try {
            session.invalidate();
        } catch (IllegalStateException ex) {
            logger.warn("User session has been already invalidated", ex);
        } finally {
            model.addAttribute("login", true);
            return "login";
        }
    }
}
