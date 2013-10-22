package sp.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sp.model.Report;
import sp.model.ajax.Statistics;
import sp.repository.ReportRepository;

/**
 * Generator for generating {@link Statistics} instances for User's checklists
 *
 * @author Paul Kulitski
 */
public class SpStatisticsGenerator {

    static final long ONE_DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
    private static final Logger logger = LoggerFactory.getLogger(SpStatisticsGenerator.class);
    
    /*
     * Subject for static setter injection via @PostConstruct handler
     */
    private static ReportRepository reportRepository;

    public static void setReportRepository(ReportRepository repository) {
        reportRepository = repository;
    }

    /**
     * Generate {@link Statistics} instance for User's checklist
     *
     * @param checklist set of IDs being currently in User's checklist
     * @return {@link Statistics} instance that holds statistics
     */
    public static synchronized Statistics generateStatistics(Set<Long> ids) {
        List<Report> reports = reportRepository.getReports(ids);
        /*
         * Cache initial capacity
         */
        int initialCapacity = reports.size();
        Set<String> performers = new HashSet<String>(initialCapacity);
        Set<String> activities = new HashSet<String>(initialCapacity);
        List<Long> rangePeriods = new ArrayList<Long>(initialCapacity);
        long tmpRange;
        for (Report report : reports) {
            performers.add(report.getPerformer());
            activities.add(report.getActivity());
            if (report.getEndDate() != null) {
                tmpRange = report.getEndDate().getTime() - report.getStartDate().getTime();
                /*
                 * If duration < 0, then Report instance is not valid. So log it,
                 * drop the range (average won't be robust). If duration is equals
                 * zero (start and end date of activity are the same day) supppose
                 * activity duration to be equals 1 day.
                 */
                if (tmpRange > 0) {
                    rangePeriods.add(tmpRange);
                } else if (tmpRange == 0) {
                    rangePeriods.add(ONE_DAY_IN_MILLISECONDS);
                }
            } else {
                tmpRange = new Date().getTime() - report.getStartDate().getTime();
                if (tmpRange > 0) {
                    rangePeriods.add(tmpRange);
                } else if (tmpRange == 0) {
                    rangePeriods.add(ONE_DAY_IN_MILLISECONDS);
                }
            }
        }
        Statistics stats = new Statistics();
        StringBuilder sb = new StringBuilder(performers.size()
                * performers.iterator().next().length());
        for (String performer : performers) {
            sb.append(performer).append(',').append(' ');
        }
        sb.delete(sb.length() - 2, sb.length() - 1);
        stats.setPerformers(sb.toString());
        stats.setCountPerformers(performers.size());
        sb = new StringBuilder(activities.size() * activities.iterator().next().length());
        for (String activity : activities) {
            sb.append(activity).append(',').append(' ');
        }
        sb.delete(sb.length() - 2, sb.length() - 1);
        stats.setActivities(sb.toString());
        stats.setCountActivities(activities.size());
        long avg = average(rangePeriods);
        logger.info("AVERAGE LONG: {}", avg);
        sb = new StringBuilder(4);

        sb.append(avg / ONE_DAY_IN_MILLISECONDS);
        logger.info("AVERAGE LONG AFTER: {}", avg / ONE_DAY_IN_MILLISECONDS);
        stats.setAverageRange(sb.toString());
        return stats;
    }

    /**
     * Calculates average value in the list
     *
     * @param list list of values
     * @return average value
     */
    private static synchronized long average(List<Long> list) {
        if (list.size() > 0) {
            Long average = 0L;
            for (Long p : list) {
                average += p;
            }
            return Math.round(average / list.size());
        } else {
            return 0L;
        }
    }
}
