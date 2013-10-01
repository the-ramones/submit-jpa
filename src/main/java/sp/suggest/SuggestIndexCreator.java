package sp.suggest;

import javax.inject.Inject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sp.model.Report;
import sp.repository.ReportRepository;
import sp.service.ReportService;

/**
 * Updates report index
 *
 * @author Paul Kulitski
 */
@Component
public class SuggestIndexCreator {

    @Inject
    SuggestIndex suggestIndex;
    @Inject
    ReportService reportService;

    private static final int UPDATE_RATE = 4 * 60 * 1000;
    
    
    @Scheduled(fixedRate = UPDATE_RATE)
    public void updateIndex() {
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
    }
}
