package sp.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import sp.model.Report;

/**
 * Report service interface
 *
 * @author Paul Kulitski
 */
public interface ReportService {

    public Report addReport(Report report);

    public List<Report> getReports(String performer);

    public List<Report> getReports(String performer, Date startDate, Date endDate);

    public List<Report> getReports(Date startDate, Date endDate);

    public List<Report> getReports(Set<Long> ids);

    public Report getReportById(Long id);

    public List<String> getPerformers();

    public Boolean hasReport(Long id);

    public Long[] hasReports(Long[] ids);

    public void removeReport(Long id);

    public void updateReport(Report report);

    public List<Report> getAllReports();
}
