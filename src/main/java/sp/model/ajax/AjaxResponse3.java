package sp.model.ajax;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Complement model class for holding responses of Ajax requests. Convenient
 * holder for response results, target field or object, errors and status
 * messages.
 *
 * @author Paul Kulitski
 */
@XmlRootElement
public class AjaxResponse3 {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    /**
     * Status of the response. Mainly 'success' or 'error'. The presence of
     * {@link AjaxResponse#status} and {@link AjaxResponse#errors} depends on
     * that field
     */
    private String status;
    /**
     * The list of returned result object from the server. Generic. Mainly fills
     * up only when {@link AjaxResponse#SUCCESS}.
     */
    private List results;
    /**
     * The list of returned error from the server. Mainly fills up only if
     * {@link AjaxResponse#ERROR}.
     */
    private List errors;

    public AjaxResponse3() {
    }

    public AjaxResponse3(String status) {
        this.status = status;
    }

    public AjaxResponse3(String status, List results, List errors) {
        this.status = status;
        this.results = results;
        this.errors = errors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List getResults() {
        return results;
    }

    public void setResults(List results) {
        this.results = results;
    }

    public List getErrors() {
        return errors;
    }

    public void setErrors(List errors) {
        this.errors = errors;
    }
}