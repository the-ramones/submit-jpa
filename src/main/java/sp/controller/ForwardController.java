package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Forward controller
 *
 * @author Paul Kulitski
 */
@Controller
public class ForwardController {

    @RequestMapping("/forward")
    public String forward(Model model) {
        return "forward";
    }
}
