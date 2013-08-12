package sp.controller;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sp.service.ReportService;

/**
 * Report CRUD operations controller
 *
 * @author the-ramones
 */
@Controller
@RequestMapping("/report")
public class ReportController {

    @Inject
    private ReportService reportService;

    @PostConstruct
    public void postConstruct() {
    }

    @PreDestroy
    public void preDestroy() {
    }

    // TODO: fix select maximum height, css for form
    @RequestMapping(value = {"", "search"}, method = RequestMethod.GET)
    public String setupForm(Model model) {
        model.addAttribute("performers", reportService.getPerformers());
        return "form";
    }

    // TODO: fix pagination
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public String doSearch(Model model,
            @ModelAttribute("performer")
            @RequestParam(value = "performer", defaultValue = "") String performer,
            @ModelAttribute("startDate")
            @RequestParam(value = "startDate") Date startDate,
            @ModelAttribute("endDate")
            @RequestParam(value = "endDate") Date endDate) {
        model.addAttribute("reports", reportService.getReports(performer, startDate, endDate));
        return "list";
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(Model model, @PathVariable("id") Long id) {
        model.addAttribute("report", reportService.getReportById(id));
        return "detail";
    }

    @RequestMapping(value = "clear", method = RequestMethod.GET)
    public String clear(Model model) {
        //TODO: redirect to setupForm @RequestMapping
        return "form";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {
        return "add";
    }

    @RequestMapping(value = "/realtime", method = RequestMethod.GET)
    public String realTimeSearch(Model model) {
        return "search";
    }    
}
