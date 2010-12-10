package au.com.sensis.mobile.crf.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link BundleFactory}.
 *
 * @author Tony Filipe
 */
public class BundleFactoryTestCase extends AbstractJUnit4TestCase {

    private static final String FILE1_CONTENT_EXTRACT = "#file1";
    private static final String FILE2_CONTENT_EXTRACT = "#file2";

    private static String anExistingTestCssFile1 =
        "/au/com/sensis/mobile/crf/service/bundleFactoryTestData/cssFile1.css";
    private static String anExistingTestCssFile2 =
        "/au/com/sensis/mobile/crf/service/bundleFactoryTestData/cssFile2.css";

    private ResourcePathTestData resourcePathTestData = new ResourcePathTestData();

    private BundleFactory objectUnderTest;

    private Resource mockResource;
    private Resource mockResource2;


    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new BundleFactory());
    }

    @Test
    public void testGetBundleWithInvalidResources() {

        try {
            final List<Resource> resources = new ArrayList<Resource>();
            resources.add(getResourcePathTestData().getMappedDefaultGroupCssResourcePath());
            resources.add(getResourcePathTestData().getMappedIphoneGroupCssResourcePath());

            getObjectUnderTest().getBundle(resources);

            Assert.fail("IOException expected");
        } catch (final IOException e) {
            // Correct, we expect a IOException to be thrown
        }
    }

    @Test
    public void testGetBundleWithValidResources() throws Exception {

        final List<Resource> resources = new ArrayList<Resource>();
        resources.add(mockResource);
        resources.add(mockResource2);

        recordExpectationsForResource(mockResource, anExistingTestCssFile1);
        recordExpectationsForLastResource(mockResource2, anExistingTestCssFile2);

        replay();

        final Resource bundle = getObjectUnderTest().getBundle(resources);

        final String bundleContents = readFileContents(bundle.getNewFile());

        Assert.assertTrue(bundle.getNewPath().contains("/bundle"));
        Assert.assertTrue(bundleContents.contains(FILE1_CONTENT_EXTRACT));
        Assert.assertTrue(bundleContents.contains(FILE2_CONTENT_EXTRACT));
    }


    // HELPER METHODS

    private void recordExpectationsForLastResource(final Resource mockResource,
            final String fileName) throws URISyntaxException {

        final File rootResourceDir = new File(getRootResourceDir(fileName));

        EasyMock.expect(mockResource.getRootResourceDir()).andReturn(rootResourceDir).times(2);
        EasyMock.expect(mockResource.getNewPath()).andReturn(fileName).times(4);

        EasyMock.expect(mockResource.getOriginalPath()).andReturn(fileName);
        EasyMock.expect(mockResource.getGroup()).andReturn(new Group());

        recordExpectationsForResource(mockResource, fileName);
    }

    private void recordExpectationsForResource(final Resource mockResource,
            final String fileName) throws URISyntaxException {

        final File cssFile = new File(this.getClass().getResource(fileName).toURI());

        EasyMock.expect(mockResource.getNewFile()).andReturn(cssFile);
    }

    private String getRootResourceDir(final String fileName) throws URISyntaxException {

        final String filepath = this.getClass().getResource(fileName).toURI().toString();

        final int endIndex = filepath.indexOf(fileName);
        int startIndex = 0;
        if (filepath.indexOf("file:/") > -1) {
            startIndex = 6;
        }

        return filepath.substring(startIndex, endIndex);
    }

    private String readFileContents(final File filePath) throws IOException  {

        final BufferedReader in = new BufferedReader(new FileReader(filePath));

        final StringBuilder content = new StringBuilder();
        String line;
        try {
            while ((line = in.readLine()) != null) {
                content.append(line);
                content.append(System.getProperty("line.separator"));
            }
        } finally {
            in.close();
        }

        return content.toString();
    }

    /**
     * @return the resourcePathTestData
     */
    public ResourcePathTestData getResourcePathTestData() {

        return resourcePathTestData;
    }

    /**
     * @return the mockResource
     */
    public Resource getMockResource() {

        return mockResource;
    }


    /**
     * @param mockResource  the mockResource to set
     */
    public void setMockResource(final Resource mockResource) {

        this.mockResource = mockResource;
    }


    /**
     * @return the mockResource2
     */
    public Resource getMockResource2() {

        return mockResource2;
    }


    /**
     * @param mockResource2  the mockResource2 to set
     */
    public void setMockResource2(final Resource mockResource2) {

        this.mockResource2 = mockResource2;
    }

    /**
     * @param resourcePathTestData  the resourcePathTestData to set
     */
    public void setResourcePathTestData(final ResourcePathTestData resourcePathTestData) {

        this.resourcePathTestData = resourcePathTestData;
    }

    /**
     * @return the objectUnderTest
     */
    private BundleFactory getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final BundleFactory objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }
}
