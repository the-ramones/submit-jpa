package sp.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sp.model.Report;
import sp.service.ReportService;

/**
 * Controller for asynchronous user interaction
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping(value = "/report/ajax")
public class AjaxController {

    private static final Logger logger = LoggerFactory.getLogger(AjaxController.class);
    @Inject
    ReportService reportService;

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
        logger.debug("IN GET REPORT AJAX");
        logger.debug("Requested ID:", id);

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
        logger.debug("IN GET REPORT AJAX");
        logger.debug("Requested ID:", id);

        if (reportService.hasReport(id)) {
            reportService.removeReport(id);
            return "success";
        } else {
            return "missing";
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public @ResponseBody
    String update(@RequestBody String report, Model model) {
        return "success";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public @ResponseBody
    String add(@RequestBody String report, Model model) {
        return "success";
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
        logger.debug("In watchReportById");

        if (reportService.hasReport(id)) {
            Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
            if (checklist != null) {
                checklist.add(id);
                return "success";
            }
        }

        logger.debug("recieved ID: {}", id);
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
        logger.debug("In watch");

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

        logger.debug("AJAX: checklist {}", checklist);
        Map pagers = (Map) session.getAttribute("pagers");
        logger.debug("AJAX: pagers {}", pagers);

        return "";
    }

    ///// TESTS /////
    @RequestMapping(value = "watch2")
    public @ResponseBody
    String watchAll2(@RequestBody String indexes, Model model) {
        logger.info("In watch2");
        return "success: " + indexes;
    }

    private class R {

        String i;
        String g;

        public String getI() {
            return i;
        }

        public void setI(String i) {
            this.i = i;
        }

        public String getG() {
            return g;
        }

        public void setG(String g) {
            this.g = g;
        }
    }

    @RequestMapping(value = "watch3")
    public @ResponseBody
    String watchAll3(@RequestParam("indexes") R indexes, Model model) {
        logger.info("In watch3");
        logger.error(indexes.toString());
        return "success: " + indexes.toString();
    }

    @RequestMapping(value = "watch4")
    public @ResponseBody
    String watchAll4(@RequestBody R indexes, Model model) {
        logger.info("In watch4");
        logger.error(indexes.toString());
        return "success: " + indexes.toString();
    }

    ////////////////////
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
        logger.debug("IN URL AJAX: {}", id);

        if (reportService.hasReport(id)) {
            StringBuffer url = request.getRequestURL();
            StringBuilder server = new StringBuilder(url.length());
            server.append(url.substring(0, url.indexOf(request.getRequestURI())));
            server.append(request.getContextPath()).append("/report/detail/").append(id);

            logger.debug("URL: {}", request.getRequestURL());
            logger.debug("URI: {}", request.getRequestURI());
            logger.debug("index of URI in URL: {}", url.indexOf(request.getRequestURI()));
            logger.debug("IN AJAX URL: {}", server.toString());

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
