package sp.service;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sp.model.Report;
import sp.model.ajax.Prompt;
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
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Long getAllCount(String query) {
        return suggestRepository.getAllCount(query);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Long> getIdsByQuery(String query) {
        return suggestRepository.getIdsByQuery(query);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Long> getIdsByQuery(String query, Long limit) {
        return suggestRepository.getIdsByQuery(query, limit);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Report> getReportsByQuery(String query) {
        return suggestRepository.getReportsByQuery(query);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Report> getReportsByQuery(String query, Long limit) {
        return suggestRepository.getReportsByQuery(query, limit);
    }

    @Override
    public List<Prompt> getPrompts(String query, Long limit) {
        return suggestRepository.getPrompts(query, limit);
    }

    @Override
    public List<String> getPromptsAsString(String query, Long limit) {
        List<Prompt> prompts = getPrompts(query, limit);
        List<String> result = new ArrayList<String>(prompts.size());
        for (Prompt prompt : prompts) {
            result.add(prompt.toString());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Long> getIdsByQuery(String query, Long limit, Long offset) {
        return suggestRepository.getIdsByQuery(query, limit, offset);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Report> getReportsByQuery(String query, Long limit, Long offset) {
        return suggestRepository.getReportsByQuery(query, limit, offset);
    }

    @Override
    public List<Prompt> getPrompts(String query) {
        return suggestRepository.getPrompts(query);
    }

    @Override
    public List<String> getPromptStrings(String query, Long limit) {
        return suggestRepository.getPromptStrings(query, limit);
    }
}
