package sp.util;

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.support.PagedListHolder;

/**
 * Reports! {@link org.springframework.beans.support.PagedListHolder} override
 * for supporting 'next', 'prev', 'first', 'last' page set up.
 *
 * @author Paul Kulitski
 * @see PagedListHolder
 */
public class SpPagedListHolder<T> extends PagedListHolder<T> {    
    
    private static final Logger logger = LoggerFactory.getLogger(SpPagedListHolder.class);

    /**
     * Sets page on the
     * {@link org.springframework.beans.support.PagedListHolder} Allow set up
     * page as {@link java.lang.String}. Acceptable values: everything that can
     * be passed into {@link java.lang.Integer#valueOf(java.lang.String)} plus
     * string values 'next', 'prev', 'first', 'last'
     *
     * @param page page being set up
     */
    public void setPage(String page) {
        Integer p = null;
        Pattern regexp = Pattern.compile("^next|prev|last|first$", Pattern.CASE_INSENSITIVE);        
        try {
            p = Integer.valueOf(page);
            setPage(p);
        } catch (NumberFormatException ex) {            
        }        
        if (p == null) {
            if (regexp.matcher(page).matches()) {
                if (page.equals("next")) {
                    nextPage();
                } else if (page.equals("prev")) {
                    previousPage();
                } else if (page.equals("last")) {
                    setPage(getPageCount() - 1);
                } else if (page.equals("first")) {
                    setPage(0);
                }
            }
        }
    }
}
