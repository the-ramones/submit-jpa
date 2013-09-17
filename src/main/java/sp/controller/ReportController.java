package sp.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.support.PagedListHolder;
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

/**
 * Report controller for synchronous actions
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("/report")
@SessionAttributes(value = {"pagers"})
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    /*
     * Spring 3+ way. Previously, used @Value(#{systemProperties.pagination.threshold})
     */
    @Value("${pagination.maxonpager}")
    private int MAX_ON_PAGER;
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

    @ModelAttribute("settings")
    public Map<String, String> populateReferenceDate() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("maxOnPager", String.valueOf(MAX_ON_PAGER));
        settings.put("pagerThreshold", String.valueOf(PAGINATION_THRESHOLD));
        return settings;
    }

    @InitBinder
    public void initReportDateEditor(WebDataBinder binder, Locale locale) {
        DateFormat df;
        df = sp.util.SpDateFormatFactory.getDateFormat("dd MMM yyyy", locale, null);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }

//    @ModelAttribute("pagers")
//    public PagedListHolder<Report> populatePager() {
//        Map<String, PagedListHolder<Report>> pagers = 
//                new HashMap<String, PagedListHolder<Report>>(1);
////        PagedListHolder<Report> pager = new PagedListHolder<Report>();
////        pager.setSort(new SpSortDefinition());
////        pager.setSource(new ArrayList());
//        return pagers;
//    }
    @ModelAttribute("pagers")
    public Map<String, PagedListHolder<Report>> populatePager() {
        Map<String, PagedListHolder<Report>> pagers =
                new HashMap<String, PagedListHolder<Report>>(1);
//        PagedListHolder<Report> pager = new PagedListHolder<Report>();
//        pager.setSort(new SpSortDefinition());
//        pager.setSource(new ArrayList());
        return pagers;
    }

    public ReportController() {
    }

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @RequestMapping(value = {"", "search"}, method = RequestMethod.GET, params = "new_search")
    public String setupForm(Model model) {
        logger.info("In GET SetupForm");
        model.addAttribute("view", "search");
        model.addAttribute("performers", reportService.getPerformers());
        model.addAttribute("startDate", new Date());
        model.addAttribute("endDate", new Date());
        model.addAttribute("performer", new String());
        return "form";
    }

    // TODO: fix pagination, fix validation
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public String doSearch(
            @Valid @ModelAttribute("startDate") @DateTimeFormat(pattern = "dd MMM yyyy")
            @RequestParam(value = "startDate") Date startDate,
            @ModelAttribute("endDate") @DateTimeFormat(pattern = "dd MMM yyyy")
            @RequestParam(value = "endDate") Date endDate,
            @ModelAttribute("performer") @Pattern(regexp = "^(?iu)[a-zа-я][ 0-9a-zа-я-#@%&\\$]{1,255}(?<!-)$")
            @RequestParam(value = "performer", defaultValue = "") String performer,
            @ModelAttribute("pagers") Map<String, PagedListHolder<Report>> pagers,
            Model model) {
        logger.error("In POST");
//        logger.info("startDate: {} of type {}", startDate, startDate.getClass().getConstructors());
//        logger.info("endDate: {} of type {}", endDate, endDate.getClass().getConstructors());
//        logger.info("performer: {} of type {}", performer, performer.getClass().getConstructors());

        List<Report> reports;
        if (performer.isEmpty()) {
            reports = reportService.getReports(startDate, endDate);
        } else {
            reports = reportService.getReports(performer, startDate, endDate);
        }
        logger.info("Reports cardinality: {}", reports.size());
        if (reports.isEmpty()) {
            return "redirect:search/nothing";
        }
        PagedListHolder<Report> pager;
        pager = new PagedListHolder(reports, new SpSortDefinition());
        pager.setPageSize(PAGINATION_THRESHOLD);
        pager.setPage(0);

        logger.error("Pager AFTER characteristics: pager={}, pageCount={}, pageSize={}",
                pager.toString(), pager.getPageCount(), pager.getPageSize());

        String searchId = SpHasher.getHash(new Object[]{startDate, endDate});

        model.addAttribute("search_id", searchId);
        pagers.put(searchId, pager);
        model.addAttribute("pagers", pagers);

        return "redirect:search";
    }

    // TODO: 'page' param validation
    @RequestMapping(value = "search", method = RequestMethod.GET, params = {"search_id"})
    public String exposeList(
            @ModelAttribute("pagers") Map<String, PagedListHolder<Report>> pagers,
            @RequestParam(value = "p", required = false) String page,
            @RequestParam(value = "search_id", required = true) String searchId,
            Model model) {
        logger.debug("In Redirect GET");
        boolean reject = false;
        PagedListHolder<Report> pager = pagers.get(searchId);
        if (pager == null) {
            return "redirect:search/nothing";
        }
        if (page != null) {
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
                    if ((intPage > 0) && (intPage <= pager.getPageCount()))
                    pager.setPage(intPage - 1);
                } catch (NumberFormatException ex) {
                    logger.warn("Cannot parse page number from request parameter 'page'={}", page);
                    reject = true;
                }
            }
            logger.debug("Setting pager page {}", page);
        } else {
            if (pager.getPageCount() > 0) {
                logger.debug("Setting the first pager page");
                pager.setPage(0);
            } else {
                reject = true;
            }
        }
        if (reject) {
            return "redirect:search/nothing";
        }
        model.addAttribute("search_id", searchId);
        model.addAttribute("pager", pager);
        return "list-paged";
    }
    
    @RequestMapping(value = "search/nothing", method = RequestMethod.GET) 
    public String nothingFound(Model model) {
        return "nothing";
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
