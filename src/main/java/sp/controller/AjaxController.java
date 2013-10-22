package sp.controller;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sp.model.Report;
import sp.model.ajax.AjaxResponse;
import sp.model.ajax.ErrorDetails;
import sp.service.ReportService;

/**
 * Controller for asynchronous user interaction
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping(value = "/report/ajax")
public class AjaxController {

    protected static final Logger logger = LoggerFactory.getLogger(AjaxController.class);
    @Inject
    ReportService reportService;
    @Inject
    @Named("messageSource")
    MessageSource messageSource;

    @InitBinder
    public void initReportDateEditor(WebDataBinder binder, Locale locale) {
        DateFormat df;
        df = sp.util.SpDateFormatFactory.getDateFormat("dd MMM yyyy", locale, null);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }

    /**
     * Return an report object with specified ID.
     *
     * @param id an ID of report being requested
     * @param model model object
     * @return report object - if was found, null - otherwise
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    Report getReport(@RequestParam Long id, Model model) {
        if (id > 0) {
            Report report = reportService.getReportById(id);
            if (report != null) {
                return report;
            }
        }
        return null;
    }

    /**
     * Removes an report with specified ID from the database. Does nothing, if
     * report with that ID wasn't found.
     *
     * @param id an ID of report being removed
     * @param model model object
     * @return string 'success' if removed from database, 'missing' - otherwise
     */
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public @ResponseBody
    String removeReport(@RequestParam("id") Long id,
            Model model) {
        if (reportService.hasReport(id)) {
            reportService.removeReport(id);
            return "success";
        } else {
            return "missing";
        }
    }

    /**
     * Checks validity and existence of passed report object and updates its
     * state in the database. Returns {@link AjaxResponse} object with status
     * and list of validation errors, if exists. return object will be
     * serialized to requested form (specified in request header 'Accept') by
     * Jackson or JAXB serializer if they are in Spring Framework classpath (no
     * need to manually register them if use <mvc:annotation-driven>).
     *
     * @param report report beign updated
     * @param result {@link BindingResult} object
     * @param model model object
     * @return {@link AjaxResponse} object that will be send to the client
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public @ResponseBody
    AjaxResponse update(@Valid Report report, BindingResult result,
            Locale locale, Model model) {
        AjaxResponse<Report> res;
        //TODO: atomic or silent?
        if (reportService.hasReport(report.getId())) {
            if (!result.hasErrors()) {
                reportService.updateReport(report);
                return new AjaxResponse<Report>(AjaxResponse.SUCCESS);
            } else {
                res = new AjaxResponse(AjaxResponse.ERROR);

                for (FieldError error : result.getFieldErrors()) {
                    String message = messageSource.getMessage(error, locale);

                    ErrorDetails details = new ErrorDetails(ErrorDetails.FIELD_ERROR,
                            error.getField(),
                            error.getRejectedValue(),
                            message);
                    res.addError(details);
                }

                for (ObjectError error : result.getGlobalErrors()) {
                    String message = messageSource.getMessage(error, locale);

                    ErrorDetails details = new ErrorDetails(ErrorDetails.OBJECT_ERROR,
                            error.getObjectName(),
                            "",
                            message);
                    res.addError(details);
                }
                return res;
            }
        }
        return new AjaxResponse(AjaxResponse.ERROR);
    }

    /**
     * Checks validity of passed report object and adds to the database. Returns
     * {@link AjaxResponse} object with status and list of validation errors, if
     * exists. return object will be serialized to requested form (specified in
     * request header 'Accept') by Jackson or JAXB serializer if they are in
     * Spring Framework classpath (no need to manually register them if use
     * <mvc:annotation-driven>).
     *
     * @param report report beign updated
     * @param result {@link BindingResult} object
     * @param model model object
     * @paran locale current User locale
     * @return {@link AjaxResponse} object that will be send to the client
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public @ResponseBody
    AjaxResponse add(@Valid Report report, BindingResult result,
            Locale locale, Model model) {
        AjaxResponse<Report> res;
        if (!result.hasErrors()) {
            reportService.addReport(report);
            res = new AjaxResponse<Report>(AjaxResponse.SUCCESS);
        } else {
            res = new AjaxResponse<Report>(AjaxResponse.ERROR);
            for (FieldError error : result.getFieldErrors()) {
                String message = messageSource.getMessage(error, locale);

                ErrorDetails details = new ErrorDetails(ErrorDetails.FIELD_ERROR,
                        error.getField(),
                        error.getRejectedValue(),
                        message);
                res.addError(details);
            }

            for (ObjectError error : result.getGlobalErrors()) {
                String message = messageSource.getMessage(error, locale);

                ErrorDetails details = new ErrorDetails(ErrorDetails.OBJECT_ERROR,
                        error.getObjectName(),
                        "",
                        message);
                res.addError(details);
            }
        }
        return res;
    }

    /**
     * Add report with specified ID to the user's checklist
     *
     * @param id an ID of report being watched
     * @param session current user session
     * @param model model object
     * @return string "success" if was added to checklist, otherwise - empty
     * string
     */
    @RequestMapping(value = "watch/{id}")
    public @ResponseBody
    String watchReportById(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (reportService.hasReport(id)) {
            //TODO: synchronized(session.getId().intern()) {..}?
            Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
            if (checklist != null) {
                checklist.add(id);
                return "success";
            }
        }
        return "";
    }

    /**
     * Add reports with specified IDs to the user's checklist.
     *
     * @param indexes an array of IDs being watch
     * @param session current user session
     * @param model model object
     * @return string "success" if was added to checklist, if not all was added
     * - a comma-separated string of reports was added, otherwise - empty string
     */
    @RequestMapping(value = "watch")
    public @ResponseBody
    String watchAll(@RequestParam("indexes[]") Long[] indexes,
            HttpSession session, Model model) {
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if (checklist != null) {
            Long[] check = reportService.hasReports(indexes);
            if (check.length == indexes.length) {
                checklist.addAll(Arrays.asList(indexes));
                return "success";
            } else if (check.length != 0) {
                checklist.addAll(Arrays.asList(check));
                StringBuilder sb = new StringBuilder(check.length * 4);
                for (Long id : check) {
                    sb.append(id).append(',');
                }
                return sb.toString();
            }
        }
        return "";
    }

    /**
     * Compute and return report's URL if it exists.
     *
     * @param id an ID of report being requested
     * @param request HTTP request object
     * @param model model object
     * @return an URL address requested report being available, null - if report
     * inaccessible
     */
    @RequestMapping(value = "uri", method = RequestMethod.GET)
    public @ResponseBody
    String getUri(@RequestParam("id") Long id,
            HttpServletRequest request, Model model) {
        if (reportService.hasReport(id)) {
            StringBuffer url = request.getRequestURL();
            StringBuilder server = new StringBuilder(url.length());
            server.append(url.substring(0, url.indexOf(request.getRequestURI())));
            server.append(request.getContextPath()).append("/report/detail/").append(id);
            return server.toString();
        } else {
            return "";
        }
    }

    /**
     * Removes an report with specified index from the pager, so it still
     * up-to-date with the User actions.
     *
     * @param id an report's ID being looking for
     * @param searchId search id
     * @param session session object
     * @param model model object
     * @return string 'success' - if successfully removed, empty string -
     * otherwise
     */
    @RequestMapping(value = "pager/remove", method = RequestMethod.POST)
    public @ResponseBody
    String removeFromPager(@RequestParam("id") Long id,
            @RequestParam("search_id") String searchId,
            HttpSession session, Model model) {
        PagedListHolder<Report> pager = ((Map<String, PagedListHolder<Report>>) session.getAttribute("pagers")).get(searchId);
        //TODO: synchronized(pager) {..}?
        if (pager != null) {
            List<Report> list = pager.getSource();
            Report idReport = new Report();
            idReport.setId(id);
            int pos = Collections.binarySearch(list, id, new Comparator() {
                @Override
                public int compare(Object report1, Object report2) {
                    if (((Report) report1).getId() == (((Report) report2)).getId()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            list.remove(pos);
            return "success";
        }
        return "";
    }
}
