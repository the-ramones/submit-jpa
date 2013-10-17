package sp.model.dwr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import sp.system.SystemConstants;

/**
 * Front-end system's state holder
 *
 * @author Paul Kulitski
 */
@Lazy
@Component
@Scope(value = "singleton")
@PropertySource("classpath:report-servlet.properties")
public class FrontendState extends GenericState {

    @Autowired
    Environment env;
    private int maxOnPager;
    private int pageSize;
    private int promptLimit;

    public FrontendState() {
        super();
    }

    public int getMaxOnPager() {
        //return maxOnPager;
        try {
            return Integer.valueOf(SystemConstants.MAX_ON_PAGER_DAFAULT_VALUE);
        } catch (NumberFormatException ex) {
        }
        return -1;
    }

    public void setMaxOnPager(int maxOnPager) {
        this.maxOnPager = maxOnPager;
    }

    public int getPageSize() {
        //return pageSize;
        try {
            return Integer.valueOf(SystemConstants.PAGE_SIZE_DEFAULT_VALUE);
        } catch (NumberFormatException ex) {
        }
        return -1;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPromptLimit() {
        //return promptLimit;
        try {
            return Integer.valueOf(SystemConstants.PROMPT_LIMIT_DEFAULT_VALUE);
        } catch (NumberFormatException ex) {
        }
        return -1;
    }

    public void setPromptLimit(int promptLimit) {
        this.promptLimit = promptLimit;
    }
}
