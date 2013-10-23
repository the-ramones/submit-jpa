package sp.util;

import java.io.Serializable;
import org.springframework.beans.support.SortDefinition;

/**
 * Reports! {@link org.springframework.beans.support.SortDefinition}
 * implementation.
 * Use default constructor for ascending and ignore-case sort by 'id'
 * field. Implements {@link java.io.Serializable} for serialization 
 * as a {@link javax.servlet.http.HttpSession} attribute
 *
 * @author Paul Kulitski
 * @see Serializable
 */
public class SpSortDefinition implements SortDefinition, Serializable {
        
    public static final long serialVersionUID = 1997002881L;

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