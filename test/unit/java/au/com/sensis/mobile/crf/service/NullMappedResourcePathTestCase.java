package au.com.sensis.mobile.crf.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link NullMappedResourcePath}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class NullMappedResourcePathTestCase extends AbstractJUnit4TestCase {

    private NullMappedResourcePath objectUnderTest;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new NullMappedResourcePath(getResourcePathTestData()
                .getRequestedJspResourcePath()));
    }

    @Test
    public void testIsIdentityMappingMustBeTrue() throws Throwable {

        Assert.assertTrue("isIdentityMapping() must be true",
                getObjectUnderTest().isIdentityMapping());
    }

    @Test
    public void testExistsMustBeFalse() throws Throwable {
        Assert.assertFalse("exists must be false",
                getObjectUnderTest().exists());
    }

    @Test
    public void testEndsWithDotNullMustBeFalse() throws Throwable {
        Assert.assertFalse("endsWithDotNull must be false",
                getObjectUnderTest().endsWithDotNull());
    }

    /**
     * @return the objectUnderTest
     */
    private NullMappedResourcePath getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final NullMappedResourcePath objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }
}
