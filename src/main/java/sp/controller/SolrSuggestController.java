package sp.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import sp.util.SpPageable;

/**
 * Solr-based full text search Reports! controller.
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping("suggest-solr")
@SessionAttributes("solrPager")
public class SolrSuggestController {

    @Inject
    SolrService solrService;
    private static final int HASH_CLOUD_COUNT = 12;
    protected final static Logger logger = LoggerFactory.getLogger(SolrSuggestController.class);

    @ModelAttribute("solrPager")
    public SpPageable populatePage() {
        return new SpPageable();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public synchronized String setupForm(SessionStatus status, Model model) {
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
    public synchronized @ResponseBody
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
    public synchronized @ResponseBody
    Page<Report> search(@RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam("p") String p,
            @ModelAttribute("solrPager") SpPageable pager,
            HttpSession session, Model model) {
        System.out.println("IN CONTROLLER SEARCH");
        Page<Report> result = null;
        boolean reject = false;
        if (limit == null || limit <= 0) {
            limit = DEFAULT_SEARCH_LIMIT;
        }
        boolean newSearch = pager.getTotalElements() == 0 ? true : (p == null) || p.equals("");
        if (newSearch) {
            /*
             * A new search
             */
            if (query != null) {
                Page resultsPage = solrService.search(query, limit);
                pager.setPage(0);
                pager.setPageSize(limit);
                pager.setTotalElements(resultsPage.getTotalElements());
                model.addAttribute(pager);
                result = resultsPage;
            } else {
                reject = true;
            }
        } else {
            /*
             * Query for page
             */
            if (p.equals("next")) {
                if (pager.hasNext()) {
                    pager.next();
                    result = solrService.search(query, pager.getPageNumber(), pager.getPageSize());
                } else {
                    reject = true;
                }
            } else if (p.equals("prev")) {
                if (pager.hasPrevious()) {
                    pager.previousOrFirst();
                    result = solrService.search(query, pager.getPageNumber(), pager.getPageSize());
                } else {
                    reject = true;
                }
            } else {
                try {
                    Integer intPage = Integer.valueOf(p);
                    //TODO: fix validation                                        
                    if ((intPage > 0) && (intPage <= pager.getTotalPages())) {
                        pager.setPage(intPage);
                        result = solrService.search(query, pager.getPageNumber(), pager.getPageSize());
                    }
                } catch (NumberFormatException ex) {
                    logger.warn("Cannot parse page number from request parameter 'page'={}", p);
                    reject = true;
                }
            }

        }
        if (!reject) {
            return result;
        } else {
            return new PageImpl<Report>(new ArrayList<Report>(0));
        }
    }
    private static final int DEFAULT_SUGGEST_LIMIT = 8;
    private static final int DEFAULT_SEARCH_LIMIT = 10;

    @RequestMapping(value = "suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public synchronized @ResponseBody
    Page<Report> suggest(@RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) Integer limit,
            HttpSession session, Model model) {
        logger.error("in controller SUGGEST");
        if (query != null) {
            if (limit == null || limit <= 0) {
                limit = DEFAULT_SUGGEST_LIMIT;
            }
            return solrService.suggest(query, limit);
        } else {
            return new PageImpl<Report>(new ArrayList<Report>(0));
        }
    }
}
