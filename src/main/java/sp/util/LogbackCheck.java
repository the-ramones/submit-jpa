package sp.util;

/**
 * Check 'logback.xml' accessibility
 *
 * @author Paul Kulitski
 */
public class LogbackCheck {

    public static void main(String[] args) {
        String URL = "logback.xml";
        System.out.println(ClassLoader.getSystemResource(URL));
    }
}
