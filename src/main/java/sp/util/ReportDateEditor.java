package sp.util;

/**
 * PropertyEditor Converts string like '01 Sep 2010' to a valid
 * {@link java.util.Date} object
 *
 * @author Paul Kulitski
 */
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.util.StringUtils;

public class ReportDateEditor extends PropertyEditorSupport {

    private final SimpleDateFormat dateFormat;
    private final boolean allowEmpty;
    private final boolean strict;

    public ReportDateEditor(SimpleDateFormat dateFormat, boolean allowEmpty, boolean strict) {
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
        this.strict = strict;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            setValue(null);
        } else {
            String pattern = this.dateFormat.toPattern();
            if (strict && pattern.length() != text.length()) {
                throw new IllegalArgumentException("error date Pattern: " + text + ",should be " + pattern);
            }
            try {
                setValue(this.dateFormat.parse(text));
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Could not parse date: " + ex.getMessage());
            }
        }
    }

    @Override
    public String getAsText() {
        return (getValue() == null ? "" : this.dateFormat.format((Date) getValue()));
    }
}