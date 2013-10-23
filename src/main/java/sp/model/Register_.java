package sp.model;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Static meta-model for type-safe JPA2 Criteria queries
 *
 * @author Paul Kulitski
 */
@StaticMetamodel(Register.class)
public class Register_ {

    public static volatile SingularAttribute<Register, RegisterId> id;
    public static volatile SingularAttribute<Register, Op> op;
    public static volatile SingularAttribute<Register, User> user;
    public static volatile SingularAttribute<Register, Date> recordDate;
}
