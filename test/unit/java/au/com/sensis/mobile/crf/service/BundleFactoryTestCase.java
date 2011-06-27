package au.com.sensis.mobile.crf.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.config.GroupTestData;
import au.com.sensis.mobile.crf.util.MD5Builder;
import au.com.sensis.mobile.crf.util.Minifier;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link BundleFactory}.
 *
 * @author Tony Filipe
 */
public class BundleFactoryTestCase extends AbstractJUnit4TestCase {

    private static final String CSS_FILE1_CONTENT_EXTRACT = "#file1";
    private static final String CSS_FILE2_CONTENT_EXTRACT = "#file2";

    private static final String JAVASCRIPT_FILE1_CONTENT_EXTRACT = "function file1()";
    private static final String JAVASCRIPT_FILE2_CONTENT_EXTRACT = "function file2()";

    private static String anExistingTestCssFile1Classpath =
        "/au/com/sensis/mobile/crf/service/bundleFactoryTestData/cssFile1.css";
    private static String anExistingTestCssFile2Classpath =
        "/au/com/sensis/mobile/crf/service/bundleFactoryTestData/cssFile2.css";
    private static String anExistingCssBundleFileClasspath =
        "/au/com/sensis/mobile/crf/service/bundleFactoryTestData/"
            + "-bundle-c21f969b5f03d33d43e04f8f136e7682-cssFile.css";

    private static String anExistingTestJavaScriptFile1Classpath =
        "/au/com/sensis/mobile/crf/service/bundleFactoryTestData/jsFile1.js";
    private static String anExistingTestJavaScriptFile2Classpath =
        "/au/com/sensis/mobile/crf/service/bundleFactoryTestData/jsFile2.js";
    private static String anExistingJavaScriptBundleFileClasspath =
        "/au/com/sensis/mobile/crf/service/bundleFactoryTestData/"
            + "-bundle-761079f6d08f961eb60330113c5fda28-jsFile.js";

    private static File anExistingTestCssFile1;
    private static File anExistingTestJavaScriptFile1;
    private static File anExistingTestCssFile2;
    private static File anExistingTestJavaScriptFile2;

    private static File anExistingCssBundleFile;
    private static File anExistingJavaScriptBundleFile;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private final GroupTestData groupTestData = new GroupTestData();

    private BundleFactory objectUnderTest;

    private Resource mockResource;
    private Resource mockResource2;
    private Resource mockResource3;

    private Minifier mockMinifier;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new BundleFactory());

        getObjectUnderTest().setMinifier(getMockMinifier());

        setAnExistingTestCssFile1(new File(this.getClass().getResource(
                getAnExistingTestCssFile1Classpath()).toURI()));
        setAnExistingTestCssFile2(new File(this.getClass().getResource(
                getAnExistingTestCssFile2Classpath()).toURI()));

        setAnExistingTestJavaScriptFile1(new File(this.getClass().getResource(
                getAnExistingTestJavaScriptFile1Classpath()).toURI()));
        setAnExistingTestJavaScriptFile2(new File(this.getClass().getResource(
                getAnExistingTestJavaScriptFile2Classpath()).toURI()));

        setAnExistingCssBundleFile(new File(this.getClass().getResource(
                getAnExistingCssBundleFileClasspath()).toURI()));
        setAnExistingJavaScriptBundleFile(new File(this.getClass().getResource(
                getAnExistingJavaScriptBundleFileClasspath()).toURI()));
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
    public void testGetBundleWithValidCssResourcesAndNoMinification() throws Exception {

        getObjectUnderTest().setDoMinification(false);

        final List<Resource> resources = new ArrayList<Resource>();
        resources.add(mockResource);
        resources.add(mockResource2);
        resources.add(mockResource3);

        recordExpectationsForResource(mockResource, getAnExistingTestCssFile1(), getGroupTestData()
                .createIPhoneGroup());
        recordExpectationsForLastResource(mockResource2, getAnExistingTestCssFile2Classpath(),
                getAnExistingTestCssFile2(), getGroupTestData().createAppleGroup());
        recordExpectationsForBundleResource(mockResource3,
                getAnExistingCssBundleFile());

        replay();

        final Resource bundle = getObjectUnderTest().getBundle(resources);

        final String bundleContents = readFileContents(bundle.getNewFile());

        Assert.assertTrue("generated bundle path is incorrect. Was: '" + bundle.getNewPath() + "'",
                bundle.getNewPath().endsWith(
                        "/-bundle-" + createExpectedGroupsMd5Sum() + "-"
                                + getAnExistingTestCssFile2().getName()));
        Assert.assertTrue("generated bundle does not contain contents of file 1", bundleContents
                .contains(CSS_FILE1_CONTENT_EXTRACT));
        Assert.assertTrue("generated bundle does not contain contents of file 2", bundleContents
                .contains(CSS_FILE2_CONTENT_EXTRACT));
    }

    @Test
    public void testGetBundleWithValidCssResourcesAndMinification() throws Exception {

        getObjectUnderTest().setDoMinification(true);

        final List<Resource> resources = new ArrayList<Resource>();
        resources.add(mockResource);
        resources.add(mockResource2);

        recordExpectationsForResource(mockResource, getAnExistingTestCssFile1(), getGroupTestData()
                .createIPhoneGroup());
        recordExpectationsForLastResource(mockResource2, getAnExistingTestCssFile2Classpath(),
                getAnExistingTestCssFile2(), getGroupTestData().createAppleGroup());

        getMockMinifier().minifyCss(EasyMock.isA(Reader.class), EasyMock.isA(Writer.class));

        replay();

        final Resource bundle = getObjectUnderTest().getBundle(resources);

        final String bundleContents = readFileContents(bundle.getNewFile());

        // The minifier is mocked so the bundleContents will simply be empty.
        Assert.assertEquals("bundleContents is wrong after minification", StringUtils.EMPTY,
                bundleContents);
    }

    @Test
    public void testGetBundleWithValidJavaScriptResourcesAndNoMinification() throws Exception {

        getObjectUnderTest().setDoMinification(false);

        final List<Resource> resources = new ArrayList<Resource>();
        resources.add(mockResource);
        resources.add(mockResource2);
        resources.add(mockResource3);

        recordExpectationsForResource(mockResource, getAnExistingTestJavaScriptFile1(),
                getGroupTestData().createIPhoneGroup());
        recordExpectationsForLastResource(mockResource2,
                getAnExistingTestJavaScriptFile2Classpath(), getAnExistingTestJavaScriptFile2(),
                getGroupTestData().createAppleGroup());
        recordExpectationsForBundleResource(mockResource3,
                getAnExistingJavaScriptBundleFile());

        replay();

        final Resource bundle = getObjectUnderTest().getBundle(resources);

        final String bundleContents = readFileContents(bundle.getNewFile());

        Assert.assertTrue("generated bundle path is incorrect. Was: '" + bundle.getNewPath() + "'",
                bundle.getNewPath().endsWith(
                        "/-bundle-" + createExpectedGroupsMd5Sum() + "-"
                                + getAnExistingTestJavaScriptFile2().getName()));
        Assert.assertTrue("generated bundle does not contain contents of file 1", bundleContents
                .contains(JAVASCRIPT_FILE1_CONTENT_EXTRACT));
        Assert.assertTrue("generated bundle does not contain contents of file 2", bundleContents
                .contains(JAVASCRIPT_FILE2_CONTENT_EXTRACT));
    }

    @Test
    public void testGetBundleWithValidJavaScriptResourcesAndMinification() throws Exception {

        getObjectUnderTest().setDoMinification(true);

        final List<Resource> resources = new ArrayList<Resource>();
        resources.add(mockResource);
        resources.add(mockResource2);

        recordExpectationsForResource(mockResource, getAnExistingTestJavaScriptFile1(),
                getGroupTestData().createIPhoneGroup());
        recordExpectationsForLastResource(mockResource2,
                getAnExistingTestJavaScriptFile2Classpath(), getAnExistingTestJavaScriptFile2(),
                getGroupTestData().createAppleGroup());

        getMockMinifier().minifyJavaScript(EasyMock.isA(Reader.class), EasyMock.isA(Writer.class));

        replay();

        final Resource bundle = getObjectUnderTest().getBundle(resources);

        final String bundleContents = readFileContents(bundle.getNewFile());

        // The minifier is mocked so the bundleContents will simply be empty.
        Assert.assertEquals("bundleContents is wrong after minification", StringUtils.EMPTY,
                bundleContents);
    }


    private String createExpectedGroupsMd5Sum() throws NoSuchAlgorithmException {
        // Yeah, we rely on the exact same class that the object under test will use but
        // how else would you code this test?
        final MD5Builder md5Builder = new MD5Builder();

        md5Builder.add(getGroupTestData().createIPhoneGroup().getName());
        md5Builder.add(getGroupTestData().createAppleGroup().getName());
        return md5Builder.getSumAsHex();
    }

    // HELPER METHODS

    private void recordExpectationsForLastResource(final Resource mockResource,
            final String fileClasspath, final File file, final Group group)
            throws URISyntaxException {

        EasyMock.expect(mockResource.getRootResourceDir())
            .andReturn(getRootResourceDir()).atLeastOnce();
        EasyMock.expect(mockResource.getNewPath()).andReturn(fileClasspath).atLeastOnce();

        EasyMock.expect(mockResource.getOriginalPath()).andReturn("original path").atLeastOnce();

        recordExpectationsForResource(mockResource, file, group);
    }

    private void recordExpectationsForResource(final Resource mockResource,
            final File file, final Group group) throws URISyntaxException {

        EasyMock.expect(mockResource.getNewFile()).andReturn(file).atLeastOnce();
        EasyMock.expect(mockResource.getGroup()).andReturn(group).atLeastOnce();
    }

    private void recordExpectationsForBundleResource(final Resource mockResource,
            final File newFile) throws URISyntaxException {

        EasyMock.expect(mockResource.getNewFile()).andReturn(newFile).atLeastOnce();
    }

    private File getRootResourceDir() throws URISyntaxException {

        return new File(this.getClass().getResource("/").toURI());

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

    private static String getAnExistingTestCssFile1Classpath() {
        return anExistingTestCssFile1Classpath;
    }

    private static String getAnExistingTestCssFile2Classpath() {
        return anExistingTestCssFile2Classpath;
    }

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

    /**
     * @return the anExistingTestCssFile2
     */
    private static File getAnExistingTestCssFile2() {
        return anExistingTestCssFile2;
    }

    /**
     * @param anExistingTestCssFile2 the anExistingTestCssFile2 to set
     */
    private static void setAnExistingTestCssFile2(final File anExistingTestCssFile2) {
        BundleFactoryTestCase.anExistingTestCssFile2 = anExistingTestCssFile2;
    }

    /**
     * @return the mockMinifier
     */
    public Minifier getMockMinifier() {
        return mockMinifier;
    }

    /**
     * @param mockMinifier the mockMinifier to set
     */
    public void setMockMinifier(final Minifier mockMinifier) {
        this.mockMinifier = mockMinifier;
    }

    /**
     * @return the anExistingTestJavaScriptFile2
     */
    public static File getAnExistingTestJavaScriptFile2() {
        return anExistingTestJavaScriptFile2;
    }

    /**
     * @param anExistingTestJavaScriptFile2 the anExistingTestJavaScriptFile2 to set
     */
    public static void setAnExistingTestJavaScriptFile2(final File anExistingTestJavaScriptFile2) {
        BundleFactoryTestCase.anExistingTestJavaScriptFile2 = anExistingTestJavaScriptFile2;
    }

    /**
     * @return the anExistingTestJavaScriptFile1Classpath
     */
    public static String getAnExistingTestJavaScriptFile1Classpath() {
        return anExistingTestJavaScriptFile1Classpath;
    }

    /**
     * @return the anExistingTestJavaScriptFile2Classpath
     */
    public static String getAnExistingTestJavaScriptFile2Classpath() {
        return anExistingTestJavaScriptFile2Classpath;
    }

    public Resource getMockResource3() {
        return mockResource3;
    }

    public void setMockResource3(final Resource mockResource3) {
        this.mockResource3 = mockResource3;
    }

    private static File getAnExistingCssBundleFile() {
        return anExistingCssBundleFile;
    }

    private static void setAnExistingCssBundleFile(final File anExistingCssBundleFile) {
        BundleFactoryTestCase.anExistingCssBundleFile = anExistingCssBundleFile;
    }

    private static File getAnExistingJavaScriptBundleFile() {
        return anExistingJavaScriptBundleFile;
    }

    private static void setAnExistingJavaScriptBundleFile(final File anExistingJavaScriptBundleFile) {
        BundleFactoryTestCase.anExistingJavaScriptBundleFile = anExistingJavaScriptBundleFile;
    }

    private static String getAnExistingCssBundleFileClasspath() {
        return anExistingCssBundleFileClasspath;
    }

    private static String getAnExistingJavaScriptBundleFileClasspath() {
        return anExistingJavaScriptBundleFileClasspath;
    }

    private static File getAnExistingTestCssFile1() {
        return anExistingTestCssFile1;
    }

    private static void setAnExistingTestCssFile1(final File anExistingTestCssFile1) {
        BundleFactoryTestCase.anExistingTestCssFile1 = anExistingTestCssFile1;
    }

    private static File getAnExistingTestJavaScriptFile1() {
        return anExistingTestJavaScriptFile1;
    }

    private static void setAnExistingTestJavaScriptFile1(final File anExistingTestJavaScriptFile1) {
        BundleFactoryTestCase.anExistingTestJavaScriptFile1 = anExistingTestJavaScriptFile1;
    }
}
