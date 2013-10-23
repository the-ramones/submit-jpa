package sp.model.ajax;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import sp.model.Report;
import sp.util.SpLazyPager;

/**
 * Holder for list of results and lightweight pager of them to be serialized by
 * Jackson or JAXB mapper and passed to the client
 *
 * @author Paul Kulitski
 */
@XmlRootElement
public class ResultPager implements Serializable {

    private SpLazyPager pager;
    private List<Report> results;

    public ResultPager() {
    }

    public ResultPager(SpLazyPager pager, List<Report> results) {
        this.pager = pager;
        this.results = results;
    }

    public SpLazyPager getPager() {
        return pager;
    }

    public void setPager(SpLazyPager pager) {
        this.pager = pager;
    }

    public List<Report> getResults() {
        return results;
    }

    public void setResults(List<Report> results) {
        this.results = results;
    }
}
