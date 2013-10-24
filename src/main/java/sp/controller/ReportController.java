package sp.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import org.springframework.context.annotation.DependsOn;
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
import sp.util.service.SolrAdministrator;

/**
 * Report controller for synchronous actions
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("/report")
@SessionAttributes(value = {"pagers", "checklist"})
public class ReportController {

    protected static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private static final int PAGERS_INITIAL_CAPACITY = 4;
    /*
     * Spring 3+ way. Previously, used @Value(#{systemProperties.pagination.threshold})
     */
    @Value("${pagination.maxonpager}")
    private int MAX_ON_PAGER;
    @Value("${pagination.threshold}")
    private int PAGINATION_THRESHOLD;
    private int CHECKLIST_INITIAL_CAPACITY = 32;
    @Inject
    private ReportService reportService;

    @ModelAttribute("page_key")
    public String referenceData() {
        return "title.reports";
    }

    @PostConstruct
    public void postConstruct() {
    }

    @PreDestroy
    public void preDestroy() {
    }

    @ModelAttribute("settings")
    public Map<String, String> populateReferenceData() {
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

    @ModelAttribute("pagers")
    public Map<String, PagedListHolder<Report>> populatePager() {
        Map<String, PagedListHolder<Report>> pagers =
                new HashMap<String, PagedListHolder<Report>>(PAGERS_INITIAL_CAPACITY);
        return pagers;
    }

    @ModelAttribute("checklist")
    public Set<Long> createChecklist() {
        logger.debug("Create initial checklist");
        Set<Long> checklist = new HashSet<Long>(CHECKLIST_INITIAL_CAPACITY);
        return checklist;
    }

    public ReportController() {
    }

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Set up 'search report' form.
     *
     * @param model model object
     * @return view name
     */
    @RequestMapping(value = {"", "search"}, method = RequestMethod.GET, params = "new_search")
    public String setupForm(Model model) {
        model.addAttribute("view", "search");
        model.addAttribute("performers", reportService.getPerformers());
        model.addAttribute("startDate", new Date());
        model.addAttribute("endDate", new Date());
        model.addAttribute("performer", new String());
        return "form";
    }

    /**
     * POST request for searching for reports in the database.
     *
     * @param startDate start date
     * @param endDate end date
     * @param performer performer
     * @param pagers session-scope pagers
     * @param model model object
     * @return view name
     */
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

        String searchId = SpHasher.getHash(new Object[]{startDate, endDate});

        model.addAttribute("search_id", searchId);
        pagers.put(searchId, pager);
        model.addAttribute("pagers", pagers);

        return "redirect:search";
    }

    /**
     * Retrieve request page from the current user session for the specified
     * search ID and populate model with the list of reports.
     *
     * @param pagers session-scoped pagers
     * @param page requested page
     * @param searchId an ID of current search
     * @param model model object
     * @return view name
     */
    @RequestMapping(value = "search", method = RequestMethod.GET, params = {"search_id"})
    public String exposeList(
            @ModelAttribute("pagers") Map<String, PagedListHolder<Report>> pagers,
            @RequestParam(value = "p", required = false) String page,
            @RequestParam(value = "search_id", required = true) String searchId,
            Model model) {
        boolean reject = false;
        PagedListHolder<Report> pager = pagers.get(searchId);
        if (pager == null) {
            reject = true;
        }
        if (page != null) {
            /*
             * Page must be: 'next', 'prev', 'page-number'
             */
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
                    if ((intPage > 0) && (intPage <= pager.getPageCount())) {
                        pager.setPage(intPage - 1);
                    }
                } catch (NumberFormatException ex) {
                    logger.warn("Cannot parse page number from request parameter 'page'={}", page);
                    reject = true;
                }
            }
        } else {
            if (pager.getPageCount() > 0) {
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

    /**
     * Renders 'nothing found' page for search criteria.
     *
     * @param model model object
     * @return view name
     */
    @RequestMapping(value = "search/nothing", method = RequestMethod.GET)
    public String nothingFound(Model model) {
        return "nothing";
    }

    /**
     * Set up of 'detail' form.
     *
     * @param model model object
     * @return view name
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String setupDetailForm(Model model) {
        model.addAttribute("view", "byid");
        return "byid";
    }

    /**
     * POST request for details of report with specified ID as a request
     * parameter.
     *
     * @param id
     * @param model
     * @return view name
     */
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

    /**
     * REST-like request for details of report with specified ID as a request
     * path part.
     *
     * @param id report's ID being searched for
     * @param model model object
     * @param request HTTP request object
     * @return view name
     */
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detailById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        model.addAttribute("report", reportService.getReportById(id));
        model.addAttribute("uri", request.getRequestURL());
        return "detail";
    }

    /**
     * Set up of 'add new report' form.
     *
     * @param model model object
     * @param response HTTP response object
     * @return view name
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String setupAddForm(Model model, HttpServletResponse response) {
        model.addAttribute("view", "add");
        model.addAttribute("report", new Report());
        return "add";
    }

    /**
     * POST request for adding a new report to the database.
     *
     * @param report a new report
     * @param result binding result object
     * @param model model object
     * @param req HTTP request object
     * @param res HTTP response object
     * @return {@link ModelAndView} object
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ModelAndView add(@Valid @ModelAttribute("report") Report report,
            BindingResult result, Model model, HttpServletRequest req, HttpServletResponse res) {
        ModelAndView mav = new ModelAndView();
        if (result.hasErrors()) {
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
        mav.addAllObjects(model.asMap());
        mav.setViewName("detail");
        return mav;
    }

    @RequestMapping(value = "/suggest", method = RequestMethod.GET)
    public String realTimeSearch(Model model) {
        model.addAttribute("view", "ajax");
        return "search";
    }
}
