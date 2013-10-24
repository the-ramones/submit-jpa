package sp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sp.model.Report;
import sp.model.User;
import sp.model.ajax.Statistics;
import sp.service.EmailService;
import sp.service.ReportService;
import sp.service.UserService;
import sp.util.SpStatisticsGenerator;

/**
 * Checklist controller
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("/checklist")
public class ChecklistController {

    protected static final Logger logger = LoggerFactory.getLogger(ChecklistController.class);
    @Inject
    private ReportService reportService;
    @Inject
    private UserService userService;
    @Inject
    private EmailService emailService;

    @ModelAttribute("page_key")
    public String referenceData() {
        return "title.checklist";
    }

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
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public @ResponseBody
    String removeFromChecklistById(@RequestParam("id") Long id,
            HttpSession session, Model model) {
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if ((checklist != null) && !checklist.isEmpty()) {
            checklist.remove(id);
        } else {
            return "missing";
        }
        return "success";
    }

    @RequestMapping(value = "stats", method = RequestMethod.GET)
    public @ResponseBody
    Statistics getStatistics(HttpSession session,
            Model model) {
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if ((checklist != null) && !checklist.isEmpty()) {
            Statistics stats = SpStatisticsGenerator.generateStatistics(checklist);
            return stats;
        }
        return null;
    }
    private static final String JSESSIONID_KEY = "JSESSIONID";

    @RequestMapping(value = "email", method = RequestMethod.GET)
    public @ResponseBody
    String sendStatisticsOnEmail(HttpSession session, HttpServletRequest request,
            @CookieValue(JSESSIONID_KEY) String sessionId, Locale locale, Model model) {
        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if (checklist != null) {
            if (!checklist.isEmpty()) {
                Statistics stats = SpStatisticsGenerator.generateStatistics(checklist);
                session.setAttribute("statistics", stats);

                String emailHtml = null;
                /*
                 * Make a request to '/email/statistics'
                 */
                StringBuffer rawUrl = request.getRequestURL();
                int index = rawUrl.indexOf("/checklist/email");
                int responseCode = 0;
                if (index != -1) {
                    String baseUrl = rawUrl.substring(0, index);
                    try {
                        URL url = new URL(baseUrl + "/email/statistics");
                        HttpURLConnection connection =
                                (HttpURLConnection) url.openConnection();
                        if (!StringUtils.isEmpty(sessionId)) {
                            connection.setRequestProperty("Cookie", "JSESSIONID="
                                    + URLEncoder.encode(sessionId, "UTF-8"));
                        }
                        connection.setReadTimeout(10000);
                        connection.setRequestMethod("GET");
                        connection.setDoOutput(true);

                        responseCode = connection.getResponseCode();

                        InputStream in = connection.getInputStream();
                        String encoding = connection.getContentEncoding();
                        encoding = encoding == null ? "UTF-8" : encoding;
                        emailHtml = new String(IOUtils.toByteArray(in), encoding);
                    } catch (MalformedURLException ex) {
                        logger.warn("Malformed URL when constructing path to email controller", ex);
                    } catch (IOException ioex) {
                        logger.warn("Error with getting input stream from the URL connection", ioex);
                    }
                }

                //TODO: replace mock implementation with Spring Security artifacts
                User user = userService.getUserById(1);

                /*
                 * Sending an e-mail
                 */
                emailService.sendEmailWithStatisticsAndPdfAttachment(
                        emailHtml, stats, user.getFullname(), locale, user.getEmail());
                return "success";
            } else {
                return "empty";
            }
        } else {
            return "error";
        }
    }
}
