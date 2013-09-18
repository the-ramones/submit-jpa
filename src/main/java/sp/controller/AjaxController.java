package sp.controller;

import java.util.Map;
import java.util.Set;
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

/**
 * Controller for asynchronous user interaction
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping(value = "/report/ajax")
public class AjaxController {

    private static final Logger logger = LoggerFactory.getLogger(AjaxController.class);

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String deleteReport(@RequestParam("id") Long id,
            Model model) {
        return "success";
    }

    @RequestMapping(value = "/get-uri")
    public @ResponseBody
    String getUri(@RequestParam("id") Long id,
            Model model) {
        return "url";
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

    @RequestMapping(value = "watch/{id}")
    public @ResponseBody
    String watchReportById(@PathVariable("id") Long id, HttpSession session, Model model) {
        logger.info("In watchReportById");
        Set<Long> watchlist = (Set<Long>) session.getAttribute("checklist");
        watchlist.add(id);
        logger.info("recieved ID: {}", id);
        return "success: added id=" + id;
    }

    @RequestMapping(value = "watch")
    public @ResponseBody
    String watchAll(@RequestParam("indexes[]") Long[] indexes,
            HttpSession session, Model model) {
        logger.info("In watch");
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        logger.info("AJAX: checklist {}", checklist);
        Map pagers = (Map) session.getAttribute("pagers");
        logger.info("AJAX: pagers {}", pagers);

        if (checklist != null) {
            for (Long id : indexes) {
                checklist.add(id);
                logger.info("id: {}", id);
            }
        }
        return "success: added " + indexes.length + "items";
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
    @RequestMapping(value = "uri", method = RequestMethod.GET)
    public @ResponseBody
    String getUri(@RequestParam("id") Long id,
            HttpServletRequest request, Model model) {
        logger.error("IN URL AJAX: {}",id);
        String context = request.getContextPath();
        String uri = context + "/report/detail/" + id;
        return uri;
    }
}
