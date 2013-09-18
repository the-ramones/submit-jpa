package sp.controller;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sp.model.Report;
import sp.service.ReportService;

/**
 * Checklist controller
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("/checklist")
public class ChecklistController {

    private static final Logger logger = LoggerFactory.getLogger(ChecklistController.class);
    @Inject
    private ReportService reportService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String checklist(HttpSession session, Model model) {
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        List<Report> reports;
        if (!checklist.isEmpty()) {
            reports = reportService.getReports(checklist);
            model.addAttribute("reports", reports);
        } else {
            return "redirect:nothing";
        }        
        for (Long id : checklist) {
            logger.info("ID added to checklist: {}", id);
        }
        return "checklist";
    }

    @RequestMapping(value = "empty", method = RequestMethod.POST)
    public String emptyChecklist(HttpSession session, Model model) {
        session.removeAttribute("checklist");
        model.addAttribute("new_search", "");
        return "redirect:/report/search";
    }
    
    @RequestMapping(value = "remove/{id}", method = RequestMethod.GET) 
    public @ResponseBody String removeFromChecklist(@PathVariable Long id, 
        HttpSession session, Model model) {
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if (checklist != null && !checklist.isEmpty()) {
            checklist.remove(id);
        } else {
            return "missing";
        }
        return "success";
    }
    
    //TODO: validation, Validator @NotNull or right in controller
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public @ResponseBody String removeFromChecklistById(@RequestParam Long id,
        HttpSession session, Model model) {
        logger.error("IN REMOVE: {}", id);
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if ((checklist != null) && (checklist.isEmpty())) {
            checklist.remove(id);
            session.setAttribute("checklist", checklist);
        } else {
            return "missing";
        }
        return "success";
    }
}
