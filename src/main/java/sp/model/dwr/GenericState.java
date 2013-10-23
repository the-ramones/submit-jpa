package sp.model.dwr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Basic {@link State} implementation suitable for inheritance
 *
 * @author Paul Kulitski
 */
public class GenericState implements State {

    private final HashMap state = GenericState.GenericStateHolder.getInstance();

    private static class GenericStateHolder {

        private static HashMap instance = new HashMap(32);

        public static HashMap getInstance() {
            return instance;
        }
    }

    public GenericState() {
    }

    @Override
    public Map getState() {
        return state;
    }

    @Override
    public Set getStateNames() {
        return state.keySet();
    }

    @Override
    public String getStateByName(String name) {
        synchronized (state) {
            return (String) state.get(name);
        }
    }

    @Override
    public void setState(String name, String value) {
        synchronized (state) {
            state.put(name, value);
        }
    }
}
