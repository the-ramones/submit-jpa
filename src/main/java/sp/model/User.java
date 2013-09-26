package sp.model;
// Generated Jul 29, 2013 9:22:08 PM by Hibernate Tools 3.2.1.GA

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * User generated by hbm2java
 */
@Entity
@Table(name = "users", catalog = "registry")
@NamedQueries({
    @NamedQuery(name = "User.findByFullname",
        query = "select u from User u where u.fullname = :fullname")
})
public class User implements java.io.Serializable {

    private Integer id;
    
    private String fullname;
    private String job;
    private List<Register> registers = new ArrayList<Register>(0);

    public User() {
    }

    public User(String fullname) {
        this.fullname = fullname;
    }

    public User(String fullname, String job, List<Register> registers) {
        this.fullname = fullname;
        this.job = job;
        this.registers = registers;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "fullname", nullable = false)
    public String getFullname() {
        return this.fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Column(name = "job")
    public String getJob() {
        return this.job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    public List<Register> getRegisters() {
        return this.registers;
    }

    public void setRegisters(List<Register> registerses) {
        this.registers = registerses;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 11 * hash + (this.fullname != null ? this.fullname.hashCode() : 0);
        hash = 11 * hash + (this.job != null ? this.job.hashCode() : 0);
        hash = 11 * hash + (this.registers != null ? this.registers.hashCode() : 0);
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
        final User other = (User) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.fullname == null) ? (other.fullname != null) : !this.fullname.equals(other.fullname)) {
            return false;
        }
        if ((this.job == null) ? (other.job != null) : !this.job.equals(other.job)) {
            return false;
        }
        if (this.registers != other.registers && (this.registers == null || !this.registers.equals(other.registers))) {
            return false;
        }
        return true;
    }
}
