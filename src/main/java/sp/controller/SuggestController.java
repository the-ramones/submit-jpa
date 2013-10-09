package sp.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.filter.CharacterEncodingFilter;
import sp.model.Report;
import sp.model.ajax.Prompt;
import sp.model.ajax.ResultPager;
import sp.service.ReportService;
import sp.service.SuggestService;
import sp.suggest.SuggestIndexSearcher;
import sp.util.SpLightPager;

/**
 * Controller for Suggest features of application
 *
 * @author Paul Kulitski
 * @see Controller
 */
@Controller
@RequestMapping("/report/suggest/ajax")
@SessionAttributes("suggest-pager")
public class SuggestController {

    private static final Logger logger = LoggerFactory.getLogger(SuggestController.class);
    @Inject
    SuggestService suggestService;
    @Inject
    ReportService reportService;
    @Value("${pagination.maxonpager}")
    private int MAX_ON_PAGER;
    @Value("${pagination.threshold}")
    private int PAGINATION_THRESHOLD;
    @Value("${prompt.limit.default}")
    private int DEFAULT_LIMIT;

    @ModelAttribute("settings")
    public Map<String, String> populateReferenceData() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("maxOnPager", String.valueOf(MAX_ON_PAGER));
        settings.put("pagerThreshold", String.valueOf(PAGINATION_THRESHOLD));
        return settings;
    }

    @ModelAttribute("suggest-pager")
    public SpLightPager populatePager() {
        SpLightPager pager = new SpLightPager(0);
        pager.setMaxOnPager(MAX_ON_PAGER);
        pager.setPageSize(PAGINATION_THRESHOLD);
        return pager;
    }

    @InitBinder
    public void initReportDateEditor(WebDataBinder binder, Locale locale) {
        DateFormat df;
        df = sp.util.SpDateFormatFactory.getDateFormat("dd MMM yyyy", locale, null);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }

    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ResultPager> getReportsByQuery(
            @RequestParam("query") String query,
            @ModelAttribute("suggest-pager") SpLightPager pager,
            @RequestParam(value = "limit", required = false) int limit,
            @RequestParam(value = "p", required = false) String page,
            @RequestParam(value = "useIndex", required = false) boolean useIndex,
            @RequestParam(value = "recent", required = false) boolean recent,
            Model model, HttpServletRequest request, HttpSession session) {
        logger.debug("IN GET REPORS BY QUERY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        ResultPager body = new ResultPager();

        System.out.println("Request encoding: " + request.getCharacterEncoding());
        System.out.println("Request query: " + request.getParameter("query"));
        System.out.println("QUERY: " + query);
        try {
            String querya = new String(query.getBytes("utf-8"), "utf-8");
            System.out.println("QUERY AFTER: " + querya);
        } catch (UnsupportedEncodingException ex) {
            logger.error("Cannot convert search query because encoding is not supported");
        }

        String queryb = null;
        try {
            queryb = URLDecoder.decode(query, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("Cannot convert search query because encoding is not supported");
        }
        System.out.println("QUERY B: " + queryb);
        logger.error("WATCH Pager: {}", pager);

        /*
         * Contsrain result of the rearch if recend records needed.
         */
        if (recent || limit <= 0) {
            limit = PAGINATION_THRESHOLD;
        }
        /*
         * Decision: paged or new search
         */
        boolean newSearch = pager.getSourceCount() == 0 ? true : page == null;
        if (useIndex) {
            /*
             * Search using index
             */
            if (newSearch) {
                List<Long> ids = indexSearcher.search(query, limit);

                logger.error("!! NEW SEARCH IDS: {}", ids);

                if (!ids.isEmpty()) {
                    pager.setSourceCount(ids.size());
                    logger.error("SIZE OF IDS: {}", ids.size());
                    pager.setPageSize(limit);
                    pager.setPage(0);
                    pager.setIds(ids);
                    body.setPager(pager);

                    Set<Long> idsSet = new HashSet();
                    idsSet.addAll(ids.subList(0, (limit) > ids.size() ? ids.size() : limit));
                    body.setResults(reportService.getReports(idsSet));

                    logger.error("NEW SEARCH PAGER: {}", pager);
                } else {
                    body.setResults(new ArrayList<Report>(0));
                }
            } else {
                boolean correct = pager.setPage(page);
                if (correct) {
                    //List<Long> ids = indexSearcher.search(query, limit);
                    List<Long> ids = pager.getIds();

                    logger.error("!!OLD SEARCH IDS BEFORE: {}", ids);
                    logger.error("Pager: offset={}, size={}", pager.getPageOffset(), pager.getPageSize());
                    logger.error("!! IDS: {}", ids);

                    if (!ids.isEmpty()) {

                        logger.error("IN GET PAGER");

                        Set<Long> idsSet = new HashSet();
                        if (pager.getPage() + 1 < pager.getPageCount()) {
                            idsSet.addAll(ids.subList(pager.getPageOffset(),
                                    pager.getPageOffset() + pager.getPageSize()));
                        } else {
                            idsSet.addAll(ids.subList(pager.getPageOffset(),
                                    ids.size() - 1));
                        }

                        body.setPager(pager);
                        body.setResults(reportService.getReports(idsSet));
                    } else {
                        body.setResults(new ArrayList<Report>(0));
                    }
                } else {
                    body.setPager(null);
                    body.setResults(new ArrayList<Report>(0));
                }
            }
        } else {
            /*
             * Direct search in database
             */
            if (newSearch) {
                body.setResults(suggestService.getReportsByQuery(query, Long.valueOf(limit)));
                pager.setSourceCount(suggestService.getAllCount(query).intValue());
                pager.setPageSize(limit);
                pager.setPage(0);
                body.setPager(pager);
            } else {
                boolean correct = pager.setPage(page);
                if (correct) {
                    body.setPager(pager);
                    body.setResults(suggestService.getReportsByQuery(query,
                            Long.valueOf(limit), Long.valueOf(pager.getPageOffset())));
                } else {
                    body.setPager(null);
                    body.setResults(new ArrayList<Report>(0));
                }
            }
        }

        logger.error("QUERY: {}", query);
        logger.error("pager: {}", pager);
        logger.error("page: {}", page);
        logger.error("useIndex: {}", useIndex);
        logger.error("limit: {}", limit);
        logger.error("BODY: {}", body);

        ResponseEntity<ResultPager> re = new ResponseEntity<ResultPager>(body, headers, HttpStatus.OK);
        return re;
    }

    @RequestMapping(value = "ids", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<List<Long>> getIdsByQuery(
            @RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) Long limit,
            Model model) {
        logger.debug("IN GET IDS BY QUERY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        List<Long> body;

        logger.error("QUERY:" + query);

        if (limit != null) {
            body = suggestService.getIdsByQuery(query, limit);
        } else {
            body = suggestService.getIdsByQuery(query);
        }
        ResponseEntity<List<Long>> re = new ResponseEntity<List<Long>>(body, headers, HttpStatus.OK);
        return re;
    }

    @RequestMapping(value = "count", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Long> getCount(@RequestParam("query") String query,
            Model model) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        ResponseEntity<Long> re = new ResponseEntity<Long>(
                suggestService.getAllCount(query), headers, HttpStatus.OK);
        return re;
    }

    @RequestMapping(value = "prompt", method = RequestMethod.GET)
    public ResponseEntity<List<Prompt>> getPrompts(@RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) int limit,
            Model model) {
        logger.debug("IN GET PROMPTS");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        ResponseEntity<List<Prompt>> re;
        if (limit > 0) {
            re = new ResponseEntity<List<Prompt>>(
                    suggestService.getPrompts(query, Long.valueOf(limit)), headers, HttpStatus.OK);
        } else {
            re = new ResponseEntity<List<Prompt>>(
                    suggestService.getPrompts(query, Long.valueOf(DEFAULT_LIMIT)), headers, HttpStatus.OK);
        }
        return re;
    }
    
    @Inject
    SuggestIndexSearcher indexSearcher;

    @RequestMapping(value = "prompt-strings", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<List<String>> getPromptsAsString(@RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) int limit,
            @RequestParam(value = "useIndex", required = false) boolean useIndex,
            Model model) {
        logger.debug("IN GET PROMPTS AS STRINGS");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        ResponseEntity<List<String>> re;
        if (useIndex) {
            if (limit > 0) {
                re = new ResponseEntity<List<String>>(
                        indexSearcher.suggest(query, limit), headers, HttpStatus.OK);
            } else {
                re = new ResponseEntity<List<String>>(
                        indexSearcher.suggest(query), headers, HttpStatus.OK);
            }
        } else {
            if (limit > 0) {
                re = new ResponseEntity<List<String>>(
                        suggestService.getPromptStrings(query, Long.valueOf(limit)), headers, HttpStatus.OK);
            } else {
                re = new ResponseEntity<List<String>>(
                        suggestService.getPromptStrings(query, Long.valueOf(DEFAULT_LIMIT)), headers, HttpStatus.OK);
            }
        }
        return re;
    }
}
