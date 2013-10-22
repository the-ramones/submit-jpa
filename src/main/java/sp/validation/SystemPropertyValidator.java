package sp.validation;

import java.util.regex.Pattern;
import org.apache.commons.validator.UrlValidator;

/**
 * Validator for system property values
 *
 * @author Paul Kulitski
 */
public class SystemPropertyValidator {

    public boolean supports(Class<?> type) {
        if (String.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }
    private static final String POSITIVE_NUMBER_REGEXP = "^(?iu)[0-9]{1,10}[0-9\\.]{0,6}$";
    private static final String POSITIVE_INTEGER_REGEXP = "^(?iu)[0-9]{1,16}$";
    private static final String LEGAL_LATIN_CHARS_REGEXP = "^\\p{ASCII}+$";

    public boolean isValid(String key, String value) {
        boolean result = false;
        UrlValidator urlValidator = new UrlValidator();
        if (key.equals("promptLimit")) {
            result = Pattern.matches(POSITIVE_INTEGER_REGEXP, value);
        } else if (key.equals("pageSize")) {
            result = Pattern.matches(POSITIVE_INTEGER_REGEXP, value);
        } else if (key.equals("maxOnPager")) {
            result = Pattern.matches(POSITIVE_INTEGER_REGEXP, value);
        } else if (key.equals("solrHost")) {
            result = urlValidator.isValid(value);
        } else if (key.equals("solrCore")) {
            result = Pattern.matches(LEGAL_LATIN_CHARS_REGEXP, value);
        } else if (key.equals("dburl")) {
            result = urlValidator.isValid(value);
        } else if (key.equals("schema")) {
            result = Pattern.matches(LEGAL_LATIN_CHARS_REGEXP, value);
        } else if (key.equals("solrTimeout")) {
            result = Pattern.matches(POSITIVE_INTEGER_REGEXP, value);
        } else if (key.equals("solrMaxConnections")) {
            result = Pattern.matches(POSITIVE_INTEGER_REGEXP, value);
        }
        return result;
    }
}
