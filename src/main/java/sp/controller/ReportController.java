package sp.controller;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import sp.model.Report;
import sp.service.ReportService;
import sp.util.SpHasher;
import sp.util.SpSortDefinition;
import sp.validation.ReportDate;

/**
 * Report controller
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("/report")
@SessionAttributes(value = {"pager"})
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    /*
     * Spring 3+ way
     */
    @Value("${pagination.threshold}")
    private int PAGINATION_THRESHOLD;
    @Inject
    private ReportService reportService;

    @PostConstruct
    public void postConstruct() {
    }

    @PreDestroy
    public void preDestroy() {
    }

    @InitBinder
    public void initReportDateEditor(WebDataBinder binder, Locale locale) {
        DateFormat df;
        df = sp.util.SpDateFormatFactory.getDateFormat("dd MMM yyyy", locale, null);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }

    @ModelAttribute("pager")
    public PagedListHolder<Report> populatePager() {
        PagedListHolder<Report> pager = new PagedListHolder<Report>();
        pager.setSort(new SpSortDefinition());
        pager.setSource(new ArrayList());
        return pager;
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
        model.addAttribute("startDate", new Date());
        model.addAttribute("endDate", new Date());
        model.addAttribute("performer", new String());
        return "form";
    }

    // TODO: fix pagination, ix validation
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public String doSearch(
            @Valid @ModelAttribute("startDate") @DateTimeFormat(pattern = "dd MMM yyyy")
            @RequestParam(value = "startDate") Date startDate,
            @ModelAttribute("endDate") @DateTimeFormat(pattern = "dd MMM yyyy")
            @RequestParam(value = "endDate") Date endDate,
            @ModelAttribute("performer") @Pattern(regexp = "^(?iu)[a-zа-я][ 0-9a-zа-я-#@%&\\$]{1,255}(?<!-)$")
            @RequestParam(value = "performer", defaultValue = "") String performer,
            @RequestParam(value = "page", required = false) String page,
            @ModelAttribute("pager") PagedListHolder<Report> pager,
            Model model) {
        logger.info("pagination.threshold: {}", PAGINATION_THRESHOLD);
        //        logger.info("startDate: {} of type {}", startDate, startDate.getClass().getConstructors());
        //        logger.info("endDate: {} of type {}", endDate, endDate.getClass().getConstructors());
        //        logger.info("performer: {} of type {}", performer, performer.getClass().getConstructors());           
        List<Report> reports;
        //PagedListHolder pager;
        logger.error("Pager characteristics: pager={}, pageCount={}, pageSize={}",
                pager.toString(), pager.getPageCount(), pager.getPageSize());
        if (pager.getPageList().isEmpty()) {
            logger.debug("Retrieving reports from service layer");
            if (performer.isEmpty()) {
                reports = reportService.getReports(startDate, endDate);
            } else {
                reports = reportService.getReports(performer, startDate, endDate);
            }
            logger.info("Reports cardinality: {}", reports.size());
            model.addAttribute("search_id", SpHasher.getHash(new Object[]{startDate, endDate}));
            long size = reports.size();
            //if (size > PAGINATION_THRESHOLD) {
            pager = new PagedListHolder(reports, new sp.util.SpSortDefinition());
            pager.setPageSize(PAGINATION_THRESHOLD);
            pager.setPage(0);
            for (Report report: pager.getPageList()) {
                logger.info("report: {}", report);
            }
            model.addAttribute("pager", pager);
            //}
        } else {
            logger.debug("Using of PagedListHolder from current session");
            logger.info("Current pagination page: {}", page);
            /*
             * Page must be: 'next', 'prev', 'page-number'
             */
            if (page != null) {
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
                        pager.setPage(Integer.valueOf(page) - 1);
                    } catch (NumberFormatException ex) {
                        logger.warn("Cannot parse page number from request parameter 'page'={}", page);
                    }
                }
            } else {
                logger.debug("Setting pager page number 1");
                pager.setPage(0);
            }
            logger.info("Pagination page: {}", pager.getPage());
            //model.addAttribute("pager", pager);
        }
        logger.error("Pager after characteristics: pager={}, pageCount={}, pageSize={}",
                pager.toString(), pager.getPageCount(), pager.getPageSize());
        
//        model.addAttribute("reports", reports);
        return "list-paged";
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String setupDetailForm(Model model) {
        model.addAttribute("view", "byid");
        return "byid";
    }

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public String setupDetailForm(@RequestParam Long id, Model model) {
        if (reportService.hasReport(id)) {
            return "redirect:detail/" + id;
        } else {
            model.addAttribute("view", "byid");
            model.addAttribute("id", id);
            model.addAttribute("error", "byid.id.wrong");
            return "byid";
        }
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detailById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        model.addAttribute("report", reportService.getReportById(id));
        model.addAttribute("uri", request.getRequestURL());
        return "detail";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String setupAddForm(Model model, HttpServletResponse res) {
        model.addAttribute("view", "add");
        model.addAttribute("report", new Report());
        return "add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ModelAndView add(@Valid @ModelAttribute("report") Report report,
            BindingResult result, Model model, HttpServletRequest req, HttpServletResponse res) {
        ModelAndView mav = new ModelAndView();
        if (result.hasErrors()) {
            System.out.println("report: " + report);
            System.out.println("model: " + model);
            model.addAttribute("view", "add");
            mav.addAllObjects(model.asMap());
            mav.setViewName("add");
            return mav;
        }
        report = reportService.addReport(report);
        String protocol = "http://";
        if (req.getRequestURL().indexOf("https") != -1) {
            protocol = "https://";
        }
        model.addAttribute("uri", protocol + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/report/detail/" + report.getId());
        model.addAttribute("back", req.getRequestURL());
        model.addAttribute("report", report);
        System.out.println(model);
        mav.addAllObjects(model.asMap());
        mav.setViewName("detail");
        System.out.println("Context:" + req.getContextPath());
        System.out.println("URI:" + req.getRequestURI());
        System.out.println("URL:" + req.getRequestURL());
        System.out.println("User:" + req.getRemoteUser());
        System.out.println("Session id :" + req.getRequestedSessionId());
        return mav;
    }

    @RequestMapping(value = "/ajax", method = RequestMethod.GET)
    public String realTimeSearch(Model model) {
        model.addAttribute("view", "ajax");
        return "search";
    }
}
