package sp.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sp.model.Op;
import sp.model.Register;
import sp.model.RegisterId;
import sp.model.Report;
import sp.model.User;
import sp.repository.OpRepository;
import sp.repository.RegisterRepository;
import sp.repository.ReportRepository;
import sp.repository.UserRepository;

/**
 *
 * @author Paul Kulitski
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Inject
    private ReportRepository reportRepository;
    @Inject
    private RegisterRepository registerRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private OpRepository opRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Report addReport(Report report) {
        Op op = opRepository.getOpByTitle("INSERT").get(0);
        User user = userRepository.getUserById(1);

        RegisterId registerId = new RegisterId(registerRepository.getLastId() + 1,
                user.getId(), op.getId());
        Register register = new Register();
        register.setId(registerId);
        register.setOp(op);
        register.setUser(user);
        registerRepository.saveRegister(register);

        registerRepository.saveRegister(register);
        return reportRepository.saveReport(report);
    }

    /**
     * Cache with condition
     *
     * @param performer
     * @return
     */
    @Override
    //@Cacheable(value = "sp.model.Report", key = "#performer")    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Report> getReports(String performer) {
        return reportRepository.getReportsByPerformer(performer);

    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Report getReportById(Long id) {
        return reportRepository.getReportById(id);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<String> getPerformers() {
        return reportRepository.getPerformers();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Report> getReports(String performer, Date startDate, Date endDate) {
        return reportRepository.getReports(performer, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Report> getReports(Date startDate, Date endDate) {
        return reportRepository.getReports(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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
    @Transactional(rollbackFor = Exception.class)
    public void removeReport(Long id) {
        Op op = opRepository.getOpByTitle("DELETE").get(0);
        User user = userRepository.getUserById(1);

        RegisterId registerId = new RegisterId(registerRepository.getLastId() + 1,
                user.getId(), op.getId());
        Register register = new Register();
        register.setId(registerId);
        register.setOp(op);
        register.setUser(user);
        registerRepository.saveRegister(register);

        reportRepository.removeReport(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReport(Report report) {
        Op op = opRepository.getOpByTitle("UPDATE").get(0);
        User user = userRepository.getUserById(1);

        RegisterId registerId = new RegisterId(registerRepository.getLastId() + 1,
                user.getId(), op.getId());
        Register register = new Register();
        register.setId(registerId);
        register.setOp(op);
        register.setUser(user);
        registerRepository.saveRegister(register);

        reportRepository.updateReport(report);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Report> getAllReports() {
        return reportRepository.getAllReports();
    }
}
