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
 * Operation entity
 * 
 * @Author Paul Kulitski
 */
@Entity
@Table(name = "ops", catalog = "registry")
@NamedQueries({
    @NamedQuery(name = "Op.findByTitle", query = "select o from Op o where o.title = :title")
})
public class Op implements java.io.Serializable {

    private Integer id;
    private String title;
    private String description;
    private List<Register> registers = new ArrayList<Register>(0);

    public Op() {
    }

    public Op(String title) {
        this.title = title;
    }

    public Op(String title, String description, List<Register> registers) {
        this.title = title;
        this.description = description;
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

    @Column(name = "title", nullable = false, length = 100)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "op")
    public List<Register> getRegisters() {
        return this.registers;
    }

    public void setRegisters(List<Register> registerses) {
        this.registers = registerses;
    }

    @Override
    public int hashCode() {
        int hash = 243;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 97 * hash + (this.registers != null ? this.registers.hashCode() : 0);
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
        final Op other = (Op) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if (this.registers != other.registers && (this.registers == null || !this.registers.equals(other.registers))) {
            return false;
        }
        return true;
    }
}
