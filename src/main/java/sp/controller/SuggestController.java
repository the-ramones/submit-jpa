package sp.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sp.model.Report;
import sp.model.ajax.Prompt;
import sp.service.SuggestService;

/**
 * Controller for Suggest features of application
 *
 * @author Paul Kulitski
 * @see Controller
 */
@Controller 
@RequestMapping("/report/suggest/ajax")
public class SuggestController {

    private static final Logger logger = LoggerFactory.getLogger(SuggestController.class);
    @Inject
    SuggestService suggestService;

    @InitBinder
    public void initReportDateEditor(WebDataBinder binder, Locale locale) {
        DateFormat df;
        df = sp.util.SpDateFormatFactory.getDateFormat("dd MMM yyyy", locale, null);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Report>> getReprotsByQuery(
            @RequestParam("query") String query,
            @RequestParam(value = "limit", required = false) Long limit,
            Model model) {
        logger.debug("IN GET REPORS BY QUERY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");
        List<Report> body;

        logger.error("QUERY:" + query);

        if (limit != null) {
            body = suggestService.getReportsByQuery(query, limit);
        } else {
            body = suggestService.getReportsByQuery(query);
        }
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
