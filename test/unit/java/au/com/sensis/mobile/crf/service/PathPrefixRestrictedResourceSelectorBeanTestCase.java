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
 * Unit test {@link PathPrefixRestrictedResourceSelectorBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class PathPrefixRestrictedResourceSelectorBeanTestCase
        extends AbstractJUnit4TestCase {

    private static final String [] WILDCARD_EXTENSIONS = new String [] {"*"};

    private PathPrefixRestrictedResourceSelectorBean objectUnderTest;
    private ResourceSelector mockResourceSelector;
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
        setObjectUnderTest(new PathPrefixRestrictedResourceSelectorBean(
                getResourcePathTestData().getMapComponentPathPrefix(),
                getMockResourceSelector()));
    }

    @Test
    public void testConstructorWhenPathPrefixIsBlank() throws Throwable {
        final String[] testValues =
                new String[] { null, StringUtils.EMPTY, " ", "  " };
        for (final String testValue : testValues) {
            try {
                new PathPrefixRestrictedResourceSelectorBean(testValue,
                        getMockResourceSelector());

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
    public void testConstructorWhenResourceSelectorIsNull() throws Throwable {
        try {
            new PathPrefixRestrictedResourceSelectorBean(
                    getResourcePathTestData().getMapComponentPathPrefix(), null);
            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourceSelector must not be null", e
                            .getMessage());
        }
    }

    @Test
    public void testGetResourcePathWhenPrefixMatches() throws Throwable {

        EasyMock.expect(
                getMockResourceSelector().getResourcePath(
                        getMockDevice(),
                        getRequestedMatchedResourcePath()))
                .andReturn(
                        getExpectedMappedResourcePath());
        replay();

        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest().getResourcePath(
                        getMockDevice(),
                        getRequestedMatchedResourcePath());

        Assert.assertEquals("actualMappedResourcePath is wrong",
                getExpectedMappedResourcePath(),
                actualMappedResourcePath);
    }

    private String getRequestedMatchedResourcePath() {
        return getResourcePathTestData()
                .getMapComponentRequestedJspResourcePath();
    }

    @Test
    public void testGetResourcePathWhenPrefixDoesNotMatch() throws Throwable {
        final NullMappedResourcePath expectedMappedResourcePath =
                new NullMappedResourcePath(getRequestedUnmatchedResourcePath());

        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest().getResourcePath(
                        getMockDevice(),
                        getRequestedUnmatchedResourcePath());

        Assert.assertEquals("actualMappedResourcePath is wrong",
                expectedMappedResourcePath,
                actualMappedResourcePath);

    }

    private String getRequestedUnmatchedResourcePath() {
        return getResourcePathTestData()
                .getRequestedJspResourcePath();
    }

    @Test
    public void testGetResourcePathWithExtensionsWhenPrefixMatches()
            throws Throwable {

        EasyMock.expect(
                getMockResourceSelector().getResourcePathWithExtensions(
                        getMockDevice(),
                        getRequestedMatchedResourcePath(),
                        WILDCARD_EXTENSIONS)).andReturn(
                getExpectedMappedResourcePath());
        replay();

        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest().getResourcePathWithExtensions(
                        getMockDevice(),
                        getRequestedMatchedResourcePath(),
                        WILDCARD_EXTENSIONS);

        Assert.assertEquals("actualMappedResourcePath is wrong",
                getExpectedMappedResourcePath(),
                actualMappedResourcePath);
    }

    @Test
    public void testGetResourcePathWithExtensionsWhenPrefixDoesNotMatch()
            throws Throwable {
        final NullMappedResourcePath expectedMappedResourcePath =
                new NullMappedResourcePath(getRequestedUnmatchedResourcePath());

        final MappedResourcePath actualMappedResourcePath =
                getObjectUnderTest()
                        .getResourcePathWithExtensions(
                                getMockDevice(),
                                getRequestedUnmatchedResourcePath(),
                                WILDCARD_EXTENSIONS);

        Assert.assertEquals("actualMappedResourcePath is wrong",
                expectedMappedResourcePath, actualMappedResourcePath);
    }

    @Test
    public void testGetAllResourcePathsWhenPrefixMatches() throws Throwable {

        EasyMock.expect(
                getMockResourceSelector().getAllResourcePaths(
                        getMockDevice(),
                        getRequestedMatchedResourcePath()))
                .andReturn(
                        getExpectedMappedResourcePaths());
        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().getAllResourcePaths(
                        getMockDevice(),
                        getRequestedMatchedResourcePath());

        Assert.assertEquals("actualMappedResourcePath is wrong",
                getExpectedMappedResourcePaths(),
                actualMappedResourcePaths);
    }

    @Test
    public void testGetAllResourcePathsWhenPrefixDoesNotMatch()
        throws Throwable {
        final List<MappedResourcePath> actualMappedResourcePaths =
            getObjectUnderTest().getAllResourcePaths(getMockDevice(),
                    getRequestedUnmatchedResourcePath());

        Assert
        .assertEquals("actualMappedResourcePath is wrong",
                getExpectedNullMappedResourcePaths(),
                actualMappedResourcePaths);

    }

    private MappedResourcePath getExpectedMappedResourcePath() {
        return getResourcePathTestData()
                .getMappedAppleGroupResourcePath();
    }
    private List<MappedResourcePath> getExpectedMappedResourcePaths() {
        return Arrays.asList(getExpectedMappedResourcePath());
    }


    private List<MappedResourcePath> getExpectedNullMappedResourcePaths() {
        final List<MappedResourcePath> expectedMappedResourcePaths =
                new ArrayList<MappedResourcePath>();
        expectedMappedResourcePaths.add(new NullMappedResourcePath(
                getRequestedUnmatchedResourcePath()));
        return expectedMappedResourcePaths;
    }


    private PathPrefixRestrictedResourceSelectorBean getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(
            final PathPrefixRestrictedResourceSelectorBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    public ResourceSelector getMockResourceSelector() {
        return mockResourceSelector;
    }

    public void setMockResourceSelector(
            final ResourceSelector mockResourceSelector) {
        this.mockResourceSelector = mockResourceSelector;
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
