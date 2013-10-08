package sp.suggest;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(SuggestIndexCreator.class);

    @PostConstruct
    public void initIndex() {
        logger.debug("Initializing a Reports! Suggest index. Update fixed rate has been set to {}", UPDATE_RATE_HOURLY);
        reloadIndex();
        logger.error("INDEX: {}", suggestIndex.getIndex());
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

            List<Report> reports = reportService.getAllReports();
            iterateAndAddToSwap(reports);
            /*
             * Switch indexes.
             */
            suggestIndex.switchIndexes();

        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(SuggestIndexCreator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            suggestIndex.setProcessing(false);
            suggestIndex.getProcessLock().unlock();
        }
    }

    private void iterateAndAddToSwap(List<Report> reports) throws UnsupportedEncodingException {
        Long id;
        for (Report report : reports) {
            id = report.getId();
            for (String key : report.getActivity().split("\\s")) {
                key = key.toLowerCase();
                System.out.println("KEY ACTIVITY: " + new String(key.getBytes("iso-8859-1"), "utf-8"));
                suggestIndex.addToSwapIndex(key, id);
            }
            for (String key : report.getPerformer().split("\\s")) {
                System.out.println("NATIVE PERFORMER: " + report.getPerformer());
                System.out.println("KEY PERFORMER: " + key);
                System.out.println("KEY PERFORMER ENCODED: " + new String(key.getBytes("iso-8859-1"), "utf-8"));
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
