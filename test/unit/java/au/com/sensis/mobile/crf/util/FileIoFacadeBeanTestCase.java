package au.com.sensis.mobile.crf.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link FileIoFacadeBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class FileIoFacadeBeanTestCase extends AbstractJUnit4TestCase {

    private FileIoFacadeBean objectUnderTest;

    private static final String EXPECTED_FIND_ME_FILE_ON_CLASSPATH =
        "/au/com/sensis/mobile/crf/util/fileIoFacadeBeanTestData/level1/level2/findMe.png";

    private static final String EXPECTED_ANOTHER_FIND_ME_PNG_FILE_ON_CLASSPATH =
        "/au/com/sensis/mobile/crf/util/fileIoFacadeBeanTestData/level1/"
        + "level2/anotherFindMe.png";

    private static final String EXPECTED_ANOTHER_FIND_ME_GIF_FILE_ON_CLASSPATH =
        "/au/com/sensis/mobile/crf/util/fileIoFacadeBeanTestData/level1/"
        + "level2/anotherFindMe.gif";

    private File expectedFindMeFile;
    private File expectedAnotherFindMePngFile;
    private File expectedAnotherFindMeGifFile;

    private File withExtensionsParentDirectory;
    private File listByWildcardsTestDataDirectory;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new FileIoFacadeBean());

        setExpectedFindMeFile(new File(this.getClass().getResource(
                EXPECTED_FIND_ME_FILE_ON_CLASSPATH).toURI()));
        setExpectedAnotherFindMePngFile(new File(this.getClass().getResource(
                EXPECTED_ANOTHER_FIND_ME_PNG_FILE_ON_CLASSPATH).toURI()));
        setExpectedAnotherFindMeGifFile(new File(this.getClass().getResource(
                EXPECTED_ANOTHER_FIND_ME_GIF_FILE_ON_CLASSPATH).toURI()));

        setWithExtensionsParentDirectory(
                new File(this.getClass().getResource(
                    "/au/com/sensis/mobile/crf/util/fileIoFacadeBeanTestData").toURI()));
        setListByWildcardsTestDataDirectory(
                new File(this.getClass().getResource(
                    "/au/com/sensis/mobile/crf/util/fileIoFacadeBeanTestData/level1/level2")
                        .toURI()));
    }

    @Test
    public void testFileExistsWhenTrue() throws Throwable {
        final URL url = this.getClass().getResource(
                "/au/com/sensis/mobile/crf/util/FileIoFacadeBeanTestCase.class");

        Assert.assertTrue("fileExists should be true",
                getObjectUnderTest().fileExists(url.getPath()));

    }

    @Test
    public void testFileExistsWhenFalse() throws Throwable {
        Assert.assertFalse("fileExists should be false",
                getObjectUnderTest().fileExists("i don't exist"));

    }

    @Test
    public void testParentDirAndFileExistsWhenTrue() throws Throwable {
        final URL url =
                this.getClass().getResource(
                        "/au/com/sensis/mobile/crf/util/");

        Assert.assertTrue("fileExists should be true", getObjectUnderTest()
                .fileExists(new File(url.toURI()),
                        "FileIoFacadeBeanTestCase.class"));

    }

    @Test
    public void testParentDirAndFileExistsWhenFalse() throws Throwable {
        final URL url =
            this.getClass().getResource(
                    "/au/com/sensis/mobile/crf/util/");

        Assert.assertFalse("fileExists should be false", getObjectUnderTest()
                .fileExists(new File(url.toURI()),
                "I-do-not-exist"));

    }

    @Test
    public void testListByExtensionsWithSpecificExtension() throws Throwable {
        final File[] listings =
                getObjectUnderTest().list(getWithExtensionsParentDirectory(),
                        "level1/level2/findMe",
                        new String[] { "png" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 1, listings.length);
        Assert.assertEquals("listings[0] is wrong", getExpectedFindMeFile(), listings[0]);
    }

    @Test
    public void testListByExtensionsWithSpecificExtensionsMultipleMatches() throws Throwable {
        final File[] listings =
            getObjectUnderTest().list(getWithExtensionsParentDirectory(),
                    "level1/level2/anotherFindMe",
                    new String[] { "png", "gif" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 2, listings.length);
        Assert.assertTrue("listings does not contain: " + getExpectedAnotherFindMeGifFile(),
                ArrayUtils.contains(listings, getExpectedAnotherFindMeGifFile()));
        Assert.assertTrue("listings does not contain: " + getExpectedAnotherFindMePngFile(),
                ArrayUtils.contains(listings, getExpectedAnotherFindMePngFile()));
    }

    @Test
    public void testListByExtensionsWithWildcardExtension() throws Throwable {
        final File [] listings =
            getObjectUnderTest().list(getWithExtensionsParentDirectory(),
                    "level1/level2/findMe", new String[] { "*" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 1, listings.length);
        Assert.assertEquals("listings[0] is wrong", expectedFindMeFile, listings[0]);
    }

    @Test
    public void testListByExtensionsWithWildcardExtensionMultipleMatches() throws Throwable {
        final File[] listings =
            getObjectUnderTest().list(getWithExtensionsParentDirectory(),
                    "level1/level2/anotherFindMe",
                    new String[] { "*" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 2, listings.length);
        Assert.assertTrue("listings does not contain: " + getExpectedAnotherFindMeGifFile(),
                ArrayUtils.contains(listings, getExpectedAnotherFindMeGifFile()));
        Assert.assertTrue("listings does not contain: " + getExpectedAnotherFindMePngFile(),
                ArrayUtils.contains(listings, getExpectedAnotherFindMePngFile()));
    }

    @Test
    public void testListByExtensionsWithSpecificExtensionAndWildcardExtension() throws Throwable {
        final File [] listings =
                getObjectUnderTest().list(getWithExtensionsParentDirectory(),
                        "level1/level2/findMe",
                        new String[] { "blah", "*" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 1, listings.length);
        Assert.assertEquals("listings[0] is wrong", expectedFindMeFile, listings[0]);
    }

    @Test
    public void testListByExtensionsWithPartialWildcardExtension() throws Throwable {
        final File [] listings =
            getObjectUnderTest().list(getWithExtensionsParentDirectory(),
                    "level1/level2/findMe",
                    new String[] { "p*" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 1, listings.length);
        Assert.assertEquals("listings[0] is wrong", expectedFindMeFile, listings[0]);
    }

    @Test
    public void testListByExtensionsWhenNoFilesFound() throws Throwable {
        final File [] listings =
            getObjectUnderTest().list(getWithExtensionsParentDirectory(),
                    "level1/level2/findMe",
                    new String[] { "blah*" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 0, listings.length);
    }

    @Test
    public void testListByExtensionsWhenParentDirectoryNotFound() throws Throwable {
        final File [] listings =
            getObjectUnderTest().list(new File("i don't exist"),
                    "level1/level2/findMe",
                    new String[] { "blah*" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 0, listings.length);
    }

    @Test
    public void testListByMatchedAndExcludedExtensions() throws Throwable {
        final File[] listings =
                getObjectUnderTest().list(getWithExtensionsParentDirectory(),
                        "level1/level2/anotherFindMe", new String[] { "*" },
                        new String[] { "gif" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 1, listings.length);
        Assert.assertEquals("listings[0] is wrong", getExpectedAnotherFindMePngFile(), listings[0]);
    }

    @Test
    public void testListByWildcardsWhenFilesFoundByMultipleMatchWildcard() throws Throwable {
        final File[] listings =
            getObjectUnderTest().list(getListByWildcardsTestDataDirectory(),
                    new String[] { "another*" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 2, listings.length);

        final List<File> foundListings = Arrays.asList(listings);
        Assert.assertTrue("listings does not contain anotherFindMeGifFile",
                foundListings.contains(getExpectedAnotherFindMeGifFile()));
        Assert.assertTrue("listings does not contain anotherFindMePngFile",
                foundListings.contains(getExpectedAnotherFindMePngFile()));
    }

    @Test
    public void testListByWildcardsWhenFilesFoundBySingleMatchWildcard() throws Throwable {
        final File[] listings =
            getObjectUnderTest().list(getListByWildcardsTestDataDirectory(),
                    new String[] { "findMe.???" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 1, listings.length);
        Assert.assertEquals("listings[0] is wrong", getExpectedFindMeFile(), listings[0]);
    }

    @Test
    public void testListByWildcardsWhenFilesFoundByMultipleWildcardPatterns() throws Throwable {
        final File[] listings =
            getObjectUnderTest().list(getListByWildcardsTestDataDirectory(),
                    new String[] { "another*", "findMe.???" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 3, listings.length);

        final List<File> foundListings = Arrays.asList(listings);
        Assert.assertTrue("listings does not contain anotherFindMeGifFile",
                foundListings.contains(getExpectedAnotherFindMeGifFile()));
        Assert.assertTrue("listings does not contain anotherFindMePngFile",
                foundListings.contains(getExpectedAnotherFindMePngFile()));
        Assert.assertTrue("listings does not contain expectedFindMeFile",
                foundListings.contains(getExpectedFindMeFile()));
    }

    @Test
    public void testListByWildcardsWhenNoFilesFound() throws Throwable {
        final File[] listings =
            getObjectUnderTest().list(getListByWildcardsTestDataDirectory(),
                    new String[] { "you can't find me*" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 0, listings.length);
    }

    @Test
    public void testListByWildcardsWhenDirectoryNotFound() throws Throwable {
        final File[] listings =
            getObjectUnderTest().list(new File("i don't exist"),
                    new String[] { "you can't find me*" });

        Assert.assertNotNull("listings should not be null", listings);
        Assert.assertEquals("incorrect number of listings", 0, listings.length);
    }

    @Test
    public void testWriteFileAndCloseStream() throws Throwable {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        getObjectUnderTest().writeFileAndCloseStream(getExpectedFindMeFile(),
                outputStream);

        Assert.assertEquals("Wrong data written to outputstream",
                "findMe.png contents", outputStream.toString());

    }
    /**
     * @return the objectUnderTest
     */
    private FileIoFacadeBean getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final FileIoFacadeBean objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    private void setExpectedFindMeFile(final File expectedFindMeFile) {
        this.expectedFindMeFile = expectedFindMeFile;
    }

    private File getExpectedFindMeFile() {
        return expectedFindMeFile;
    }

    private void setWithExtensionsParentDirectory(
            final File withExtensionsParentDirectory) {
        this.withExtensionsParentDirectory = withExtensionsParentDirectory;
    }

    private File getWithExtensionsParentDirectory() {
        return withExtensionsParentDirectory;
    }

    private File getExpectedAnotherFindMePngFile() {
        return expectedAnotherFindMePngFile;
    }

    private void setExpectedAnotherFindMePngFile(
            final File expectedAnotherFindMePngFile) {
        this.expectedAnotherFindMePngFile = expectedAnotherFindMePngFile;
    }

    private File getExpectedAnotherFindMeGifFile() {
        return expectedAnotherFindMeGifFile;
    }

    private void setExpectedAnotherFindMeGifFile(final File expectedAnotherFindMeGifFile) {
        this.expectedAnotherFindMeGifFile = expectedAnotherFindMeGifFile;
    }

    private File getListByWildcardsTestDataDirectory() {
        return listByWildcardsTestDataDirectory;
    }

    private void setListByWildcardsTestDataDirectory(
            final File listByWildcardsTestDataDirectory) {
        this.listByWildcardsTestDataDirectory = listByWildcardsTestDataDirectory;
    }



}
