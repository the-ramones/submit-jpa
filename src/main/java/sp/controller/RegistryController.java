package sp.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import sp.model.Register;
import sp.service.RegistryService;
import sp.util.SpDateFormatFactory;
import sp.util.SpLazyPager;
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

    @Value("${pagination.maxonpager}")
    private int MAX_ON_PAGER;
    @Value("${pagination.threshold}")
    private int PAGINATION_THRESHOLD;
    @Inject
    RegistryService registryService;
    private static final Logger logger = LoggerFactory.getLogger(RegistryController.class);

    @ModelAttribute("settings")
    public Map<String, String> populateReferenceData() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("maxOnPager", String.valueOf(MAX_ON_PAGER));
        settings.put("pagerThreshold", String.valueOf(PAGINATION_THRESHOLD));
        return settings;
    }

    @ModelAttribute("registry-pager")
    public SpLazyPager populateREgisterPager() {
        return new SpLazyPager(0);
    }

    @InitBinder
    public void initReportDateEditor(WebDataBinder binder, Locale locale) {
        DateFormat df = SpDateFormatFactory.getDateFormat(locale);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showRegistry(@RequestParam(value = "p", required = false) String page,
            @RequestParam(value = "new", required = false) String newSearch,
            @ModelAttribute("registry-pager") SpLazyPager pager,
            Model model) {
        logger.error("IN SHOW REGISTRY");

        boolean reject = false;
        if (pager.getSourceCount() == 0 || newSearch != null) {
            List<Register> registers = registryService.getRegisters(0, PAGINATION_THRESHOLD);
            if (!registers.isEmpty()) {
                pager.setPageSize(PAGINATION_THRESHOLD);
                pager.setSourceCount(registers.size());
                pager.setPage(0);
                model.addAttribute("registers", registers);
                return "registry";
            } else {
                return "redirect:search/nothing";
            }
        } else {
            if (pager != null) {
                if (page != null) {
                    try {
                        pager.setPage(page);
                    } catch (NumberFormatException ex) {
                        logger.error("Wrong page string. Redirecting to the fallback page");
                        reject = true;                        
                    }
                } else {
                    pager.setPage(0);
                }
            }
        }
        if (reject) {
            return "redirect:registry/nothing";
        }
        return "registry";
    }
    
    @RequestMapping(value = "nothing", method = RequestMethod.GET)
    public String nothingFound(Model model) {
        model.addAttribute("backpath", "/registry?new");
        return "nothing";
    }
    
}
