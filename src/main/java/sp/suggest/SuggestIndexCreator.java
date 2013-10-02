package sp.suggest;

import javax.inject.Inject;
import org.springframework.stereotype.Component;
import sp.model.Report;
import sp.service.ReportService;

/**
 * Updates report index
 *
 * @author Paul Kulitski
 */
@Component
public class SuggestIndexCreator implements IndexCreator {

    @Inject
    SuggestIndex suggestIndex;
    @Inject
    ReportService reportService;

    @Override
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
