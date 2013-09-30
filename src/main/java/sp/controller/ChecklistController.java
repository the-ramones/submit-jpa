package sp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;
import org.apache.pdfbox.io.IOUtils;
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
import sp.model.ajax.Statistics;
import sp.service.EmailService;
import sp.service.ReportService;
import sp.util.SpEmailGenerator;
import sp.util.SpStatisticsGenerator;

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
    
    @Inject
    private EmailService emailService;

    /**
     * Checks User session for 'checklist' set with IDs of checked-for-later
     * reports. Retrieves reports from the service layer and put it to the model
     * for rendering in view
     *
     * @param session current user session object
     * @param model model object
     * @return view name
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String checklist(HttpSession session, Model model) {
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        List<Report> reports = null;
        if ((checklist != null) && !checklist.isEmpty()) {
            reports = reportService.getReports(checklist);
            /*
             * Check for null was leaved for view
             */
            model.addAttribute("reports", reports);

            StringBuilder sb = new StringBuilder(1024);
            for (Long id : checklist) {
                sb.append(id).append(' ');
            }
            logger.debug("IDs added to checklist: {}", sb.toString());
        }
        return "checklist";
    }

    /**
     * Empties User checklist.
     *
     * @param session current user session object
     * @param model model object
     * @return view name
     */
    @RequestMapping(value = "empty", method = RequestMethod.POST)
    public String emptyChecklist(HttpSession session, Model model) {
        session.removeAttribute("checklist");
        // TODO: should or not?
        session.removeAttribute("pagers");
        model.addAttribute("new_search", "");
        return "redirect:/report/search";
    }

    /**
     * Removes an report with specified ID as a part of request URL.
     *
     * @param id an ID of report being removed
     * @param session current user session object
     * @param model model object
     * @return string 'success' if was removed, 'missing' - if report with
     * specified ID wasn't found in user's checklist
     */
    @RequestMapping(value = "remove/{id}", method = RequestMethod.GET)
    public @ResponseBody
    String removeFromChecklist(@PathVariable Long id,
            HttpSession session, Model model) {
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if (checklist != null && !checklist.isEmpty()) {
            checklist.remove(id);
        } else {
            return "missing";
        }
        return "success";
    }

    /**
     * Removes an report with specified ID as a request parameter.
     *
     * @param id an ID of report being removed
     * @param session current user session object
     * @param model model object
     * @return string 'success' if was removed, 'missing' - if report with
     * specified ID wasn't found in user's checklist
     */
    //TODO: validation, Validator @NotNull or right in controller
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public @ResponseBody
    String removeFromChecklistById(@RequestParam("id") Long id,
            HttpSession session, Model model) {
        logger.debug("IN CHECKLIST REMOVE: {}", id);

        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if ((checklist != null) && !checklist.isEmpty()) {
            checklist.remove(id);
            /*
             * TODO: Put back to the model or not?
             */
            logger.debug("CHECKLIST: {}", checklist);
        } else {
            return "missing";
        }
        return "success";
    }

    @RequestMapping(value = "stats", method = RequestMethod.GET)
    public @ResponseBody
    Statistics getStatistics(HttpSession session,
            Model model) {
        logger.debug("IN GET STATISTICS");

        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if ((checklist != null) && !checklist.isEmpty()) {
            Statistics stats = SpStatisticsGenerator.generateStatistics(checklist);
            return stats;
        }
        return null;
    }

    @RequestMapping(value = "email", method = RequestMethod.GET)
    public @ResponseBody
    String sendStatisticsOnEmail(HttpSession session, HttpServletRequest request,
            Locale locale, Model model) {
        logger.debug("IN SEND ON EMAIL");

        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if (checklist != null) {
            if (!checklist.isEmpty()) {
                Statistics stats = SpStatisticsGenerator.generateStatistics(checklist);
                String emailHtml = null;
                /*
                 * Make a request to '/email/statistics'
                 */
                StringBuffer rawUrl = request.getRequestURL();                
                rawUrl.substring(rawUrl.indexOf("/checklist/email"));
                try {
                    URL emailUrl = new URL(rawUrl.toString());                    
                    URLConnection con = emailUrl.openConnection();
                    InputStream in = con.getInputStream();
                    String encoding = con.getContentEncoding();
                    encoding = encoding == null ? "UTF-8" : encoding;
                    byte[] b = new byte[1024];
                    emailHtml = new String(IOUtils.toByteArray(in), encoding);                    
                } catch (MalformedURLException ex) {
                    logger.warn("Malformed URL when constructing path to email controller", ex);
                } catch (IOException ioex) {
                    logger.warn("Error with getting input stream from the URL connection", ioex);
                }                
                emailService.sendEmailWithStatistics(emailHtml, stats);
                return "success";
            } else {
                return "empty";
            }
        } else {
            return "error";
        }
    }
}
