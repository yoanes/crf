package au.com.sensis.mobile.crf.service;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.util.TimeGeneratorFactory;
import au.com.sensis.mobile.crf.util.TimerGeneratorStub;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ResourceCacheEntryBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceCacheEntryBeanTestCase extends AbstractJUnit4TestCase {

    private static final int DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT = 1;
    private static final int DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS = 60000;

    private ResourceCacheEntryBean objectUnderTest;
    private TimerGeneratorStub timerGeneratorStub;
    private Resource mockResource;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setTimerGeneratorStub(new TimerGeneratorStub());
        TimeGeneratorFactory.changeDefaultTimeGeneratorSingleton(getTimerGeneratorStub());

        setObjectUnderTest(
            new ResourceCacheEntryBean(new Resource[] { getMockResource() },
                    DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                    DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS));
    }

    @Test
    public void testIsEmptyResourcesWhenTrue() throws Throwable {
        setObjectUnderTest(new ResourceCacheEntryBean(new Resource[] {},
                ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS));

        Assert.assertTrue("isEmptyResources() should be true", getObjectUnderTest()
                .isEmptyResources());
    }

    @Test
    public void testIsEmptyResourcesWhenNull() throws Throwable {
        setObjectUnderTest(new ResourceCacheEntryBean(null,
                ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS));

        Assert.assertTrue("isEmptyResources() should be true", getObjectUnderTest()
                .isEmptyResources());
    }

    @Test
    public void testIsEmptyResourcesWhenFalse() throws Throwable {

        Assert.assertFalse("isEmptyResources() should be false", getObjectUnderTest()
                .isEmptyResources());
    }

    @Test
    public void testRefreshCountLimitReachedWhenFalse() throws Throwable {
        getObjectUnderTest().incrementRefreshCountRateLimited();
        Assert.assertFalse("refrechCountLimitReached() should be false", getObjectUnderTest()
                .maxRefreshCountReached());
    }

    @Test
    public void testRefreshCountLimitReachedWhenTrue() throws Throwable {
        // TODo: fix this hardcoded timestamp update.
        getTimerGeneratorStub().setTimestamp(
                new Date(getTimerGeneratorStub().getTimeInMillis() + 80000));
        getObjectUnderTest().incrementRefreshCountRateLimited();
        Assert.assertTrue("refrechCountLimitReached() should be true", getObjectUnderTest()
                .maxRefreshCountReached());
    }

    /**
     * @return the objectUnderTest
     */
    private ResourceCacheEntryBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ResourceCacheEntryBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the timerGeneratorStub
     */
    private TimerGeneratorStub getTimerGeneratorStub() {
        return timerGeneratorStub;
    }

    /**
     * @param timerGeneratorStub the timerGeneratorStub to set
     */
    private void setTimerGeneratorStub(final TimerGeneratorStub timerGeneratorStub) {
        this.timerGeneratorStub = timerGeneratorStub;
    }

    /**
     * @return the mockResource
     */
    public Resource getMockResource() {
        return mockResource;
    }

    /**
     * @param mockResource the mockResource to set
     */
    public void setMockResource(final Resource mockResource) {
        this.mockResource = mockResource;
    }
}
