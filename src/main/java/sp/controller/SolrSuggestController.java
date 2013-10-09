package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import sp.util.SpLightPager;

/**
 * Solr-based full text search Reports! controller.
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("suggest-solr")
@SessionAttributes(types = {SpLightPager.class})
public class SolrSuggestController {

    @ModelAttribute("solr-pager")
    public SpLightPager populateLightPager() {
        return new SpLightPager(0);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String setupForm(SessionStatus status, Model model) {
        return "search-solr";
    }
}
