package sp.repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
    @PersistenceContext(unitName = "reportPU")
    private EntityManager entityManager;

    @Override
    public Report getReportById(Long id) {
        if (id != null) {
            return entityManager.find(Report.class, id);
        } else {
            return null;
        }
    }

    @Override
    public Report saveReport(Report report) {
        if (report != null) {
            entityManager.persist(report);
            entityManager.flush();
            return report;
        } else {
            return null;
        }
    }

    @Override
    public List<Report> getReportsByPerformer(String performer) {
        if (performer != null) {
            TypedQuery<Report> query =
                    entityManager.createNamedQuery("Report.getReportsByPerformer", Report.class);
            query.setParameter("performer", performer);
            return query.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public List<String> getPerformers() {
        TypedQuery<String> query =
                entityManager.createNamedQuery("Report.getPerformers", String.class);
        return query.getResultList();
    }

    @Override
    public List<Report> getReports(String performer, Date startDate, Date endDate) {
        if (performer != null) {
            TypedQuery<Report> query =
                    entityManager.createNamedQuery("Report.getReports", Report.class);
            query.setParameter("performer", performer);
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            } else {
                query.setParameter("startDate", new Date(0));
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            } else {
                query.setParameter("endDate", new Date());
            }
            return query.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public List<Report> getReports(Date startDate, Date endDate) {
        TypedQuery<Report> query =
                entityManager.createNamedQuery("Report.getReportsByPeriod", Report.class);
        if (startDate != null) {
            query.setParameter("startDate", startDate);
        } else {
            query.setParameter("startDate", new Date(0));
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        } else {
            query.setParameter("endDate", new Date());
        }
        return query.getResultList();
    }

    @Override
    public List<Report> getReports(Set<Long> ids) {
        if (ids != null) {
            TypedQuery<Report> query =
                    entityManager.createNamedQuery("Report.getReportsByIds", Report.class);
            query.setParameter("ids", ids);
            return query.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public Boolean hasReport(Long id) {
        if (id != null) {
            TypedQuery<Report> query =
                    entityManager.createNamedQuery("Report.hasReport", Report.class);
            query.setParameter("id", id);
            return !query.getResultList().isEmpty();
        } else {
            return null;
        }
    }

    @Override
    public Long[] hasReports(Long[] ids) {
        if (ids != null) {
            TypedQuery<Long> query =
                    entityManager.createNamedQuery("Report.hasReports", Long.class);
            query.setParameter("ids", Arrays.asList(ids));
            Long count = query.getSingleResult();
            if (count == ids.length) {
                return ids;
            } else {
                Long[] consistIds = new Long[ids.length];
                for (Long id : ids) {
                    if (hasReport(id)) {
                        consistIds[consistIds.length] = id;
                    }
                }
                return consistIds;
            }
        } else {
            return null;
        }
    }

    @Override
    public void removeReport(Long id) {
        if (id != null) {
            entityManager.remove(getReportById(id));
        }
    }

    @Override
    public void updateReport(Report report) {
        if (report != null) {
            Report persistedReport = getReportById(report.getId());
            if (persistedReport != null) {
                persistedReport.setStartDate(report.getStartDate());
                persistedReport.setEndDate(report.getEndDate());
                persistedReport.setPerformer(report.getPerformer());
                persistedReport.setActivity(report.getActivity());
            }
        }
    }

    @Override
    public List<Report> getAllReports() {
        TypedQuery<Report> query =
                entityManager.createNamedQuery("Report.getAllReports", Report.class);
        return query.getResultList();
    }
}
