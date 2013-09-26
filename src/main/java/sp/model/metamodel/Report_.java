package sp.model;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import sp.model.Report;

/**
 * Static meta-model for type-safe JPA2 Criteria queries
 *
 * @author Paul Kulitski
 */
@StaticMetamodel(Report.class)
public class Report_ {

    public static volatile SingularAttribute<Report, Long> id;
    public static volatile SingularAttribute<Report, Date> startDate;
    public static volatile SingularAttribute<Report, Date> endDate;
    public static volatile SingularAttribute<Report, String> performer;
    public static volatile SingularAttribute<Report, String> activity;
}
