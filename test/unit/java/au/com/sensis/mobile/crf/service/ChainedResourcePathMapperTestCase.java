package au.com.sensis.mobile.crf.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link ChainedResourcePathMapper}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ChainedResourcePathMapperTestCase extends AbstractJUnit4TestCase {

    private ChainedResourcePathMapper objectUnderTest;
    private ResourcePathMapper mockRsourcePathMapper1;
    private ResourcePathMapper mockRsourcePathMapper2;
    private final GroupTestData groupTestData = new GroupTestData();
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        swapOutRealLoggerForMock(ChainedResourcePathMapper.class);

        setObjectUnderTest(new ChainedResourcePathMapper(Arrays.asList(
                getMockRsourcePathMapper1(), getMockRsourcePathMapper2())));
    }

    @Test
    public void testConstructorWithNullResourcePathMappers() throws Throwable {
        try {
            new ChainedResourcePathMapper(null);

            Assert.fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {

            Assert.assertEquals("IllegalArgumentException has wrong message",
                    "resourcePathMappers must not be null", e.getMessage());
        }
    }

    @Test
    public void testConstructorWithEmptyResourcePathMappers() throws Throwable {

        EasyMock.expect(
                getMockLogger(ChainedResourcePathMapper.class).isEnabledFor(
                        Level.WARN)).andReturn(Boolean.TRUE);

        getMockLogger(ChainedResourcePathMapper.class).warn(
                "resourcePathMappers is empty. "
                        + "This ChainedResourcePathMapper will always "
                        + "return a NullMappedResourcePath.");

        replay();

        new ChainedResourcePathMapper(new ArrayList<ResourcePathMapper>());

    }

    @Test
    public void testMapResourcePathWhenFound() throws Throwable {

        EasyMock.expect(getMockRsourcePathMapper1().resolve(
                getRequestedPath(), getGroup()))
                .andReturn(new ArrayList<MappedResourcePath>());

        EasyMock.expect(getMockRsourcePathMapper2().resolve(
                getRequestedPath(), getGroup()))
                .andReturn(getExpectedMappedResourcePaths());

        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve(getRequestedPath(),
                        getGroup());

        Assert.assertEquals("actualMappedResourcePath is wrong",
                getExpectedMappedResourcePaths(), actualMappedResourcePaths);
    }

    @Test
    public void testMapResourcePathWhenNotFound() throws Throwable {

        EasyMock.expect(
                getMockRsourcePathMapper1().resolve(getRequestedPath(),
                        getGroup())).andReturn(new ArrayList<MappedResourcePath>());

        EasyMock.expect(
                getMockRsourcePathMapper2().resolve(getRequestedPath(),
                        getGroup())).andReturn(new ArrayList<MappedResourcePath>());

        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().resolve(getRequestedPath(),
                        getGroup());

        Assert.assertNotNull("actualMappedResourcePath should not be null",
                actualMappedResourcePaths);
        Assert.assertTrue("actualMappedResourcePath should not be empty",
                actualMappedResourcePaths.isEmpty());
    }

    private List<MappedResourcePath> getExpectedMappedResourcePaths() {
        return Arrays.asList(getResourcePathTestData().getMappedIphoneGroupResourcePath());
    }

    private Group getGroup() {
        return getGroupTestData().createIPhoneGroup();
    }

    private String getRequestedPath() {
        return getResourcePathTestData()
                .getRequestedJspResourcePath();
    }

    /**
     * @return the objectUnderTest
     */
    private ChainedResourcePathMapper getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ChainedResourcePathMapper objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockRsourcePathMapper2
     */
    public ResourcePathMapper getMockRsourcePathMapper2() {
        return mockRsourcePathMapper2;
    }

    /**
     * @param mockRsourcePathMapper2 the mockRsourcePathMapper2 to set
     */
    public void setMockRsourcePathMapper2(final ResourcePathMapper mockRsourcePathMapper2) {
        this.mockRsourcePathMapper2 = mockRsourcePathMapper2;
    }

    /**
     * @return the mockRsourcePathMapper1
     */
    public ResourcePathMapper getMockRsourcePathMapper1() {
        return mockRsourcePathMapper1;
    }

    /**
     * @param mockRsourcePathMapper1 the mockRsourcePathMapper1 to set
     */
    public void setMockRsourcePathMapper1(final ResourcePathMapper mockRsourcePathMapper1) {
        this.mockRsourcePathMapper1 = mockRsourcePathMapper1;
    }

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }
}
