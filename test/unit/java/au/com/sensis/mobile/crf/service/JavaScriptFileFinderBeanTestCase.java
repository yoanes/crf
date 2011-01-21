package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.exception.ConfigurationRuntimeException;
import au.com.sensis.mobile.crf.util.FileIoFacade;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.mobile.crf.util.PropertiesLoader;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link JavaScriptFileFinderBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptFileFinderBeanTestCase extends
        AbstractJUnit4TestCase {

    private static final String ORDER_PROPERTY_NAME = "order";

    private static final String BUNDLES_PROPERTIES_FILE_NAME = "bundles.properties";

    private JavaScriptFileFinderBean objectUnderTest;

    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();
    private FileIoFacade mockFileIoFacade;
    private File mockFile1;
    private File mockFile2;
    private PropertiesLoader mockPropertiesLoader;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        FileIoFacadeFactory
                .changeDefaultFileIoFacadeSingleton(getMockFileIoFacade());

        setObjectUnderTest(new JavaScriptFileFinderBean(
                getMockPropertiesLoader(), BUNDLES_PROPERTIES_FILE_NAME,
                ORDER_PROPERTY_NAME));
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
    public void testFindJavaScriptFiles() throws Throwable {

        final TestData[] testData = getTestDataWhenResourceIsForABundle();
        for (int i = 0; i < testData.length; i++) {

            final Resource resource =
                    getResourcePathTestData()
                            .getMappedDefaultGroupPackagedScriptBundleResourcePath();

            EasyMock.expect(
                    getMockPropertiesLoader().loadPropertiesNotNull(
                            new File(resource.getNewFile(),
                                    BUNDLES_PROPERTIES_FILE_NAME))).andReturn(
                    testData[i].getBundleProperties());

            EasyMock.expect(
                    getMockFileIoFacade()
                            .list(
                                    EasyMock.eq(resource.getNewFile()),
                                    EasyMock.aryEq(testData[i]
                                            .getWildcardOneAsArray())))
                    .andReturn(testData[i].getWildcardOnefoundFiles());

            if (testData[i].getWildcardTwo() != null) {
                EasyMock.expect(
                        getMockFileIoFacade().list(
                                EasyMock.eq(resource.getNewFile()),
                                EasyMock.aryEq(testData[i]
                                        .getWildcardTwoAsArray()))).andReturn(
                        testData[i].getWildcardTwofoundFiles());
            }
            replay();

            final List<File> actualFiles =
                    getObjectUnderTest().findFiles(
                            resource.getNewFile());
            assertComplexObjectsEqual(
                    "Incorrect files returned for testData at index " + i
                            + ": " + testData[i], testData[i]
                            .getExpectedResult(), actualFiles);

            // Explicit verification since we are in a loop.
            verify();

            // Reset mocks prior to next iteration.
            reset();
        }
    }

    @Test
    public void testFindJavaScriptFilesWhenOrderPropertyMalformed()
            throws Throwable {

        final String[] testOrderPropertyValues =
                { " ", "  ", " , ", ",", "grid2.js, ", " , grid2.js" };

        for (final String testOrderPropertyValue : testOrderPropertyValues) {
            final Properties properties = new Properties();
            properties.setProperty(ORDER_PROPERTY_NAME, testOrderPropertyValue);

            final Resource resource =
                    getResourcePathTestData()
                            .getMappedDefaultGroupPackagedScriptBundleResourcePath();

            final File bundlePropertiesFile =
                    new File(resource.getNewFile(),
                            BUNDLES_PROPERTIES_FILE_NAME);
            EasyMock.expect(
                    getMockPropertiesLoader().loadPropertiesNotNull(
                            bundlePropertiesFile)).andReturn(properties);

            replay();

            try {
                getObjectUnderTest().findFiles(resource.getNewFile());

                Assert
                        .fail("ConfigurationRuntimeException expected for testValue: '"
                                + testOrderPropertyValue + "'");
            } catch (final ConfigurationRuntimeException e) {
                final String expectedMessage =
                        "Configuration file "
                                + bundlePropertiesFile
                                + " contains an error. order property "
                                + "must be a comma separated list "
                                + "of file patterns (with optional * and ? wildcards). Was: '"
                                + testOrderPropertyValue + "'";
                Assert.assertEquals(
                        "ConfigurationRuntimeException has wrong message",
                        expectedMessage, e.getMessage());
            }

            // Explicit verification since we are in a loop.
            verify();

            // Reset mocks prior to next iteration.
            reset();

        }
    }

    @Test
    public void testFindJavaScriptFilesWhenPropertiesLoaderThrowsException()
            throws Throwable {

        final Resource resource =
                getResourcePathTestData()
                        .getMappedDefaultGroupPackagedScriptBundleResourcePath();

        final IOException expectedException = new IOException("test");
        EasyMock.expect(
                getMockPropertiesLoader().loadPropertiesNotNull(
                        new File(resource.getNewFile(),
                                BUNDLES_PROPERTIES_FILE_NAME))).andThrow(
                expectedException);

        replay();

        try {
            getObjectUnderTest().findFiles(resource.getNewFile());

            Assert.fail("IOException expected");
        } catch (final IOException e) {
            Assert.assertEquals("exception is wrong", expectedException, e);
        }
    }

//    @Test
//    public void testExpandPathWhenResourceIsNotForABundle()
//            throws Throwable {
//        final Resource resource =
//                getResourcePathTestData()
//                        .getMappedDefaultGroupNamedScriptResourcePath();
//
//        final List<File> expectedFiles =
//                Arrays.asList(new File(resource.getRootResourceDir(),
//                        resource.getNewPath()));
//        final List<File> actualFiles =
//                getObjectUnderTest().findJavaScriptFiles(resource);
//        assertComplexObjectsEqual(
//                "Incorrect files returned ", expectedFiles, actualFiles);
//
//    }

    private JavaScriptFileFinderBean getObjectUnderTest() {
        return objectUnderTest;
    }

    private void setObjectUnderTest(final JavaScriptFileFinderBean objectUnderTest) {
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

    public File getMockFile1() {
        return mockFile1;
    }

    public void setMockFile1(final File mockFile1) {
        this.mockFile1 = mockFile1;
    }

    public File getMockFile2() {
        return mockFile2;
    }

    public void setMockFile2(final File mockFile2) {
        this.mockFile2 = mockFile2;
    }

    public PropertiesLoader getMockPropertiesLoader() {
        return mockPropertiesLoader;
    }

    public void setMockPropertiesLoader(final PropertiesLoader mockPropertiesLoader) {
        this.mockPropertiesLoader = mockPropertiesLoader;
    }

    private Properties createBundleProperties(final String orderProperty) {
        final Properties properties = new Properties();
        properties.setProperty(ORDER_PROPERTY_NAME, orderProperty);
        return properties;
    }

    public TestData[] getTestDataWhenResourceIsForABundle() {
        return new TestData[] {
                new TestData(createBundleProperties("grid2.js, *.js"),
                        "grid2.js",
                        "*.js",
                        new File[] { getMockFile1() },
                        new File[] { getMockFile2(), getMockFile1() },
                        Arrays.asList(getMockFile1(), getMockFile2())),
                new TestData(createBundleProperties(" grid2.js  , *.js "),
                        "grid2.js",
                        "*.js",
                        new File[] { getMockFile1() },
                        new File[] { getMockFile2(), getMockFile1() },
                        Arrays.asList(getMockFile1(), getMockFile2())),
                new TestData(createBundleProperties("grid2.js, *.js"),
                        "grid2.js",
                        "*.js",
                        new File[] { },
                        new File[] { },
                        new ArrayList<File>()),
                new TestData(createBundleProperties("animation2.js"),
                        "animation2.js",
                        "*.js",
                        new File[] { getMockFile1() },
                        new File[] { getMockFile2(), getMockFile1() },
                        Arrays.asList(getMockFile1(), getMockFile2())),
                new TestData(new Properties(),
                        "*.js",
                        null,
                        new File[] { getMockFile1() },
                        null,
                        Arrays.asList(getMockFile1())),
                new TestData(new Properties(),
                        "*.js",
                        null,
                        new File[] { },
                        null,
                        new ArrayList<File>()),
        };
    }

    private static final class TestData {
        private final Properties bundleProperties;
        private final String wildcardOne;
        private final String wildcardTwo;
        private final File [] wildcardOnefoundFiles;
        private final File [] wildcardTwofoundFiles;
        private final List<File> expectedResult;

        private TestData(final Properties bundleProperties,
                final String wildcardOne, final String wildcardTwo,
                final File[] wildcardOnefoundFiles,
                final File[] wildcardTwofoundFiles,
                final List<File> expectedResult) {
            this.bundleProperties = bundleProperties;
            this.wildcardOne = wildcardOne;
            this.wildcardTwo = wildcardTwo;
            this.wildcardOnefoundFiles = wildcardOnefoundFiles;
            this.wildcardTwofoundFiles = wildcardTwofoundFiles;
            this.expectedResult = expectedResult;
        }

        private Properties getBundleProperties() {
            return bundleProperties;
        }

        public String getWildcardOne() {
            return wildcardOne;
        }

        public String[] getWildcardOneAsArray() {
            return new String[] { getWildcardOne() };
        }

        public String getWildcardTwo() {
            return wildcardTwo;
        }

        public String[] getWildcardTwoAsArray() {
            return new String[] { getWildcardTwo() };
        }

        private File[] getWildcardOnefoundFiles() {
            return wildcardOnefoundFiles;
        }

        public File[] getWildcardTwofoundFiles() {
            return wildcardTwofoundFiles;
        }

        public List<File> getExpectedResult() {
            return expectedResult;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                .append("bundleProperties", getBundleProperties())
                .append("wildcardOne", getWildcardOne())
                .append("wildcardTwo", getWildcardTwo())
                .append("wildcardOnefoundFiles", ArrayUtils.toString(getWildcardOnefoundFiles()))
                .append("wildcardTwofoundFiles", ArrayUtils.toString(getWildcardTwofoundFiles()))
                .append("expectedResult", expectedResult)
                .toString();
        }


    }
}
