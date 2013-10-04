package sp.controller;

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
import sp.model.Report;
import sp.model.ajax.Prompt;
import sp.model.ajax.ResultPager;
import sp.service.ReportService;
import sp.service.SuggestService;
import sp.util.SpLazyPager;

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

    @ModelAttribute("settings")
    public Map<String, String> populateReferenceData() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("maxOnPager", String.valueOf(MAX_ON_PAGER));
        settings.put("pagerThreshold", String.valueOf(PAGINATION_THRESHOLD));
        return settings;
    }

    @ModelAttribute("suggest-pager")
    public SpLazyPager populatePager() {
        SpLazyPager pager = new SpLazyPager(0);
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

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<ResultPager> getReprotsByQuery(
            @RequestParam("query") String query,
            @ModelAttribute("suggest-pager") SpLazyPager pager,
            @RequestParam(value = "limit", required = false) int limit,
            @RequestParam(value = "p", required = false) String page,
            @RequestParam(value = "useIndex", required = false) boolean useIndex,
            @RequestParam(value = "recent", required = false) boolean recent,
            Model model) {
        logger.debug("IN GET REPORS BY QUERY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        ResultPager body = new ResultPager();

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
                List<Long> ids = suggestService.getIdsByQuery(query, Long.valueOf(limit));
                pager.setSourceCount(suggestService.getAllCount(query).intValue());
                pager.setPageSize(limit);
                pager.setPage(0);
                Set<Long> idsSet = new HashSet();
                idsSet.addAll(ids);
                body.setPager(pager);
                body.setResults(reportService.getReports(idsSet));
            } else {
                boolean correct = pager.setPage(page);
                if (correct) {
                    List<Long> ids = suggestService.getIdsByQuery(query,
                            Long.valueOf(limit), Long.valueOf(pager.getPageOffset()));
                    Set<Long> idsSet = new HashSet();
                    idsSet.addAll(ids);
                    body.setPager(pager);
                    body.setResults(reportService.getReports(idsSet));
                } else {
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

    @RequestMapping(value = "ids", method = RequestMethod.GET)
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

    @RequestMapping(value = "count", method = RequestMethod.GET)
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
            @RequestParam(value = "limit", required = false) Long limit,
            Model model) {
        logger.debug("IN GET PROMPTS");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        ResponseEntity<List<Prompt>> re;
        if (limit > 0) {
            re = new ResponseEntity<List<Prompt>>(
                    suggestService.getPrompts(query, limit), headers, HttpStatus.OK);
        } else {
            re = new ResponseEntity<List<Prompt>>(
                    suggestService.getPrompts(query, -1L), headers, HttpStatus.OK);
        }
        return re;
    }
}
