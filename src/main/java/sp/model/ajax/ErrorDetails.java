package sp.model.ajax;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Holds details of validation errors. Instances is created on the server,
 * serialized by xml/json-mappers (e.g. Jackson) and sends to the client in
 * serialized form. Convenient storage for error type, target field, raw field
 * value and localized constraint violation message (checked and injected) on
 * the server side.
 *
 * @author Kulitski Paul
 */
@XmlRootElement
public class ErrorDetails implements Serializable {

    public static final String OBJECT_ERROR = "object";
    public static final String FIELD_ERROR = "field";
    /**
     * Type of error: object or field
     *
     * @see FieldError
     * @see ObjectError
     */
    private String type;
    /**
     * Target field name, where validation constraint fails
     */
    private String field;
    /**
     * Field value that failed under validation constraint
     */
    private Object value;
    /**
     * Localized constraint violation message. Inject {@link MessageSource} in
     * controller class and retrieve localized message from that source
     */
    private String message;

    public String getType() {
        return type;
    }

    public String getField() {
        return field;
    }

    public void setField(String target) {
        this.field = target;
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

    public ErrorDetails(String type) {
    }

    public ErrorDetails(String type, String field, Object value, String message) {
        this.type = type;
        this.field = field;
        this.message = message;
        this.value = value;
    }

    @Override
    public String toString() {
        return "ErrorDetails{" + "type=" + type + ", field=" + field + ", value=" + value + ", message=" + message + '}';
    }
}
