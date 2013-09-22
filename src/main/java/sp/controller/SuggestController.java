package sp.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import sp.service.ReportService;

/**
 * Controller for Suggest features of application
 *
 * @author Paul Kulitski
 * @see Controller
 */
@Controller
@RequestMapping("/report/ajax/suggest")
public class SuggestController {

    private static final Logger logger = LoggerFactory.getLogger(SuggestController.class);
    
    @Inject
    ReportService reportService;
    
    @InitBinder
    public void initReportDateEditor(WebDataBinder binder, Locale locale) {
        DateFormat df;
        df = sp.util.SpDateFormatFactory.getDateFormat("dd MMM yyyy", locale, null);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }
    
    
}
