package sp.util;

import org.springframework.beans.support.SortDefinition;

/**
 * Reports! {@link org.springframework.beans.support.SortDefinition}
 * implementation.
 * Use default constructor for ascending and ignore-case sort by 'id'
 * field
 *
 * @author Paul Kulitski
 */
public class SpSortDefinition implements SortDefinition {

    private String property;
    private boolean ignoreCase;
    private boolean ascending;

    public SpSortDefinition() {
        property = "id";
        ignoreCase = true;
        ascending = true;
    }

    public SpSortDefinition(String property, boolean ignoreCase, boolean ascending) {
        this.property = property;
        this.ignoreCase = ignoreCase;
        this.ascending = ascending;
    }
        
    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    @Override
    public boolean isAscending() {
        return ascending;
    }
}