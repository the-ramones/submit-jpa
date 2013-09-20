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
public class AjaxResponse<T> {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    private String status;
    private List<T> results;
    private List<ErrorDetails> errors;

    public AjaxResponse() {
    }

    public AjaxResponse(String status) {
        this.status = status;
    }

    public AjaxResponse(String status, List<T> results, List<ErrorDetails> errors) {
        this.status = status;
        this.results = results;
        this.errors = errors;
    }

    public AjaxResponse(String status, T result, ErrorDetails error) {
        this.status = status;
        this.results = new ArrayList<T>();
        this.results.add(result);
        this.errors = new ArrayList<ErrorDetails>();
        this.errors.add(error);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public List<ErrorDetails> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetails> errors) {
        this.errors = errors;
    }

    public void addError(ErrorDetails error) {
        if (this.errors == null) {
            this.errors = new ArrayList<ErrorDetails>();
            this.errors.add(error);
        } else {
            this.errors.add(error);
        }
    }

    public void addResult(T result) {
        if (this.results == null) {
            this.results = new ArrayList<T>();
            this.results.add(result);
        } else {
            this.results.add(result);
        }
    }

    public T getSingleResult() {
        if (this.results.size() > 0) {
            return this.results.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "AjaxResponse{" + "status=" + status + ", results=" + results + ", errors=" + errors + '}';
    }

    public static class ErrorDetails {

        public static final String OBJECT_ERROR = "object";
        public static final String FIELD_ERROR = "field";
        private String type;
        private String target;
        private Object value;
        private String message;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getError() {
            return target;
        }

        public void setError(String target) {
            this.target = target;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public ErrorDetails() {
        }

        public ErrorDetails(String type, String target, Object value, String message) {
            this.type = type;
            this.target = target;
            this.message = message;
            this.value = value;
        }

        @Override
        public String toString() {
            return "ErrorDetails{" + "type=" + type + ", target=" + target + ", value=" + value + ", message=" + message + '}';
        }
    }
}
