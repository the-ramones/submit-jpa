package sp.repository;

import java.util.List;
import sp.model.Report;

/**
 * Interface for Reports! Suggest. Limited for search only for 'performer' field
 * so far.
 *
 * @author Paul Kulitski
 */
public interface SuggestRepository {

    public Long[] getIdsByQuery(String query);

    public Long[] getIdsByQuery(String query, Long limit);

    public List<Report> getReportsByQuery(String query);

    public List<Report> getReportsByQuery(String query, Long limit);
}
