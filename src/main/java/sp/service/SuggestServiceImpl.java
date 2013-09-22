package sp.service;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import sp.model.Report;
import sp.repository.SuggestRepository;

/**
 * Reports! Suggest service {@link SuggestReportService} basic implementation.
 *
 * @author Paul Kulitski
 * @see Service
 */
@Service
public class SuggestServiceImpl implements SuggestService {

    @Inject
    SuggestRepository suggestRepository;

    @Override
    public Long[] getIdsByQuery(String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long[] getIdsByQuery(String query, Long limit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Report> getReportsByQuery(String query) {
        return suggestRepository.getReportsByQuery(query);
    }

    @Override
    public List<Report> getReportsByQuery(String query, Long limit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
