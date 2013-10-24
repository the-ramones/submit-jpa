package sp.service;

import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.TermsPage;
import org.springframework.stereotype.Service;
import sp.model.Report;
import sp.repository.SolrRepository;

/**
 * Basic implementation of {@link SolrService}. Spring data Solr supports
 * spring managed transactions.
 *
 * @author Paul Kulitski
 */
@Service
public class SolrServiceImpl implements SolrService {

    @Inject
    SolrRepository solrRepository;
    
    @Override
    public Page<Report> searchByPerformer(String query) {
        return solrRepository.searchByPerformer(query);
    }

    @Override
    public Page<Report> searchByActivity(String query) {
        return solrRepository.searchByActivity(query);
    }

    @Override
    public Page<Report> search(String query) {
        return solrRepository.search(query);
    }

    @Override
    public Page<Report> suggest(String query) {
        return solrRepository.suggest(query);
    }

    @Override
    public TermsPage getSearchCloud() {
        return solrRepository.getSearchCloud();
    }

    @Override
    public Page<Report> search(String query, Integer limit) {
        return solrRepository.search(query, limit);
    }

    @Override
    public Page<Report> search(String query, Pageable p) {
        return solrRepository.search(query, p);
    }

    @Override
    public Page<Report> search(String query, Integer limit, Pageable p) {
        return solrRepository.search(query, limit, p);
    }

    @Override
    public Page<Report> suggest(String query, Integer limit) {
        return solrRepository.suggest(query, limit);
    }
    
    @Override
    public Page<Report> search(String query, int page, int size) {
        return solrRepository.search(query, page, size);
    }
}
