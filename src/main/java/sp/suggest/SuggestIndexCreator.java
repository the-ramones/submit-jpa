package sp.suggest;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.context.annotation.Lazy;
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

    @PostConstruct
    public void initIndex() {
        updateIndex();
    }
    private static int MILISECONDS_IN_HOUR = 1 * 60 * 60 * 1000;

    @Override
    public void updateIndex() {
        suggestIndex.setProcessing(true);
        Date prevHour = new Date(new Date().getTime() - MILISECONDS_IN_HOUR);
        Date nowHour = new Date();
        List<Report> reports = reportService.getReports(prevHour, nowHour);
        iterateAndAddToIndex(reports);
    }

    @Override
    public void reloadIndex() {
        try {
            suggestIndex.getProcessLock().lock();
            suggestIndex.setProcessing(true);

            ConcurrentHashMap<String, LinkedList> swapIndex =
                    suggestIndex.getSwapIndex();
            List<Report> reports = reportService.getAllReports();
            iterateAndAddToSwap(reports);
            /*
             * Switch indexes.
             */
            suggestIndex.switchIndexes();

        } finally {
            suggestIndex.setProcessing(false);
            suggestIndex.getProcessLock().unlock();
        }
    }

    private void iterateAndAddToSwap(List<Report> reports) {
        Long id;
        for (Report report : reports) {
            id = report.getId();
            for (String key : report.getActivity().split("\\s")) {
                key = key.toLowerCase();
                suggestIndex.addToSwapIndex(key, id);
            }
            for (String key : report.getPerformer().split("\\s")) {
                key = key.toLowerCase();
                suggestIndex.addToSwapIndex(key, id);
            }
        }
    }

    private void iterateAndAddToIndex(List<Report> reports) {
        Long id;
        for (Report report : reports) {
            id = report.getId();
            for (String key : report.getActivity().split("\\s")) {
                key = key.toLowerCase();
                suggestIndex.addToIndex(key, id);
            }
            for (String key : report.getPerformer().split("\\s")) {
                key = key.toLowerCase();
                suggestIndex.addToIndex(key, id);
            }
        }
    }
}
