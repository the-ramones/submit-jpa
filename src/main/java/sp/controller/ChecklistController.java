package sp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
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

    private static final Logger logger = LoggerFactory.getLogger(ChecklistController.class);
    @Inject
    private ReportService reportService;
    @Inject
    private UserService userService;
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

    private static final String JSESSION_KEY = "JSESSIONID";
    
    @RequestMapping(value = "email", method = RequestMethod.GET)
    public @ResponseBody
    String sendStatisticsOnEmail(HttpSession session, HttpServletRequest request,
            @CookieValue("JSESSIONID") String sessionId, Locale locale, Model model) {
        logger.debug("IN SEND ON EMAIL");

        Set<Long> checklist = (Set<Long>) session.getAttribute("checklist");
        if (checklist != null) {
            if (!checklist.isEmpty()) {
                Statistics stats = SpStatisticsGenerator.generateStatistics(checklist);
                session.setAttribute("statistics", stats);
                
                /*
                 * Session Ids
                 */
                logger.error("JSESSION ID FROM SESSION: {}", session.getId());
                logger.error("JSESSION ID FROM @CookieValue: {}", sessionId);
                String reqSessionId = null;
                for (Cookie cookie: request.getCookies()) {
                    if (cookie.getName().equals("JSESSIONID")) {
                        reqSessionId = cookie.getValue();
                    }
                }
                logger.error("JSESSION ID FROM REQUEST: {}", reqSessionId);
                
                String emailHtml = null;
                /*
                 * Make a request to '/email/statistics'
                 */
                StringBuffer rawUrl = request.getRequestURL();
                int index = rawUrl.indexOf("/checklist/email");
                if (index != -1) {
                    String baseUrl = rawUrl.substring(0, index);
                    try {
                        CookieManager cm = new CookieManager();
                        cm.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
                        CookieHandler.setDefault(cm);
                        CookieStore cookieJar = cm.getCookieStore();
                        HttpCookie sessionCookie = new HttpCookie("JSESSIONID", reqSessionId);

                        URL emailUrl = new URL(baseUrl + "/email/statistics");
                        try {
                            cookieJar.add(emailUrl.toURI(), sessionCookie);
                        } catch(URISyntaxException uex) {
                            logger.warn("Error in URI syntax");
                        }

                        HttpURLConnection con = (HttpURLConnection) emailUrl.openConnection();
                        InputStream in = con.getInputStream();
                        String encoding = con.getContentEncoding();
                        encoding = encoding == null ? "UTF-8" : encoding;
                        byte[] b = new byte[1024];
                        int c = 0;
                        emailHtml = new String(IOUtils.toByteArray(in), encoding);
                        
                        for (HttpCookie cookie: cm.getCookieStore().getCookies()) {
                            System.out.println("AFTER COOKIE: " + cookie.toString());
                        }
                    } catch (MalformedURLException ex) {
                        logger.warn("Malformed URL when constructing path to email controller", ex);
                    } catch (IOException ioex) {
                        logger.warn("Error with getting input stream from the URL connection", ioex);
                    }
                }

                //TODO: replace mock implementation with Spring Security artifacts
                User user = userService.getUserById(1);

                logger.error("user, stats, locale, reciever: {} {} {} {}", stats, user.getFullname(), locale, user.getEmail());

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
