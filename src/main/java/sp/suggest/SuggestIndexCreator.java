package sp.suggest;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import sp.model.Report;
import sp.service.ReportService;

/**
 * Updates report index
 *
 * @author Paul Kulitski
 */
//@Lazy
@Component
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
public class SuggestIndexCreator implements IndexCreator {

    @Inject
    SuggestIndex suggestIndex;
    @Inject
    ReportService reportService;
    private static final Logger logger = LoggerFactory.getLogger(SuggestIndexCreator.class);

    @PostConstruct
    public void initIndex() {
        logger.debug("Initializing a Reports! Suggest index. "
                + "Update fixed rate has been set to {}", UPDATE_RATE_HOURLY);
        reloadIndex();
    }
    private static int MILISECONDS_IN_HOUR = 1 * 60 * 60 * 1000;

    //TODO: add processing lock
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
            logger.error("Cannot convert to specified character encoding");
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
                Normalizer.normalize(key, Normalizer.Form.NFD);
                suggestIndex.addToSwapIndex(key, id);
            }
            for (String key : report.getPerformer().split("\\s")) {
                key = key.toLowerCase();
                Normalizer.normalize(key, Normalizer.Form.NFD);
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
                Normalizer.normalize(key, Normalizer.Form.NFD);
                suggestIndex.addToIndex(key, id);
            }
            for (String key : report.getPerformer().split("\\s")) {
                key = key.toLowerCase();
                Normalizer.normalize(key, Normalizer.Form.NFD);
                suggestIndex.addToIndex(key, id);
            }
        }
    }
}
