package au.com.sensis.mobile.crf.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

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

    private File cssSourceFile;
    private File javascriptSourceFile;


    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {

        setObjectUnderTest(new YUIMinifier());

        setCssSourceFile(new File(this.getClass().getResource(CSS_FILENAME).toURI()));
        setJavascriptSourceFile(new File(this.getClass().getResource(JS_FILENAME).toURI()));
    }

    @Test
    public void testMinifyCSS() throws MinificationException, IOException {

        final Writer minifiedCssWriter = new StringWriter();
        final Reader sourceCssReader = new FileReader(getCssSourceFile());
        try {
            getObjectUnderTest().minifyCss(sourceCssReader, minifiedCssWriter);

            final String minifiedContent = minifiedCssWriter.toString();

            // Verify that minifaction removes whitespace and comments
            Assert.assertFalse(minifiedContent.contains(" "));
            Assert.assertFalse(minifiedContent.contains("\t"));
            Assert.assertFalse(minifiedContent.contains("/*"));

            // Basic test that the minifiedContent still contains the original data.
            Assert.assertTrue("minifiedContent contains id selector",
                    minifiedContent.contains("#aStyle"));
        } finally {
            if (sourceCssReader != null) {
                sourceCssReader.close();
            }
        }

    }

    @Test
    public void testMinifyJavaScript() throws MinificationException, IOException {

        final Writer minifiedCssWriter = new StringWriter();
        final Reader sourceCssReader = new FileReader(getJavascriptSourceFile());
        try {
            getObjectUnderTest().minifyJavaScript(sourceCssReader, minifiedCssWriter);

            final String minifiedContent = minifiedCssWriter.toString();

            // Verify that minifaction removes whitespace and comments
            Assert.assertFalse(minifiedContent.contains("\t"));
            Assert.assertFalse(minifiedContent.contains("<!--"));

            // Basic test that the minifiedContent still contains the original data.
            Assert.assertTrue("minifiedContent contains id selector",
                    minifiedContent.contains("function doSomething()"));
        } finally {
            if (sourceCssReader != null) {
                sourceCssReader.close();
            }
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

    private void setJavascriptSourceFile(final File javascriptSourceFile) {
        this.javascriptSourceFile = javascriptSourceFile;
    }

    private File getJavascriptSourceFile() {
        return javascriptSourceFile;
    }

    private void setCssSourceFile(final File cssSourceFile) {
        this.cssSourceFile = cssSourceFile;
    }

    private File getCssSourceFile() {
        return cssSourceFile;
    }
}
