package sp.controller;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import sp.model.Register;
import sp.service.RegistryService;
import sp.util.SpSortDefinition;

/**
 * Registry Controller
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("/registry")
@SessionAttributes("registry-pager")
public class RegistryController {

    @Inject
    RegistryService registryService;
    private static final Logger logger = LoggerFactory.getLogger(RegistryController.class);

    @ModelAttribute("registry-pager")
    public PagedListHolder<Register> populateREgisterPager() {
        return new PagedListHolder<Register>(
                new ArrayList<Register>(), new SpSortDefinition());
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showRegistry(@RequestParam("p") String page,
            @ModelAttribute("registry-pager") PagedListHolder<Register> pager,
            Model model) {
        boolean reject = false;
        if (page == null) {
            List<Register> registry = registryService.getAll();
            /*
             * Peplace old register-pager with the new
             */
            pager = new PagedListHolder<Register>(registry, new SpSortDefinition());
        } else {
            /*
             * Page must be: 'next', 'prev', 'page-number'
             */
            logger.debug("Looking up for the requested page: {}", page);
            if (page.equalsIgnoreCase("next")) {
                pager.nextPage();
            } else if (page.equalsIgnoreCase("prev")) {
                pager.previousPage();
            } else if (page.equalsIgnoreCase("first")) {
                pager.setPage(0);
            } else if (page.equalsIgnoreCase("last")) {
                pager.setPage(pager.getPageCount() - 1);
            } else {
                try {
                    Integer intPage = Integer.valueOf(page);
                    //TODO: fix validation                                        
                    if ((intPage > 0) && (intPage < pager.getPageCount())) {
                        pager.setPage(intPage - 1);
                    }
                } catch (NumberFormatException ex) {
                    logger.warn("Cannot parse page number from request parameter 'page'={}", page);
                    reject = true;
                }
            }
            logger.debug("Setting pager page {}", page);
       

            return "registry";
        }
        return null;
    }
}
