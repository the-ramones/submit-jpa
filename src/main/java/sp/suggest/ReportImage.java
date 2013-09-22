package sp.suggest;

/**
 * Instance that holds an image (short representation of an report)
 *
 * @author Paul Kulitski
 */
public class ReportImage {

    private Long identifier;
    private String representation;

    public Long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Long identifier) {
        this.identifier = identifier;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    public ReportImage() {
    }

    public ReportImage(Long identifier, String representation) {
        this.identifier = identifier;
        this.representation = representation;
    }
}
