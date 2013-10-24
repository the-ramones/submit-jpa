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
    @PersistenceContext(unitName = "reportPU")
    EntityManager em;

    private String normalizeQuery(String query) {
        String escapeQuery = query.replaceAll("[?%:]", "");
        StringBuilder sb = new StringBuilder(query.length() + 2);
        sb.append('%').append(escapeQuery.trim().toLowerCase()).append('%');
        return sb.toString();
    }

    @Override
    public Long getAllCount(String query) {
        if (query != null) {
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
        } else {
            return 0L;
        }
    }

    @Override
    public List<Long> getIdsByQuery(String query) {
        return getIdsByQuery(query, null, null);
    }

    @Override
    public List<Long> getIdsByQuery(String query, Long limit) {
        return getIdsByQuery(query, limit, null);
    }

    @Override
    public List<Long> getIdsByQuery(String query, Long limit, Long offset) {
        if (query != null) {
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

            if (limit != null) {
                if (limit > 0) {
                    idQuery.setMaxResults(limit.intValue());
                }
            }
            if (offset != null) {
                if (offset > 0) {
                    idQuery.setFirstResult(offset.intValue());
                }
            }
            return idQuery.getResultList();
        }
        return null;
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
        return getReportsByQuery(query, null, null);
    }

    @Override
    public List<Report> getReportsByQuery(String query, Long limit) {
        return getReportsByQuery(query, limit, null);
    }

    @Override
    public List<Report> getReportsByQuery(String query, Long limit, Long offset) {
        if (query != null) {
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

            if (limit != null) {
                if (limit > 0) {
                    reportQuery.setMaxResults(limit.intValue());
                }
            }
            if (offset != null) {
                if (offset > 0) {
                    reportQuery.setFirstResult(offset.intValue());
                }
            }
            return reportQuery.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public List<Prompt> getPrompts(String query) {
        return getPrompts(query, null);
    }

    @Override
    public List<Prompt> getPrompts(String query, Long limit) {

        if (query != null) {
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
            if ((limit != null) && (limit > 0)) {
                resultQuery.setMaxResults(limit.intValue());
            }
            return resultQuery.getResultList();
        } else {
            return null;
        }
    }

    @Override
    public List<String> getPromptStrings(String query, Long limit) {

        if (query != null) {
            List<String> results;
            String nQuery = normalizeQuery(query);

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cqPerformers = cb.createQuery(String.class);

            Root<Report> report = cqPerformers.from(Report.class);

            cqPerformers.select(report.get(Report_.performer)).where(
                    cb.like(cb.lower(report.get(Report_.performer)), nQuery));

            cqPerformers.groupBy(report.get(Report_.performer));

            TypedQuery<String> resultQuery = em.createQuery(cqPerformers);
            if ((limit != null) && (limit > 0)) {
                resultQuery.setMaxResults(limit.intValue() / 2);
            }
            results = resultQuery.getResultList();
      
            CriteriaBuilder cb2 = em.getCriteriaBuilder();
            CriteriaQuery<String> cqActivity = cb2.createQuery(String.class);
            Root<Report> report2 = cqActivity.from(Report.class);
                        
            cqActivity.select(report2.get(Report_.activity)).where(
                    cb2.like(cb.lower(report2.get(Report_.activity)), nQuery));
            
            cqActivity.groupBy(report2.get(Report_.activity));
            
            TypedQuery<String> resultQuery2 = em.createQuery(cqActivity);
            if ((limit != null) && (limit > 0)) {
                resultQuery2.setMaxResults(limit.intValue() / 2);
            }
            List<String> results2 = resultQuery2.getResultList();  
            
            results.addAll(results2);
            
            return results;
        } else {
            return null;
        }
    }
}
