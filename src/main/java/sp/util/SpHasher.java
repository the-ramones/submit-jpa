package sp.util;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Reports! hasher utility. Thread-safe. Use as hash factory. Utilizes
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
        int[] initialAndMultiplier = getInitialAndMultiplier();
        return String.valueOf(new HashCodeBuilder(
                initialAndMultiplier[0], initialAndMultiplier[1]).hashCode());
    }

    /**
     * Generate a random hash value with salt
     *
     * @param salt array of objects to be used as salt (timestamps or date)
     * @return hash value generated
     */
    public static synchronized String getHash(Object[] salt) {
        int[] initialAndMultiplier = getInitialAndMultiplier();
        HashCodeBuilder hash = new HashCodeBuilder(
                initialAndMultiplier[0], initialAndMultiplier[1]);
        for (Object obj : salt) {
            hash.append(obj);
        }
        return String.valueOf(hash.hashCode());
    }

    private static synchronized int[] getInitialAndMultiplier() {
        int init = Math.round((float) Math.random() * (~((int) 1 << 31))) | 1;
        int multiplier = Math.round((float) Math.random() * (~((int) 1 << 31))) | 1;
        return new int[]{init, multiplier};
    }
}
