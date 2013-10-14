package sp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.result.TermsPage;
import sp.model.Report;

/**
 * Interface for Apache Solr repositories
 *
 * @author Paul Kulitski
 */
public interface SolrRepository {

    public Page<Report> searchByPerformer(String query);

    public Page<Report> searchByActivity(String query);

    public Page<Report> search(String query);

    public TermsPage getSearchCloud();
}
