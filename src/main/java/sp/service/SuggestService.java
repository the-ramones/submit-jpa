package sp.service;

import java.util.List;
import sp.model.Report;

/**
 * Suggest Reports! service interface
 *
 * @author Paul Kulitski
 */
public interface SuggestService {

    public Long[] getIdsByQuery(String query);

    public Long[] getIdsByQuery(String query, Long limit);

    public List<Report> getReportsByQuery(String query);

    public List<Report> getReportsByQuery(String query, Long limit);
}
