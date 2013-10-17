package sp.model.dwr;

import java.util.Map;
import java.util.Set;

/**
 * Interface for system state
 *
 * @author Paul Kulitski
 */
public interface State {

    public Map getState();

    public Set getStateNames();

    public String getStateByName(String name);

    public void setState(String name, String value);
}
