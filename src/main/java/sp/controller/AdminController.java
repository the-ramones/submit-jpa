package sp.controller;

import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sp.system.SystemConstants;
import sp.util.service.PropertyService;
import sp.validation.SystemPropertyValidator;

/**
 * Admin web-interface controller
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Inject
    PropertyService propertyService;
    @Inject
    @Named("messageSource")
    MessageSource messageSource;

    @ModelAttribute("page_key")
    public String referenceData() {
        return "title.admin";
    }
    
    @RequestMapping(value = {"/", "/manager", "/admin"}, method = RequestMethod.GET)
    public String manager(Model model) {
        /*
         * Fill in model
         */
        model.addAttribute("promptLimit",
                propertyService.getFrontend(SystemConstants.PROMPT_LIMIT));
        model.addAttribute("pageSize",
                propertyService.getFrontend(SystemConstants.PAGE_SIZE));
        model.addAttribute("maxOnPager",
                propertyService.getFrontend(SystemConstants.MAX_ON_PAGER));
        model.addAttribute("solrHost",
                propertyService.getBackend(SystemConstants.SOLR_HOST));
        model.addAttribute("solrCore",
                propertyService.getBackend(SystemConstants.SOLR_CORE));
        model.addAttribute("dburl",
                propertyService.getBackend(SystemConstants.DATABASE_URL));
        model.addAttribute("schema",
                propertyService.getBackend(SystemConstants.DATABASE_SCHEME));
        model.addAttribute("solrTimeout",
                propertyService.getBackend(SystemConstants.SOLR_TIMEOUT));
        model.addAttribute("solrMaxConnections",
                propertyService.getBackend(SystemConstants.SOLR_MAXCONNECTIONS));
        return "admin";
    }

    @RequestMapping(value = "update", method = {RequestMethod.GET, RequestMethod.POST}, produces = "text/plain;charset=utf-8")
    public @ResponseBody
    String updateSystemProperties(@RequestParam(value = "type") String type,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "value", required = false) String value,
            Locale locale, Model model) {
        String realKey;
        String result = "success";
        boolean isUrl = false;
        if (key != null && value != null
                && key.compareTo("") != 0 && value.compareTo("") != 0) {
            /*
             * Check for existence of a property
             */
            if (key.equals("promptLimit")) {
                realKey = SystemConstants.PROMPT_LIMIT;
            } else if (key.equals("pageSize")) {
                realKey = SystemConstants.PAGE_SIZE;
            } else if (key.equals("maxOnPager")) {
                realKey = SystemConstants.MAX_ON_PAGER;
            } else if (key.equals("solrHost")) {
                realKey = SystemConstants.SOLR_HOST;
                isUrl = true;
            } else if (key.equals("solrCore")) {
                realKey = SystemConstants.SOLR_CORE;
            } else if (key.equals("dburl")) {
                realKey = SystemConstants.DATABASE_URL;
                isUrl = true;
            } else if (key.equals("schema")) {
                realKey = SystemConstants.DATABASE_SCHEME;
            } else if (key.equals("solrTimeout")) {
                realKey = SystemConstants.SOLR_TIMEOUT;
            } else if (key.equals("solrMaxConnections")) {
                realKey = SystemConstants.SOLR_MAXCONNECTIONS;
            } else {
                return "error";
            }
            SystemPropertyValidator propertyValidator = new SystemPropertyValidator();
            if (propertyValidator.isValid(key, value)) {
                if (type.equals("front")) {
                    propertyService.updateFrontend(key, value);
                } else if (type.equals("back")) {
                    propertyService.updateBackend(key, value);
                } else {
                    result = "error";
                }
            } else {
                if (isUrl) {
                    result = messageSource.getMessage("admin.invalidurl", null, locale);
                } else {
                    result = messageSource.getMessage("admin.invalidnumber", null, locale);
                }
            }
        }
        return result;
    }
}
