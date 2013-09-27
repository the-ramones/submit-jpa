package sp.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import sp.model.Report;
import sp.model.Report_;
import sp.model.ajax.Prompt;

/**
 * Implementation of {@link SuggestRepository}. Limited for search only for
 * 'performer' and 'activity' fields so far.
 *
 * @author Paul Kulitski
 */
@Repository
public class SuggestRepositoryImpl implements SuggestRepository {

    private static final Logger logger = LoggerFactory.getLogger(SuggestRepositoryImpl.class);
    @PersistenceContext
    EntityManager em;

    private String normalizeQuery(String query) {
        StringBuilder sb = new StringBuilder(query.length() + 2);
        sb.append('%').append(query.trim().toLowerCase()).append('%');
        return sb.toString();
    }

    @Override
    public Long getAllCount(String query) {
        String nQuery = normalizeQuery(query);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Report> report = cq.from(Report.class);
        cq.select(report.get(Report_.id))
                .where(cb.or(
                cb.like(report.get(Report_.performer), nQuery),
                cb.like(report.get(Report_.activity), nQuery)));

        cq.select(cb.count(report.get(Report_.id)));

        TypedQuery<Long> resultQuery = em.createQuery(cq);
        return resultQuery.getSingleResult();
    }

    @Override
    public List<Long> getIdsByQuery(String query) {

        String nQuery = normalizeQuery(query);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Report> report = cq.from(Report.class);

        cq.select(report.get(Report_.id))
                .where(cb.or(
                cb.like(cb.lower(report.get(Report_.performer)), nQuery),
                cb.like(cb.lower(report.get(Report_.activity)), nQuery)));

        cq.orderBy(cb.desc(report.get(Report_.startDate)));

        TypedQuery<Long> idQuery = em.createQuery(cq);

        return (List<Long>) idQuery.getResultList();
    }

    @Override
    public List<Long> getIdsByQuery(String query, Long limit) {
        String nQuery = normalizeQuery(query);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Report> report = cq.from(Report.class);

        cq.select(report.get(Report_.id))
                .where(cb.or(
                cb.like(cb.lower(report.get(Report_.performer)), nQuery),
                cb.like(cb.lower(report.get(Report_.activity)), nQuery)));

        cq.orderBy(cb.desc(report.get(Report_.startDate)));

        TypedQuery<Long> idQuery = em.createQuery(cq);

        if (limit > 0) {
            return (List<Long>) idQuery.setMaxResults(limit.intValue()).getResultList();
        } else {
            return (List<Long>) idQuery.getResultList();
        }
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
        logger.error("Normalized query: {}", nQuery);
        /*
         * Create a criteria builder and criteria query. Type-safe
         */
        logger.error("Entity manager: {}", em);
        logger.error("Entity manager properties: {}", em.getProperties());
        logger.error("Entity manager properties: {}", em.isOpen());
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
        Predicate predicatePerformer;
        predicatePerformer = cb.like(cb.lower(report.get(Report_.performer)), nQuery);
        Predicate predicateActivity;
        predicateActivity = cb.like(cb.lower(report.get(Report_.activity)), nQuery);

        cq.select(report)
                .where(cb.or(predicatePerformer, predicateActivity))
                .orderBy(cb.asc(report.get(Report_.performer)));

        cq.orderBy(cb.desc(report.get(Report_.startDate)));

        TypedQuery<Report> reportQuery = em.createQuery(cq);
        logger.error("Report query: {}", reportQuery.toString());
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
        Predicate predicatePerformer;
        predicatePerformer = cb.like(cb.lower(report.get(Report_.performer)), nQuery);
        Predicate predicateActivity;
        predicateActivity = cb.like(cb.lower(report.get(Report_.activity)), nQuery);

        cq.select(report)
                .where(cb.or(predicatePerformer, predicateActivity))
                .orderBy(cb.asc(report.get(Report_.performer)));

        cq.orderBy(cb.desc(report.get(Report_.startDate)));

        TypedQuery<Report> reportQuery = em.createQuery(cq);

        if (limit > 0) {
            return reportQuery.setMaxResults(limit.intValue()).getResultList();
        } else {
            return reportQuery.getResultList();
        }
    }

    @Override
    public List<Prompt> getPrompts(String query, Long limit) {

        String nQuery = normalizeQuery(query);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Prompt> cq = cb.createQuery(Prompt.class);

        Root<Report> report = cq.from(Report.class);

        Selection prompt = cb.construct(Prompt.class,
                report.get(Report_.performer),
                report.get(Report_.activity),
                report.get(Report_.id));

        cq.select(prompt).where(cb.or(
                cb.like(cb.lower(report.get(Report_.performer)), nQuery),
                cb.like(cb.lower(report.get(Report_.activity)), nQuery)));

        cq.orderBy(cb.desc(report.get(Report_.startDate)));

        TypedQuery<Prompt> resultQuery = em.createQuery(cq);
        if (limit > 0) {
            resultQuery.setMaxResults(limit.intValue());
        }
        return resultQuery.getResultList();
    }
}
