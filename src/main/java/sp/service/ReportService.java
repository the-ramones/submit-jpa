package sp.service;

import java.util.Date;
import java.util.List;
import sp.model.Report;

/**
 *
 * @author the-ramones
 */
public interface ReportService {

    public Report addReport(Report report);

    public List<Report> getReports(String performer);
    
    public List<Report> getReports(String performer, Date startDate, Date endDate);

    public Report getReportById(Long id);
    
    public List<String> getPerformers();
}
