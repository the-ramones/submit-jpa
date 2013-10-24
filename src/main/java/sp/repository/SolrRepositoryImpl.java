package sp.repository;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleTermsQuery;
import org.springframework.data.solr.core.query.result.TermsPage;
import org.springframework.stereotype.Repository;
import sp.model.Report;
import sp.model.searchable.ReportSearchable;
import sp.model.searchable.ReportSearchableField;
import sp.util.service.SolrAdministrator;

/**
 * Basic implementations of {@link SolrRepository}
 *
 * @author Paul Kulitski
 */
//@Lazy
@Repository
@DependsOn("solrAdministrator")
public class SolrRepositoryImpl implements SolrRepository {

    @Inject
    @Named("solrTemplate")
    SolrOperations solrOperations;
    private static final String EDISMAX = "edismax";
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    protected static final Logger logger = LoggerFactory.getLogger(SolrRepositoryImpl.class);

    @Inject
    SolrAdministrator solrAdministrator;
    
    @PostConstruct
    public void postConstruct() {
        solrAdministrator.launchFullDataImport();
    }

    
    @Override
    public Page<Report> searchByPerformer(String query) {
        SimpleQuery q;
        q = new SimpleQuery(new Criteria(
                ReportSearchable.PERFORMER_FIELD).contains(query));
        q.setDefType(EDISMAX);
        q.addSort(new Sort(
                new Sort.Order(Sort.Direction.ASC, ReportSearchable.PERFORMER_FIELD)));

        Page<Report> page;
        page = solrOperations.queryForPage(q, Report.class);
        return page;
    }

    @Override
    public Page<Report> searchByActivity(String query) {
        SimpleQuery q = new SimpleQuery(
                new Criteria(ReportSearchableField.ACTIVITY).contains(query));
        q.setDefType(EDISMAX);
        q.addSort(new Sort(
                new Sort.Order(Sort.Direction.ASC, ReportSearchable.ACTIVITY_FIELD)));
        return solrOperations.queryForPage(q, Report.class);
    }

    @Override
    public Page<Report> search(String query) {
        return search(query, null, null);
    }

    @Override
    public Page<Report> search(String query, int page, int size) {
        String[] terms = normalizeQuery(query);
        SimpleQuery q = new SimpleQuery(
                new Criteria(ReportSearchableField.PERFORMER).contains(terms)
                .or(new Criteria(ReportSearchableField.ACTIVITY).contains(terms)));
        q.setDefType(EDISMAX);
        q.addSort(new Sort(
                new Sort.Order(Sort.Direction.DESC, ReportSearchable.START_DATE_FIELD)));
        if (page >= 0 && size > 0) {
            q.setPageRequest(new PageRequest(page, size));
        } else {
            return new PageImpl(new ArrayList(0));
        }
        return solrOperations.queryForPage(q, Report.class);
    }

    @Override
    public Page<Report> search(String query, Pageable p) {
        return search(query, null, p);
    }

    @Override
    public Page<Report> search(String query, Integer limit) {
        return search(query, limit, null);
    }

    @Override
    public Page<Report> search(String query, Integer limit, Pageable p) {
        String[] terms = normalizeQuery(query);
        SimpleQuery q = new SimpleQuery(
                new Criteria(ReportSearchableField.PERFORMER).contains(terms)
                .or(new Criteria(ReportSearchableField.ACTIVITY).contains(terms)));
        q.setDefType(EDISMAX);
        if (p != null) {
            q.setPageRequest(p);
        } else {
            if (limit != null && limit.compareTo(0) > 0) {
                q.setPageRequest(new PageRequest(0, limit));
            }
        }
        return solrOperations.queryForPage(q, Report.class);
    }

    @Override
    public Page<Report> suggest(String query) {
        return suggest(query, null);
    }

    @Override
    public Page<Report> suggest(String query, Integer limit) {
        String[] terms = normalizeQuery(query);
        SimpleQuery q = new SimpleQuery(
                new Criteria(ReportSearchableField.PERFORMER).contains(terms)
                .or(new Criteria(ReportSearchableField.ACTIVITY).contains(terms)));
        if (limit != null && (limit.compareTo(0) > 0)) {
            q.setPageRequest(new PageRequest(0, limit));
        }
        q.addSort(new Sort(
                new Sort.Order(Sort.Direction.DESC, ReportSearchable.START_DATE_FIELD)));
        q.addGroupByField(ReportSearchableField.PERFORMER);
        q.setDefType(EDISMAX);
        return solrOperations.queryForPage(q, Report.class);
    }

    @Override
    public TermsPage getSearchCloud() {
        SimpleTermsQuery q = new SimpleTermsQuery();
        q.addField(ReportSearchableField.PERFORMER);
        q.addField(ReportSearchableField.ACTIVITY);
        TermsPage termsPage = solrOperations.queryForTermsPage(q);
        return termsPage;
    }

    private String[] normalizeQuery(String query) {
        String[] splitted = query.split(" ");
        String[] queries = new String[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            queries[i] = splitted[i].trim().toLowerCase();
        }
        return queries;
    }
}
