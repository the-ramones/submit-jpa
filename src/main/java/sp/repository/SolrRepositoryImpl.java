package sp.repository;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
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

/**
 * Basic implementations of {@link SolrRepository}
 *
 * @author Paul Kulitski
 */
@Lazy
@Repository
public class SolrRepositoryImpl implements SolrRepository {

    @Inject
    @Named("solrTemplate")
    SolrOperations solrOperations;
    private static final String EDISMAX = "edismax";

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
        SimpleQuery q = new SimpleQuery(
                new Criteria(ReportSearchableField.PERFORMER).contains(query)
                .or(new Criteria(ReportSearchableField.ACTIVITY).contains(query)));
//        q.addSort(new Sort(
//                new Sort.Order(Sort.Direction.ASC, ReportSearchable.PERFORMER_FIELD)));
//        q.addSort(new Sort(
//                new Sort.Order(Sort.Direction.ASC, ReportSearchable.ACTIVITY_FIELD)));

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
}
