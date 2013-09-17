package sp.controller;

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

    @RequestMapping(value = "watch")
    public @ResponseBody
    //String watchAll(@RequestParam(value = "indexes", required = false) String[] indexes, Model model) {
    String watchAll(@RequestParam("indexes[]") String[] indexes, Model model) {
        logger.info("In watch");
        for (String id : indexes) {
            logger.info("id: {}", id);
        }
        return "success:";
    }

    @RequestMapping(value = "watch2")
    public @ResponseBody
    String watchAll2(@RequestBody String indexes, Model model) {
        logger.info("In watch2");
//        for (String id : indexes) {
//            logger.info("id: {}", id);
//        }
        return "success: " + indexes;
    }

    @RequestMapping(value = "watc3")
    public @ResponseBody
    //String watchAll(@RequestParam(value = "indexes", required = false) String[] indexes, Model model) {
    String watchAll3(@RequestParam("indexes[]") String[] indexes, Model model) {
        logger.info("In watch");
        for (String id : indexes) {
            logger.info("id: {}", id);
        }
        return "success:";
    }

    @RequestMapping(value = "watch/{id}")
    public @ResponseBody
    String watchReportById(@PathVariable("id") Long id, HttpSession session, Model model) {
        logger.info("In watchReportById");
        logger.info("recieved ID: {}", id);
        return "success";
    }
}
