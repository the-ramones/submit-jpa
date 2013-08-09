package sp.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author the-ramones
 */
@Entity
@Table(name = "reports", schema = "enterprise")
@NamedQueries({
    @NamedQuery(name = "Report.getPerformers", query = "select distinct r.performer from Report r"),
    @NamedQuery(name = "Report.getReportsByPerformer", query = "select r from Report r where r.performer = :performer")
})
public class Report implements Serializable {

    private Long id;
    private Date startDate;
    private Date endDate;
    private String performer;
    private String activity;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "startDate", nullable = false, length = 10)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "endDate", nullable = true, length = 10)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "performer", nullable = false)
    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    @Column(name = "activity", nullable = false)
    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    @Override
    public int hashCode() {
        int hash = 63;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 41 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 41 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        hash = 41 * hash + (this.performer != null ? this.performer.hashCode() : 0);
        hash = 41 * hash + (this.activity != null ? this.activity.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Report other = (Report) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate))) {
            return false;
        }
        if ((this.performer == null) ? (other.performer != null) : !this.performer.equals(other.performer)) {
            return false;
        }
        if ((this.activity == null) ? (other.activity != null) : !this.activity.equals(other.activity)) {
            return false;
        }
        return true;
    }
}
