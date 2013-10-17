package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Admin web-interface controller
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    
    @RequestMapping(value = {"/", "/manager", "/admin"}, method = RequestMethod.GET)
    public String manager(Model model) {
        
        return "admin";
    }
    
    @RequestMapping(value = "/dwr-add", method = RequestMethod.GET)
    public String dwrAdd(Model model) {
        
        return "dwr-add";
    }
}
