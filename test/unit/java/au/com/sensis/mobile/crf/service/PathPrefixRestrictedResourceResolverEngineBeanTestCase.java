package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link PathPrefixRestrictedResourceResolverEngineBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PathPrefixRestrictedResourceResolverEngineBeanTestCase
        extends AbstractJUnit4TestCase {

    private PathPrefixRestrictedResourceResolverEngineBean objectUnderTest;
    private ResourceResolverEngine mockResourceResolverEngine;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private Device mockDevice;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new PathPrefixRestrictedResourceResolverEngineBean(
                getResourcePathTestData().getMapComponentPathPrefix(),
                getMockResourceResolverEngine()));
    }

    @Test
    public void testConstructorWhenPathPrefixIsBlank() throws Throwable {
        final String[] testValues =
                new String[] { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new PathPrefixRestrictedResourceResolverEngineBean(testValue,
                        getMockResourceResolverEngine());

                Assert
                        .fail("IllegalArgumentException expected for testValue: '"
                                + testValue + "'");
            } catch (final IllegalArgumentException e) {
                Assert.assertEquals(
                        "IllegalArgumentException has wrong message",
                        "pathPrefix must not be blank: '" + testValue + "'", e
                                .getMessage());
            }
        }
    }

    @Test
    public void testConstructorWhenResourceResolverEngineIsNull() throws Throwable {
        try {
            new PathPrefixRestrictedResourceResolverEngineBean(
                    getResourcePathTestData().getMapComponentPathPrefix(), null);
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceResolverEngine must not be null", e
                            .getMessage());
        }
    }

    @Test
    public void testGetResourcePathWhenPrefixMatches() throws Throwable {

        EasyMock.expect(
                getMockResourceResolverEngine().getResourcePath(
                        getMockDevice(),
                        getRequestedMatchedResourcePath()))
                .andReturn(
                        getExpectedResource());
        replay();

        final Resource actualResource =
                getObjectUnderTest().getResourcePath(
                        getMockDevice(),
                        getRequestedMatchedResourcePath());

        Assert.assertEquals("actualResource is wrong",
                getExpectedResource(),
                actualResource);
    }

    private String getRequestedMatchedResourcePath() {
        return getResourcePathTestData()
                .getMapComponentRequestedJspResourcePath();
    }

    @Test
    public void testGetResourcePathWhenPrefixDoesNotMatch() throws Throwable {
        final Resource actualResource =
                getObjectUnderTest().getResourcePath(
                        getMockDevice(),
                        getRequestedUnmatchedResourcePath());

        Assert.assertNull("actualResource is wrong",
                actualResource);

    }

    private String getRequestedUnmatchedResourcePath() {
        return getResourcePathTestData()
                .getRequestedJspResourcePath();
    }

    @Test
    public void testGetAllResourcePathsWhenPrefixMatches() throws Throwable {

        EasyMock.expect(
                getMockResourceResolverEngine().getAllResourcePaths(
                        getMockDevice(),
                        getRequestedMatchedResourcePath()))
                .andReturn(
                        getExpectedResources());
        replay();

        final List<Resource> actualResources =
                getObjectUnderTest().getAllResourcePaths(
                        getMockDevice(),
                        getRequestedMatchedResourcePath());

        Assert.assertEquals("actualResource is wrong",
                getExpectedResources(),
                actualResources);
    }

    @Test
    public void testGetAllResourcePathsWhenPrefixDoesNotMatch()
        throws Throwable {
        final List<Resource> actualResources =
            getObjectUnderTest().getAllResourcePaths(getMockDevice(),
                    getRequestedUnmatchedResourcePath());

        Assert
        .assertEquals("actualResource is wrong",
                new ArrayList<Resource>(),
                actualResources);

    }

    private Resource getExpectedResource() {
        return getResourcePathTestData()
                .getMappedAppleGroupResourcePath();
    }

    private List<Resource> getExpectedResources() {
        return Arrays.asList(getExpectedResource());
    }

    private PathPrefixRestrictedResourceResolverEngineBean getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(
            final PathPrefixRestrictedResourceResolverEngineBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    public ResourceResolverEngine getMockResourceResolverEngine() {
        return mockResourceResolverEngine;
    }

    public void setMockResourceResolverEngine(
            final ResourceResolverEngine mockResourceResolverEngine) {
        this.mockResourceResolverEngine = mockResourceResolverEngine;
    }

    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    public Device getMockDevice() {
        return mockDevice;
    }

    public void setMockDevice(final Device mockDevice) {
        this.mockDevice = mockDevice;
    }
}
