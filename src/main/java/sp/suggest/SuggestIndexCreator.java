package sp.suggest;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.stereotype.Component;
import sp.model.Report;
import sp.service.ReportService;

/**
 * Updates report index
 *
 * @author Paul Kulitski
 */
@Lazy
@Component
public class SuggestIndexCreator implements IndexCreator {

    @Inject
    SuggestIndex suggestIndex;
    @Inject
    ReportService reportService;
    private static final int UPDATE_RATE = 4 * 60 * 1000;

    @PostConstruct
    public void initIndex() {
        updateIndex();
    }

    @Scheduled(fixedRate = UPDATE_RATE)
    public void updateIndex() {
        suggestIndex.setInProgress(true);
        Long id;
        for (Report report : reportService.getAllReports()) {
            id = report.getId();
            for (String key : report.getActivity().split("\\s")) {
                suggestIndex.addToIndex(key, id);
            }
            for (String key : report.getPerformer().split("\\s")) {
                suggestIndex.addToIndex(key, id);
            }
        }
        suggestIndex.setInProgress(false);
    }
}
