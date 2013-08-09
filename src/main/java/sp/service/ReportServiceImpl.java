package sp.service;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sp.model.Report;
import sp.repository.ReportRepository;

/**
 *
 * @author the-ramones
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Inject
    private ReportRepository reportRepository;

    @Override
    //@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
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
    public List<Report> getReports(String performer) {
        return reportRepository.getReportsByPerformer(performer);

    }

    @Override
    //@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Report getReportById(Long id) {
        return reportRepository.getReportById(id);
    }
}
