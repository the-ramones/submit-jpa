package sp.model.ajax;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holder of prompts for Suggest feature
 *
 * @author Paul Kulitski
 */
@XmlRootElement
public class Prompt implements Serializable {

    String performer;
    String activity;
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Prompt(String performer, String activity, Long id) {
        this.performer = performer;
        this.activity = activity;
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(performer.length() * 2);
        sb.append(performer).append(" - ").append(activity);
        return sb.toString();
    }
}
