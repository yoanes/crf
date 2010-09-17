package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.service.JavaScriptMappedResourcePathBean.PathExpander;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link JavaScriptMappedResourcePathBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptMappedResourcePathBeanTestCase extends
        AbstractJUnit4TestCase {

    private JavaScriptMappedResourcePathBean objectUnderTest;
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private FileIoFacade mockFileIoFacade;
    private PathExpander mockPathExpander;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory.changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        // TODO: shouldn't need to cast.
        setObjectUnderTest((JavaScriptMappedResourcePathBean) getResourcePathTestData()
            .getMappedDefaultGroupBundledScriptBundleResourcePath());
        getObjectUnderTest().setPathExpander(getMockPathExpander());
    }

    /**
     * Tear down test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @After
    public void tearDown() throws Exception {
        FileIoFacadeFactory.restoreDefaultFileIoFacadeSingleton();
    }

    @Test
    public void testExistByPathExpansionWhenPathIsForABundleWhenFilesFound() throws Throwable {

        EasyMock.expect(getMockPathExpander().expandPath(getObjectUnderTest())).andReturn(
                createExistsByFilterExpectedFileFilterResults());
        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().existByExpansion();

        assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                createExistsByFilterExpectedMappedResourcePaths(), actualMappedResourcePaths);
    }

    @Test
    public void testExistByPathExpansionWhenPathIsForABundleWhenNoFilesFound() throws Throwable {

        EasyMock.expect(getMockPathExpander().expandPath(getObjectUnderTest())).andReturn(
                new ArrayList<File>());
        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
            getObjectUnderTest().existByExpansion();

        assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                new ArrayList<MappedResourcePath>(), actualMappedResourcePaths);
    }

    @Test
    public void testExistByPathExpansionWhenPathIsForABundleWhenNullFilesFound() throws Throwable {
        final MappedResourcePath mappedResourcePath =
            getResourcePathTestData()
            .getMappedDefaultGroupBundledScriptBundleResourcePath();
        // TODO: shouldn't have to cast.
        setObjectUnderTest((JavaScriptMappedResourcePathBean) mappedResourcePath);
        getObjectUnderTest().setPathExpander(getMockPathExpander());

        EasyMock.expect(getMockPathExpander().expandPath(mappedResourcePath)).andReturn(
                null);
        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
            getObjectUnderTest().existByExpansion();

        assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                new ArrayList<MappedResourcePath>(), actualMappedResourcePaths);
    }

    @Test
    public void testExistByPathExpansionWhenPathIsNotForABundle() throws Throwable {
        setObjectUnderTest((JavaScriptMappedResourcePathBean) getResourcePathTestData()
                .getMappedDefaultGroupNamedScriptBundleResourcePath());

        replay();

        final List<MappedResourcePath> actualMappedResourcePaths =
                getObjectUnderTest().existByExpansion();
        final List<MappedResourcePath> expectedMappedResourcePaths
            = Arrays.asList((MappedResourcePath) getObjectUnderTest());

        assertComplexObjectsEqual("actualMappedResourcePaths is wrong",
                expectedMappedResourcePaths, actualMappedResourcePaths);
    }

    private List<File> createExistsByFilterExpectedFileFilterResults() {
        final File expectedFile1 = new File(
                getResourcePathTestData().getMappedDefaultGroupBundledScriptResourcePath1()
                    .getRootResourceDir(),
                getResourcePathTestData().getMappedDefaultGroupBundledScriptResourcePath1()
                    .getNewResourcePath());
        final File expectedFile2 = new File(
                getResourcePathTestData().getMappedDefaultGroupBundledScriptResourcePath2()
                    .getRootResourceDir(),
                getResourcePathTestData().getMappedDefaultGroupBundledScriptResourcePath2()
                    .getNewResourcePath());
        final List<File> expectedFileFilterResults = Arrays.asList(expectedFile1, expectedFile2);
        return expectedFileFilterResults;
    }

    private List<MappedResourcePath> createExistsByFilterExpectedMappedResourcePaths() {
        return Arrays.asList(
            getResourcePathTestData()
                    .getMappedDefaultGroupBundledScriptResourcePath1(),
            getResourcePathTestData()
                    .getMappedDefaultGroupBundledScriptResourcePath2());
    }


    private JavaScriptMappedResourcePathBean getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(final JavaScriptMappedResourcePathBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }

    public FileIoFacade getMockFileIoFacade() {
        return mockFileIoFacade;
    }

    public void setMockFileIoFacade(final FileIoFacade mockFileIoFacade) {
        this.mockFileIoFacade = mockFileIoFacade;
    }

    public PathExpander getMockPathExpander() {
        return mockPathExpander;
    }

    public void setMockPathExpander(final PathExpander mockPathExpander) {
        this.mockPathExpander = mockPathExpander;
    }
}
