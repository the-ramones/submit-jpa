package sp.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import sp.model.Report;

/**
 * Implementation of {@link ReportRepository}
 *
 * @author Paul Kulitski
 * @see ReportRepository
 * @see Repository
 */
@Repository
public class ReportRepositoryImpl implements ReportRepository {

    /*
     * Spring JPA injection, thread-safe? : yes
     * Although EntityManagerFactory instances are thread-safe, EntityManager 
     * instances are not. The injected JPA EntityManager behaves like an
     * EntityManager fetched from an application server's JNDI environment,
     * as defined by the JPA specification. It delegates all calls to the 
     * current transactional EntityManager, if any; otherwise, it falls back to
     * a newly created EntityManager per operation, in effect making its usage 
     * thread-safe. -- From Spring Reference
     * 
     * http://stackoverflow.com/questions/1310087/injecting-entitymanager-vs-entitymanagerfactory
     * 
     * "I think part of my problem is I am using
     * 
     * @PersistenceContext(unitName = "unit",
     * type = PersistenceContextType.EXTENDED)
     * 
     * If you use PersistenceContextType.EXTENDED, keep in mind you have to, if I 
     * understand correctly, manually close the transaction. See this thread for 
     * more information."
     * 
     * "I found that setting the @Repository Spring annotation on our DAOs and 
     * having EntityManager managed by Spring and injected by @PersistenceContext 
     * annotation is the most convenient way to get everything working fluently.
     * You benefit from the thread safety of the shared EntityManager and 
     * exception translation. By default, the shared EntityManager will manage 
     * transactions if you combine several DAOs from a manager for instance. 
     * In the end you'll find that your DAOs will become anemic."
     */
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Report getReportById(Long id) {
        return entityManager.find(Report.class, id);
    }

    @Override
    public Report saveReport(Report report) {
        entityManager.persist(report);
        entityManager.flush();
        return report;
    }

    @Override
    public List<Report> getReportsByPerformer(String performer) {
        TypedQuery<Report> query =
                entityManager.createNamedQuery("Report.getReportsByPerformer", Report.class);
        query.setParameter("performer", performer);
        return query.getResultList();
    }

    @Override
    public List<String> getPerformers() {
        TypedQuery<String> query =
                entityManager.createNamedQuery("Report.getPerformers", String.class);
        return query.getResultList();
    }

    @Override
    public List<Report> getReports(String performer, Date startDate, Date endDate) {
        TypedQuery<Report> query =
                entityManager.createNamedQuery("Report.getReports", Report.class);
        query.setParameter("performer", performer);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    @Override
    public List<Report> getReports(Date startDate, Date endDate) {
        TypedQuery<Report> query =
                entityManager.createNamedQuery("Report.getReportsByPeriod", Report.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    @Override
    public List<Report> getReports(Set<Long> ids) {
        TypedQuery<Report> query = 
                entityManager.createNamedQuery("Report.getReportsByIds", Report.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    @Override
    public Boolean hasReport(Long id) {
        TypedQuery<Report> query =
                entityManager.createNamedQuery("Report.hasReport", Report.class);
        query.setParameter("id", id);
        return !query.getResultList().isEmpty();
    }
}
