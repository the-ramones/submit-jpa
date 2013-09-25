package sp.model;

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Register entity
 *
 * @author Paul Kulitski
 */
@Entity
@Table(name = "registers", catalog = "registry")
@NamedQueries({
    @NamedQuery(name = "Register.findByPeriod", query = "select r from Register r where r.recordTime between :startDate and :endDate"),
    @NamedQuery(name = "Register.find", query = "select r from Register r where r.op = :op and r.user = :user and (r.recordTime between :startDate and :endDate)")
})
public class Register implements java.io.Serializable {

    private RegisterId id;
    private User user;
    private Op op;
    private Date recordTime;

    public Register() {
    }

    public Register(RegisterId id, User users, Op ops) {
        this.id = id;
        this.user = users;
        this.op = ops;
    }

    public Register(RegisterId id, User users, Op ops, Date recordTime) {
        this.id = id;
        this.user = users;
        this.op = ops;
        this.recordTime = recordTime;
    }

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "id", column =
                @Column(name = "id", nullable = false)),
        @AttributeOverride(name = "userId", column =
                @Column(name = "user_id", nullable = false)),
        @AttributeOverride(name = "opId", column =
                @Column(name = "op_id", nullable = false))})
    public RegisterId getId() {
        return this.id;
    }

    public void setId(RegisterId id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User users) {
        this.user = users;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "op_id", nullable = false, insertable = false, updatable = false)
    public Op getOp() {
        return this.op;
    }

    public void setOp(Op ops) {
        this.op = ops;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "record_time", length = 19)
    public Date getRecordTime() {
        return this.recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public int hashCode() {
        int hash = 331;
        hash = 647 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 647 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 647 * hash + (this.op != null ? this.op.hashCode() : 0);
        hash = 647 * hash + (this.recordTime != null ? this.recordTime.hashCode() : 0);
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
        final Register other = (Register) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.user != other.user && (this.user == null || !this.user.equals(other.user))) {
            return false;
        }
        if (this.op != other.op && (this.op == null || !this.op.equals(other.op))) {
            return false;
        }
        if (this.recordTime != other.recordTime && (this.recordTime == null || !this.recordTime.equals(other.recordTime))) {
            return false;
        }
        return true;
    }
}
