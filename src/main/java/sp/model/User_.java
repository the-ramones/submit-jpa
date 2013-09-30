package sp.model;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Static meta-model for type-safe JPA2 Criteria queries
 *
 * @author Paul Kulitski
 */
@StaticMetamodel(User.class)
public class User_ {

    public static volatile SingularAttribute<User, Integer> id;
    public static volatile SingularAttribute<User, String> name;
    public static volatile SingularAttribute<User, String> job;
    public static volatile SingularAttribute<User, String> email;
    public static volatile ListAttribute<User, Register> registers;
}
