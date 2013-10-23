package sp.controller;

import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import sp.model.Report;
import sp.service.ReportService;

/**
 * Report controller test
 *
 * @author Paul Kulitski
 */
public class ReportControllerTest {

    public ReportControllerTest() {
    }

    /**
     * Test of detail method, of class ReportController.
     */
    @Test
    public void testSetupDetailForm() {
        Model model = new ExtendedModelMap();
        ReportController instance = new ReportController();
        String result = instance.setupDetailForm(model);
        assertEquals(true, model.containsAttribute("view"));
        assertEquals("byid", model.asMap().get("view"));
        assertEquals("byid", result);
    }

    /**
     * Test of detail method, of class ReportController.
     */
    //@Test
    public void testDetailBuId() {
        Model model = new ExtendedModelMap();
        Long id = 1L;

        Report expReport = new Report();
        expReport.setId(id);
        expReport.setStartDate(new Date());
        expReport.setEndDate(new Date());
        expReport.setPerformer("Michael Douglas");
        expReport.setActivity("swimming");

        ReportService reportService = mock(ReportService.class);
        when(reportService.getReportById(id)).thenReturn(expReport);

        ReportController reportController = new ReportController(reportService);
        assertSame(expReport, model.asMap().get("report"));
    }
}