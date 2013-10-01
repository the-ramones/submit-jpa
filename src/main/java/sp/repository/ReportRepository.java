package sp.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;
import sp.model.Report;

/**
 * Report repository interface
 *
 * @author Paul Kulitski
 */
public interface ReportRepository {

    public Report getReportById(Long id);

    public List<Report> getReportsByPerformer(String performer);

    public List<Report> getReports(String performer, Date startDate, Date endDate);
    
    public List<Report> getReports(Date startDate, Date endDate);
    
    public List<Report> getReports(Set<Long> ids);
    
    public Report saveReport(Report report);

    public List<String> getPerformers();

    public Boolean hasReport(Long id);

    public Long[] hasReports(Long[] ids);

    public void removeReport(Long id);

    public void updateReport(Report report);
    
    public List<Report> getAllReports();
}
