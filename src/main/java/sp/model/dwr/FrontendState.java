package sp.model.dwr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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
        return maxOnPager;
    }

    public void setMaxOnPager(int maxOnPager) {
        this.maxOnPager = maxOnPager;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPromptLimit() {
        return promptLimit;
    }

    public void setPromptLimit(int promptLimit) {
        this.promptLimit = promptLimit;
    }
}
