package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/** 
 * Checklist controller
 * 
 * @author Paul Kulitski
 */
@Controller
public class ChecklistController {
    
    @RequestMapping(value="/checklist", method = RequestMethod.GET)
    public String checklist(Model model) {
        //TODO
        return "checklist";
    }
}
