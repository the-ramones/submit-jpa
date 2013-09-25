package sp.model;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Static meta-model for type-safe JPA2 Criteria queries
 *
 * @author Paul Kulitski
 */
@StaticMetamodel(Op.class)
public class Op_ {

    public static volatile SingularAttribute<Op, Integer> id;
    public static volatile SingularAttribute<Op, String> title;
    public static volatile SingularAttribute<Op, String> description;
    public static volatile ListAttribute<Op, Register> registers;
}
