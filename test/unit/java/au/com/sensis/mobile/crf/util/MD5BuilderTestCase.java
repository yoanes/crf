package au.com.sensis.mobile.crf.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link MD5Builder}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MD5BuilderTestCase extends AbstractJUnit4TestCase {

    private MD5Builder objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new MD5Builder());
    }

    @Test
    public void testGetSumAsHexForSingleAddedContent() throws Throwable {
        getObjectUnderTest().add("hello");

        Assert.assertEquals("hex md5 sum is wrong", "5d41402abc4b2a76b9719d911017c592",
                getObjectUnderTest().getSumAsHex());
    }

    @Test
    public void testGetSumAsHexForMultipleAddedContent() throws Throwable {
        // NOTE: this test assumes that adding multiple strings to the build simply
        // results in them being concatenated prior to the md5 sum being calculated.
        getObjectUnderTest().add("hello");
        getObjectUnderTest().add("bye");

        Assert.assertEquals("hex md5 sum is wrong", "f13cbdeddee958db6e285da74605ba11",
                getObjectUnderTest().getSumAsHex());
    }

    /**
     * @return the objectUnderTest
     */
    private MD5Builder getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final MD5Builder objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }
}
