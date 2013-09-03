package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home page controller
 *
 * @author the-ramones
 */
@Controller
public class HomeController {

    @RequestMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("view", "home");                
        return "home";
    }

    @RequestMapping("/contact")
    public String contact(Model model) {
        return "contact";
    }
    
    @RequestMapping("/instruction")
    public String instruction(Model model) {
        return "instruction";
    }
    
    @RequestMapping("/about")
    public String about(Model model) {
        return "about";
    }
}
