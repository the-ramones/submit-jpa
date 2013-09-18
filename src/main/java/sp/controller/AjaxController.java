package sp.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String deleteReport(@RequestParam("id") Long id,
            Model model) {
        return "success";
    }

    @RequestMapping(value = "update")
    public @ResponseBody
    String update(@RequestBody String report, Model model) {
        return "success";
    }

    @RequestMapping(value = "add")
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
        logger.info("In watchReportById");

        if (reportService.hasReport(id)) {
            Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
            if (checklist != null) {
                checklist.add(id);
                return "success";
            }
        }

        logger.info("recieved ID: {}", id);
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
        logger.info("In watch");

        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if (checklist != null) {
            Long[] check = reportService.hasReports(indexes);
            if (check.length == indexes.length) {
                checklist.addAll(Arrays.asList(indexes));
                return "success";
            } else if (check.length != 0) {
                checklist.addAll(Arrays.asList(check));
                StringBuilder sb = new StringBuilder(check.length * 4);
                for (Long id: check) {
                    sb.append(id).append(',');
                }
                return sb.toString();
            }
        }
        
        logger.info("AJAX: checklist {}", checklist);
        Map pagers = (Map) session.getAttribute("pagers");
        logger.info("AJAX: pagers {}", pagers);

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
        logger.error("IN URL AJAX: {}", id);

        if (reportService.hasReport(id)) {
            StringBuffer url = request.getRequestURL();
            url.substring(0, url.indexOf(request.getRequestURI()));
            url.append("/report/detail/").append(id);

            logger.error("IN AJAX URL: {}", url.toString());

            return url.toString();
        } else {
            return "";
        }
    }
}
