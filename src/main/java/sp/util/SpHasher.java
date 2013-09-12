package sp.util;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Reports! hasher utility Thread-safe. Use as hash factory. Utilizes
 * {@link org.apache.commons.lang.builder.HashCodeBuilder}
 *
 * @author Paul Kulitski
 */
public class SpHasher {

    /**
     * Generate a random hash value
     *
     * @return hash value generated
     */
    public static synchronized String getRandomHash() {
        int init = (int) Math.random() * ((int) 1 << 30);
        int multiplier = (int) Math.random() * ((int) 1 << 30);
        return String.valueOf(new HashCodeBuilder(init, multiplier).hashCode());
    }

    /**
     * Generate a random hash value with salt
     *
     * @param salt array of objects to be used as salt (timestamps or date)
     * @return hash value generated
     */
    public static synchronized String getHash(Object[] salt) {
        int init = (int) Math.random() * ((int) 1 << 30);
        int multiplier = (int) Math.random() * ((int) 1 << 30);
        HashCodeBuilder hash = new HashCodeBuilder(init, multiplier);
        for (Object obj : salt) {
            hash.append(obj);
        }
        return String.valueOf(hash.hashCode());
    }
}
