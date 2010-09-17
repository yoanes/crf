package au.com.sensis.mobile.crf.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.DefaultGroup;
import au.com.sensis.mobile.crf.config.Group;

/**
 * Unit test {@link DefaultGroup}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DefaultGroupTestCase extends Group {

    private DefaultGroup objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new DefaultGroup());
    }

    @Test
    public void testGetExpr() throws Throwable {

        Assert.assertEquals("expr is wrong", "true",
                getObjectUnderTest().getExpr());
    }

    @Test
    public void testSetExpr() throws Throwable {

        try {
            getObjectUnderTest().setExpr("false");

            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {

            Assert.assertEquals("IllegalStateException has wrong message",
                    "It is illegal to set the expr for DefaultGroup.", e
                            .getMessage());
        }
    }

    /**
     * @return the objectUnderTest
     */
    private DefaultGroup getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final DefaultGroup objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }


}
