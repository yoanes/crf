package au.com.sensis.mobile.crf.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple builder for building an MD5 sum from multiple pieces of content.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MD5Builder {
    private static final int HEX_RADIX = 16;
    private final MessageDigest digest;

    /**
     * Default constructor.
     *
     * @throws NoSuchAlgorithmException
     *             Thrown if the MD5 algorithm cannot be loaded. See
     *             {@link MessageDigest#getInstance(String)} for further
     *             details.
     */
    public MD5Builder() throws NoSuchAlgorithmException {
        digest = MessageDigest.getInstance("MD5");
    }

    /**
     * Add content for the MD5 sum to be calculated from.
     *
     * @param content Additional String for the MD5 sum to be calculated from.
     */
    public void add(final String content) {
        getDigest().update(content.getBytes());
    }

    /**
     * @return the MD5 sum returned as a hexidecimal encoded String.
     */
    public String getSumAsHex() {
        final byte[] md5sum = getDigest().digest();
        final BigInteger bigInt = new BigInteger(1, md5sum);
        return bigInt.toString(HEX_RADIX);
    }

    /**
     * @return the digest/
     */
    private MessageDigest getDigest() {
        return digest;
    }


}
