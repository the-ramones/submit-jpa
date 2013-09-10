package sp.controller;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
import org.springframework.web.servlet.ModelAndView;
import sp.model.Report;
import sp.service.ReportService;

/**
 * Report controller
 *
 * @author the-ramones
 */
@Controller
@RequestMapping("/report")
public class ReportController {

    static final Logger logger = LoggerFactory.getLogger(ReportController.class);
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
        logger.info("IN @InitBinder");
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        if (locale.equals(Locale.forLanguageTag("ru"))) {
            df = new SimpleDateFormat("dd MMM yyyy", new DateFormatSymbols() {
                @Override
                public String[] getMonths() {
                    return new String[]{
                        "Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Февраля"
                    };
                }

                @Override
                public String[] getShortMonths() {
                    return new String[]{
                        "Янв", "Фев", "Мар", "Апр", "Мая", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Фев"
                    };
                }
            });
        } else {
            System.out.println("Checking En Locale in SimpleDateFormating");
            df = new SimpleDateFormat("dd MMM yyyy", locale);
        }
        df.setLenient(false);
        logger.debug("EXITING @InitBinder");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
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

    /**
     * Posting back of command object
     *
     * @param report
     * @param result
     * @param model
     * @return view name
     */
//    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
//    public String add(@Valid @ModelAttribute("report") Report report,
//            BindingResult result, Model model, HttpServletRequest req, HttpServletResponse res) {
//        if (result.hasErrors()) {
//            System.out.println("report: " + report);
//            System.out.println("model: " + model);
//            model.addAttribute("view", "add");
//            return "add";
//        }
//        report = reportService.addReport(report);
//        String detailUri = "detail/" + report.getId();
//        String redirectUri = "redirect:" + detailUri;
//        model.addAttribute("uri", req.getContextPath() + detailUri);
//        model.addAttribute("back", req.getRequestURI());
//        model.addAttribute("report", report);
//        System.out.println("URI:" + req.getRequestURI());
//        System.out.println("URL:" + req.getRequestURL());
//        System.out.println("User:" + req.getRemoteUser());
//        System.out.println("Session id :" + req.getRequestedSessionId());
//        return redirectUri;
//    }
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
