package sp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.result.TermsPage;
import sp.model.Report;

/**
 * Interface for Reports! Solr search service
 *
 * @author Paul Kulitski
 */
public interface SolrService {

    public Page<Report> searchByPerformer(String query);

    public Page<Report> searchByActivity(String query);

    public Page<Report> search(String query);

    public TermsPage getSearchCloud();
    
}
