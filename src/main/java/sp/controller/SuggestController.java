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
        return new SpLazyPager(0);
    }

    @InitBinder
    public void initReportDateEditor(WebDataBinder binder, Locale locale) {
        DateFormat df;
        df = sp.util.SpDateFormatFactory.getDateFormat("dd MMM yyyy", locale, null);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Report>> getReprotsByQuery(
            @RequestParam("query") String query,
            @ModelAttribute("suggest-pager") SpLazyPager pager,
            @RequestParam(value = "limit", required = false) Long limit,
            @RequestParam(value = "p", required = false) String page,
            @RequestParam(value = "useIndex", required = false) boolean useIndex,
            Model model) {
        logger.debug("IN GET REPORS BY QUERY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        List<Report> body;

        if (useIndex) {
            /*
             * Search using index
             */
            if (pager.getSourceCount() == 0) {
                List<Long> ids = suggestService.getIdsByQuery(query, null);
                pager.setSourceCount(ids.size());
                if (limit != null) {
                    if (limit > 0) {
                        pager.setPageSize(limit.intValue());
                    } else {
                        pager.setPageSize(PAGINATION_THRESHOLD);
                    }
                } else {
                    pager.setPageSize(PAGINATION_THRESHOLD);
                }
                pager.setPage(0);
                Set<Long> idsSet = new HashSet();
                idsSet.addAll(ids);
                body = reportService.getReports(idsSet);
            } else {
            }
        } else {
            /*
             * Direct search in database
             */
            if (pager.getSourceCount() == 0) {
                body = suggestService.getReportsByQuery(query, limit);
                pager.setSourceCount(suggestService.getAllCount(query).intValue());
                if (limit != null) {
                    if (limit > 0) {
                        pager.setPageSize(limit.intValue());
                    } else {
                        pager.setPageSize(PAGINATION_THRESHOLD);
                    }
                } else {
                    pager.setPageSize(PAGINATION_THRESHOLD);
                }
                pager.setPage(0);
            } else {
                if (page != null) {
                    boolean correct = pager.setPage(page);
                    if (correct) {
                        body = suggestService.getReportsByQuery(
                                query, limit, Long.valueOf(pager.getPageOffset()));
                    } else {
                        body = new ArrayList<Report>(0);
                    }
                } else {
                    /*
                     * New search
                     * TODO: manual cache handling
                     */
                    body = suggestService.getReportsByQuery(query, limit);
                    pager.setSourceCount(suggestService.getAllCount(query).intValue());
                    if (limit != null) {
                        if (limit > 0) {
                            pager.setPageSize(limit.intValue());
                        }
                    } else {
                        pager.setPageSize(MAX_ON_PAGER);
                    }
                    pager.setPage(0);
                }
            }
        }

        logger.error("QUERY: {}", query);
        logger.error("pager: {}", pager);
        logger.error("page: {}", page);
        logger.error("useIndex: {}", useIndex);
        logger.error("limit: {}", limit);
        logger.error("BODY: {}", body);

        ResponseEntity<List<Report>> re = new ResponseEntity<List<Report>>(body, headers, HttpStatus.OK);
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
