package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Registry Controller
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("/registry")
public class RegistryController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showRegistry(Model model) {
        // TODO
        return "registry";
    }
}
