package sp.controller;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sp.model.Report;
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

    public ReportController() {
    }
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // TODO: fix select maximum height, css for form
    @RequestMapping(value = {"", "search"}, method = RequestMethod.GET)
    public String setupForm(Model model) {
        model.addAttribute("view", "search");
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

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String setupDetailForm(Model model) {
        model.addAttribute("view", "byid");        
        return "byid";
    }
            
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detailById(Model model, @PathVariable("id") Long id) {
        model.addAttribute("report", reportService.getReportById(id));
        return "detail";
    }

    /**
     * Setup of 'add activity' form
     * 
     * @param model
     * @return view name
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String setupAddForm(Model model) {
        model.addAttribute("view", "add");
        model.addAttribute("report", new Report());
        return "add";
    }
    /**
     * 
     * @param report
     * @param result
     * @param model
     * @return view name
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(@Valid @ModelAttribute("report") Report report, 
        BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("view", "add");
            return "add";
        }
        report = reportService.addReport(report);
        System.out.println(report);
        return "redirect:detail/" + report.getId();
    }

    @RequestMapping(value = "/ajax", method = RequestMethod.GET)
    public String realTimeSearch(Model model) {
        model.addAttribute("view", "ajax");
        return "search";
    } 
}
