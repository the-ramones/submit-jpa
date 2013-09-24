package sp.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import sp.model.Report;
import sp.model.metamodel.Report_;

/**
 * Implementaion of {@link SuggestRepository}. Limited for search only for
 * 'performer' and 'activity' fields so far.
 *
 * @author Paul Kulitski
 */
@Repository
public class SuggestRepositoryImpl implements SuggestRepository {

    @PersistenceContext
    EntityManager em;
    private double PROBABILTY_THRESHOLD = 0.5;

    private String normalizeQuery(String query) {
        StringBuilder sb = new StringBuilder(query.length() + 2);
        sb.append('%').append(query.trim().toLowerCase()).append('%');
        return sb.toString();
    }

    @Override
    public Long[] getIdsByQuery(String query) {

        String nQuery = normalizeQuery(query);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Report> report = cq.from(Report.class);

        cq.select(report.get(Report_.id))
                .where(cb.or(
                cb.like(cb.lower(report.get(Report_.performer)), nQuery),
                cb.like(cb.lower(report.get(Report_.activity)), nQuery)));

        TypedQuery<Long> idQuery = em.createQuery(cq);

        return (Long[]) idQuery.getResultList().toArray();
    }

    @Override
    public Long[] getIdsByQuery(String query, Long limit) {
        String nQuery = normalizeQuery(query);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Report> report = cq.from(Report.class);

        cq.select(report.get(Report_.id))
                .where(cb.or(
                cb.like(cb.lower(report.get(Report_.performer)), nQuery),
                cb.like(cb.lower(report.get(Report_.activity)), nQuery)));

        TypedQuery<Long> idQuery = em.createQuery(cq);

        return (Long[]) idQuery.getResultList().toArray();
    }

    /**
     * Returns report by matching query string as a substring of a performer
     * field in the report objects.
     *
     * @param query part of performer field
     * @return list of report passed against the query
     */
    @Override
    public List<Report> getReportsByQuery(String query) {
        /*
         * Normalize query string
         */
        String nQuery = normalizeQuery(query);
        /*
         * Create a criteria builder and criteria query. Type-safe
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        /*
         * Create a root element of a query. Additionally, can make joins, 
         * multiple root elements
         */
        Root<Report> report = cq.from(Report.class);
        /*
         * Dynamic metamodel
         */
        //EntityType<Report> Reports_ = report.getModel();

        Predicate predicatePerformer;
        predicatePerformer = cb.like(cb.lower(report.get(Report_.performer)), nQuery);
        Predicate predicateActivity;
        predicateActivity = cb.like(cb.lower(report.get(Report_.activity)), nQuery);

        cq.select(report)
                .where(cb.or(predicatePerformer, predicateActivity))
                .orderBy(cb.asc(report.get(Report_.performer)));

        TypedQuery<Report> reportQuery = em.createQuery(cq);

        return reportQuery.getResultList();
    }

    @Override
    public List<Report> getReportsByQuery(String query, Long limit) {
        /*
         * Normalize query string
         */
        String nQuery = normalizeQuery(query);
        /*
         * Create a criteria builder and criteria query. Type-safe
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Report> cq = cb.createQuery(Report.class);
        /*
         * Create a root element of a query. Additionally, can make joins, 
         * multiple root elements
         */
        Root<Report> report = cq.from(Report.class);
        /*
         * Dynamic metamodel
         */
        //EntityType<Report> Reports_ = report.getModel();

        Predicate predicatePerformer;
        predicatePerformer = cb.like(cb.lower(report.get(Report_.performer)), nQuery);
        Predicate predicateActivity;
        predicateActivity = cb.like(cb.lower(report.get(Report_.activity)), nQuery);

        cq.select(report)
                .where(cb.or(predicatePerformer, predicateActivity))
                .orderBy(cb.asc(report.get(Report_.performer)));

        TypedQuery<Report> reportQuery = em.createQuery(cq);

        return reportQuery.getResultList();
    }
}
