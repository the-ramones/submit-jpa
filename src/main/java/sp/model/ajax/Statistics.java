package sp.model.ajax;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holder for User Checklist statistics
 *
 * @author Paul Kulitski
 */
@XmlRootElement
public class Statistics implements Serializable {

    /**
     * Average range of days
     */
    private String averageRange;
    private String activities;
    private String performers;
    private int countPerformers;
    private int countActivities;

    public Statistics() {
    }

    public Statistics(String dateRange, String activities, String performers) {
        this.averageRange = dateRange;
        this.activities = activities;
        this.performers = performers;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getPerformers() {
        return performers;
    }

    public void setPerformers(String performers) {
        this.performers = performers;
    }

    public String getAverageRange() {
        return averageRange;
    }

    public void setAverageRange(String averageRange) {
        this.averageRange = averageRange;
    }

    public int getCountPerformers() {
        return countPerformers;
    }

    public void setCountPerformers(int countPerformers) {
        this.countPerformers = countPerformers;
    }

    public int getCountActivities() {
        return countActivities;
    }

    public void setCountActivities(int countActivities) {
        this.countActivities = countActivities;
    }

    @Override
    public String toString() {
        return "Statistics{" + "dateRange=" + averageRange + ", activities=" + activities + ", performers=" + performers + '}';
    }
}
