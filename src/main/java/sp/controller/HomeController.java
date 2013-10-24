package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home page controller
 *
 * @author Paul Kulitski
 */
@Controller
public class HomeController {

    @ModelAttribute("page_key")
    public String referenceData() {
        return "title.home";
    }

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
