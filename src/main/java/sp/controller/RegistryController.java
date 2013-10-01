package sp.controller;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sp.model.Register;
import sp.service.RegistryService;

/**
 * Registry Controller
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("/registry")
public class RegistryController {

    @Inject
    RegistryService registryService;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showRegistry(Model model) {
        List<Register> registry = registryService.getRegisterByPeriod(null, null);
        
        model.addAttribute("registry", registry);
        return "registry";
    }
}
