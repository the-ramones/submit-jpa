package sp.model.searchable;

import org.springframework.data.solr.core.query.Field;

/**
 * {@link Report} searchable fields
 *
 * @author Paul Kulitski
 */
public enum ReportSearchableField implements Field {

    ID(ReportSearchable.ID_FIELD),
    STARTDATE(ReportSearchable.START_DATE_FIELD),
    ENDDATE(ReportSearchable.END_DATE_FIELD),
    PERFORMER(ReportSearchable.PERFORMER_FIELD),
    ACTIVITY(ReportSearchable.ACTIVITY_FIELD);

    private ReportSearchableField(String fieldName) {
        this.fieldName = fieldName;
    }
    private final String fieldName;

    @Override
    public String getName() {
        return fieldName;
    }
}
