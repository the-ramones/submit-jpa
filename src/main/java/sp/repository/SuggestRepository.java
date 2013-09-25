package sp.repository;

import java.util.List;
import sp.model.Report;
import sp.model.ajax.Prompt;

/**
 * Interface for Reports! Suggest. Limited for search only for 'performer' field
 * so far.
 *
 * @author Paul Kulitski
 */
public interface SuggestRepository {

    public Long getAllCount(String query);

    public List<Long> getIdsByQuery(String query);

    public List<Long> getIdsByQuery(String query, Long limit);

    public List<Report> getReportsByQuery(String query);

    public List<Report> getReportsByQuery(String query, Long limit);

    public List<Prompt> getPrompts(String query, Long limit);
}
