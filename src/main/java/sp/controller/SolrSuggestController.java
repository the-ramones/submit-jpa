package sp.controller;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.TermsFieldEntry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import sp.model.Report;
import sp.model.ajax.Tuple;
import sp.service.SolrService;
import sp.util.SpLightPager;

/**
 * Solr-based full text search Reports! controller.
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("suggest-solr")
@SessionAttributes(types = {SpLightPager.class, Pageable.class})
public class SolrSuggestController {

    @Inject
    SolrService solrService;
    private static final int HASH_CLOUD_COUNT = 12;

    @ModelAttribute("solr-pager")
    public SpLightPager populateLightPager() {
        return new SpLightPager(0);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String setupForm(SessionStatus status, Model model) {
        return "search-solr";
    }

    @RequestMapping(value = "cloud", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    List getSearchCloud(HttpSession session, Model model) {
        List result = new ArrayList(0);
        int count = 0;
        for (TermsFieldEntry entry : solrService.getSearchCloud().getContent()) {
            if (count < HASH_CLOUD_COUNT) {
                result.add(new Tuple(entry.getValue(),
                        String.valueOf(entry.getValueCount())));
                count++;
            } else {
                break;
            }
        }
        return result;
    }

    @RequestMapping(value = "search", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    List<Report> search(@RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) Integer limit,
            HttpSession session, Model model) {
        if (query != null) {
            if (limit == null || limit <= 0) {
                limit = DEFAULT_SEARCH_LIMIT;
            }
            return solrService.search(query).getContent();
        } else {
            return new ArrayList(0);
        }
    }

    private static final int DEFAULT_SUGGEST_LIMIT = 8;
    private static final int DEFAULT_SEARCH_LIMIT = 10;
    
    @RequestMapping(value = "suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    List<Report> suggest(@RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) Integer limit,
            HttpSession session, Model model) {
        if (query != null) {
            if (limit == null || limit <= 0) {
                limit = DEFAULT_SUGGEST_LIMIT;
            }            
            return solrService.search(query).getContent();
        } else {
            return new ArrayList<Report>(0);
        }
    }
}
