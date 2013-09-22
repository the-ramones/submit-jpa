package sp.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sp.model.Report;
import sp.repository.ReportRepository;

/**
 *
 * @author Paul Kulitski
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Inject
    private ReportRepository reportRepository;

    @Override
    //@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)//Isolation.SERIALIZABLE)
    @Transactional
    public Report addReport(Report report) {
        return reportRepository.saveReport(report);
    }

    /**
     * Cache with condition of report's date (as report cannot be added post
     * factum)
     *
     * @param performer
     * @return
     */
    @Override
    //@Cacheable(value = "sp.model.Report", key = "#performer")
    //@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Transactional
    public List<Report> getReports(String performer) {
        return reportRepository.getReportsByPerformer(performer);

    }

    @Override
    //@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Transactional
    public Report getReportById(Long id) {
        return reportRepository.getReportById(id);
    }

    @Override
    //@Transactional(propagation = Propagation.SUPPORTED, readOnly = true)
    @Transactional
    public List<String> getPerformers() {
        return reportRepository.getPerformers();
    }

    @Override
    @Transactional
    public List<Report> getReports(String performer, Date startDate, Date endDate) {
        return reportRepository.getReports(performer, startDate, endDate);
    }

    @Override
    @Transactional
    public List<Report> getReports(Date startDate, Date endDate) {
        return reportRepository.getReports(startDate, endDate);
    }

    @Override
    @Transactional
    public List<Report> getReports(Set<Long> ids) {
        return reportRepository.getReports(ids);
    }

    @Override
    public Boolean hasReport(Long id) {
        return reportRepository.hasReport(id);
    }

    @Override
    public Long[] hasReports(Long[] ids) {
        return reportRepository.hasReports(ids);
    }

    @Override
    @Transactional
    public void removeReport(Long id) {
        reportRepository.removeReport(id);
    }

    @Override
    @Transactional
    public void updateReport(Report report) {
        reportRepository.updateReport(report);
    }
}
