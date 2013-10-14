package sp.service;

import javax.inject.Inject;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.result.TermsPage;
import org.springframework.stereotype.Service;
import sp.model.Report;
import sp.repository.SolrRepository;

/**
 * Basic implementation of {@link SolrService}
 *
 * @author Paul Kulitski
 */
@Lazy
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
    public TermsPage getSearchCloud() {
        return solrRepository.getSearchCloud();
    }
}
