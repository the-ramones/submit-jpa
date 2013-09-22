package sp.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import org.springframework.stereotype.Repository;
import sp.model.Report;

/**
 * Implementaion of {@link SuggestRepository}. Limited for search only for 
 * 'performer' field so far.
 *
 * @author Paul Kulitski
 */
@Repository
public class SuggestRepositoryImpl implements SuggestRepository {

    @PersistenceContext
    EntityManager em;    
    
    private double PROBABILTY_THRESHOLD = 0.5;

    private String normalizeQuery(String query) {
        return query.trim().toLowerCase();
    }

    @Override
    public Long[] getIdsByQuery(String query) {
//        String nQuery = normalizeQuery(query);    
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        EntityType<Report> Report_ = em.getMetamodel().entity(Report.class);
//
//        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//        EntityType<Report> Report_2 = cq.get
//        Root<Report> report = cq.from(Report.class);
//        report.getModel().
//        cq.select(report.get(Report_.getId(Long.class))).where(cb.like(report.get("performer"), nQuery));
        return null;
    }

    @Override
    public Long[] getIdsByQuery(String query, Long limit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        cq.select(report);
        TypedQuery<Report> reportQuery = em.createQuery(cq);
        return reportQuery.getResultList();
    }

    @Override
    public List<Report> getReportsByQuery(String query, Long limit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
