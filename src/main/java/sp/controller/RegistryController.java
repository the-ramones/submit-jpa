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
    protected static final Logger logger = LoggerFactory.getLogger(RegistryController.class);

    @ModelAttribute("page_key")
    public String referenceData() {
        return "title.registry";
    }

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
        logger.debug("Showing application registry");

        List<Register> registers = null;
        if (pager.getSourceCount() == 0 || newSearch != null) {
            registers = registryService.getRegisters(0, PAGINATION_THRESHOLD);
            if (!registers.isEmpty()) {
                pager.setPageSize(PAGINATION_THRESHOLD);
                pager.setSourceCount(registryService.count().intValue());
                pager.setPage(0);
                model.addAttribute("registers", registers);

                return "registry";
            } else {
                return "redirect:registry/nothing";
            }
        } else {
            if (page != null) {
                boolean correct = pager.setPage(page);
                if (!correct) {
                    return "redirect:registry/nothing";
                }
            } else {
                pager.setPage(0);
            }
            registers = registryService
                    .getRegisters(pager.getPageOffset(), pager.getPageSize());
            model.addAttribute("registers", registers);
        }

        return "registry";
    }

    @RequestMapping(value = "nothing", method = RequestMethod.GET)
    public String nothingFound(Model model) {
        model.addAttribute("backpath", "/registry?new");
        return "nothing";
    }
}
