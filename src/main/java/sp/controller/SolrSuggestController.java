package sp.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.TermsFieldEntry;
import org.springframework.data.solr.core.query.result.TermsPage;
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
        /*
         * Add Search Cloud to the page
         */
        System.out.println("SOLR SERVICE: " + solrService);
        long maxOccurrence = 0;
        for (TermsFieldEntry entry : solrService.getSearchCloud().getContent()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " : " + entry.getValueCount());
            
            if (entry.getValueCount() > maxOccurrence) {
                maxOccurrence = entry.getValueCount();
            }
        }
        Iterator cloud = solrService.getSearchCloud().getContent().iterator();
        
        model.addAttribute("maxOccurrence", maxOccurrence);
        model.addAttribute("cloud", cloud);
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
    Page<Report> search(@RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) Integer limit,
            HttpSession session, Model model) {
        if (query != null) {
            if (limit == null || limit <= 0) {
                limit = DEFAULT_SEARCH_LIMIT;
            }
            return solrService.search(query);
        } else {
            return new PageImpl<Report>(new ArrayList<Report>(0));
        }
    }

    private static final int DEFAULT_SUGGEST_LIMIT = 8;
    private static final int DEFAULT_SEARCH_LIMIT = 10;
    
    @RequestMapping(value = "suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    Page<Report> suggest(@RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) Integer limit,
            HttpSession session, Model model) {
        if (query != null) {
            if (limit == null || limit <= 0) {
                limit = DEFAULT_SUGGEST_LIMIT;
            }            
            return solrService.search(query);
        } else {
            return new PageImpl<Report>(new ArrayList<Report>(0));
        }
    }
}
