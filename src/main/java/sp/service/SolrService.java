package sp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Report> search(String query, Integer limit);

    public Page<Report> search(String query, Pageable p);

    public Page<Report> search(String query, Integer limit, Pageable p);
    
    public Page<Report> search(String query, int page, int size);

    public Page<Report> suggest(String query);

    public Page<Report> suggest(String query, Integer limit);

    public TermsPage getSearchCloud();
}
