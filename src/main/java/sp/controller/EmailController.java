package sp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import sp.model.ajax.Statistics;

/**
 * Controller for HTML email creation. Utilizes out-of-the-box support of
 * Velocity Template Engine by Spring Framework
 *
 * @author Paul Kulitski
 * @see Controller
 */
@Controller
@RequestMapping("email")
@SessionAttributes("statistics")
public class EmailController {

    protected static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @ModelAttribute("statistics")
    public Statistics populateStatistics() {
        return new Statistics();
    }

    /**
     * Accepts {@link Statistics} instance as a session attribute and pass it to
     * View for rendering.
     *
     * @param stats User's session {@link Statistics} object
     * @param model model object
     * @return view name
     */
    @RequestMapping(value = "statistics", method = RequestMethod.GET)
    public String getHtmlWithStatistics(@ModelAttribute("statistics") Statistics stats,
            Model model, HttpServletRequest req, HttpServletResponse res, HttpSession session) {

        model.addAttribute("statistics", stats);

        return "stats-email";
    }
}
