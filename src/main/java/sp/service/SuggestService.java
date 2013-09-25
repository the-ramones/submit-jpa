package sp.service;

import java.util.List;
import sp.model.Report;
import sp.model.ajax.Prompt;

/**
 * Suggest Reports! service interface
 *
 * @author Paul Kulitski
 */
public interface SuggestService {

    public Long getAllCount(String query);

    public List<Long> getIdsByQuery(String query);

    public List<Long> getIdsByQuery(String query, Long limit);

    public List<Report> getReportsByQuery(String query);

    public List<Report> getReportsByQuery(String query, Long limit);

    public List<Prompt> getPrompts(String query, Long limit);

    public List<String> getPromptsAsString(String query, Long limit);
}
