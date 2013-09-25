package sp.model;

import java.util.ArrayList;
import java.util.List;
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
 * Database User entity
 *
 * @author Paul Kulitksi
 */
@Entity
@Table(name = "users", catalog = "registry")
@NamedQueries({
    @NamedQuery(name = "User.findByName", query = "select u from User u where u.name = :name")
})
public class User implements java.io.Serializable {

    private Integer id;
    private String name;
    private String job;
    private List<Register> registers = new ArrayList<Register>(0);

    public User() {
    }

    public User(String fullname) {
        this.name = fullname;
    }

    public User(String fullname, String job, List<Register> registers) {
        this.name = fullname;
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
    public String getName() {
        return this.name;
    }

    public void setName(String fullname) {
        this.name = fullname;
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
        int hash = 773;
        hash = 111 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 111 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 111 * hash + (this.job != null ? this.job.hashCode() : 0);
        hash = 111 * hash + (this.registers != null ? this.registers.hashCode() : 0);
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
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
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
