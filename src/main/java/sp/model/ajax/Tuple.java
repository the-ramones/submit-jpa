package sp.model.ajax;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Tuple interface for convenient AJAX interaction.
 *
 * Issue: replace with JDK 1.5 Generics, needs additional Jackson Mapper
 * configuration to work with
 *
 * @author Paul Kulitski
 */
@XmlRootElement
public class Tuple implements Serializable {

    private String X;
    private String Y;

    public Tuple() {
    }

    public Tuple(String X, String Y) {
        this.X = X;
        this.Y = Y;
    }

    public String getX() {
        return X;
    }

    public void setX(String X) {
        this.X = X;
    }

    public String getY() {
        return Y;
    }

    public void setY(String Y) {
        this.Y = Y;
    }
}
