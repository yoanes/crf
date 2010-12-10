package au.com.sensis.mobile.crf.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.crf.exception.MinificationException;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link YUIMinifier}.
 *
 * @author Tony Filipe
 */
public class YUIMinifierTestCase extends AbstractJUnit4TestCase {

    private YUIMinifier objectUnderTest;

    private static final String CSS_FILENAME =
        "/au/com/sensis/mobile/crf/util/YUIMinifierTestData/cssFile.css";

    private static final String JS_FILENAME =
        "/au/com/sensis/mobile/crf/util/YUIMinifierTestData/javascriptFile.js";

    private static final String OTHER_FILENAME = "testTextFile.txt";

    private String preMinifiedCSSContent;
    private String preMinifiedJSContent;

    private File cssFile;
    private File javascriptFile;


    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new YUIMinifier());

        cssFile = new File(this.getClass().getResource(CSS_FILENAME).toURI());
        preMinifiedCSSContent = readFileContents(cssFile);

        javascriptFile = new File(this.getClass().getResource(JS_FILENAME).toURI());
        preMinifiedJSContent = readFileContents(javascriptFile);
    }

    /**
     * Restore the test data with its original content.
     */
    @After
    public void tearDown() throws IOException {

        restoreTestFile(cssFile, preMinifiedCSSContent);
        restoreTestFile(javascriptFile, preMinifiedJSContent);
    }

    @Test
    public void testMinifyWithUnsupportedFiletype() {

        try {
            getObjectUnderTest().minify(OTHER_FILENAME);

            Assert.fail("MinificationException expected");
        } catch (final MinificationException e) {
            // Correct, we expect a MinificationException to be thrown
        }
    }

    @Test
    public void testMinifyWithNoFiletype() {

        try {
            getObjectUnderTest().minify("/au/com/sensis/extensionlessFile");

            Assert.fail("MinificationException expected");
        } catch (final MinificationException e) {
            // Correct, we expect a MinificationException to be thrown
        }
    }

    @Test
    public void testMinifyWithNotFoundFile() {

        try {
            getObjectUnderTest().minify("/another/path" + JS_FILENAME);

            Assert.fail("MinificationException expected");
        } catch (final MinificationException e) {
            // Correct, we expect a MinificationException to be thrown
        }
    }

    @Test
    public void testMinifyCSSFile() throws MinificationException, IOException {

        getObjectUnderTest().minify(cssFile.getPath());

        final String minifiedContent = readFileContents(cssFile);

        // Verify that minifaction removes whitespace and comments
        Assert.assertFalse(minifiedContent.contains(" "));
        Assert.assertFalse(minifiedContent.contains("\t"));
        Assert.assertFalse(minifiedContent.contains("/*"));
    }

    @Test
    public void testMinifyJavascriptFile() throws MinificationException, IOException {

        getObjectUnderTest().minify(javascriptFile.getPath());

        final String minifiedContent = readFileContents(javascriptFile);

        // Verify that minifaction removes whitespace and comments
        Assert.assertFalse(minifiedContent.contains("\t"));
        Assert.assertFalse(minifiedContent.contains("<!--"));
    }


    // HELPER METHODS

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

    private void restoreTestFile(final File filePath, final String fileContents)
    throws IOException {

        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
        try {
            bufferedWriter.write(fileContents);
        } finally {
            bufferedWriter.close();
        }
    }



    /**
     * @return the objectUnderTest
     */
    private YUIMinifier getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final YUIMinifier objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }
}
