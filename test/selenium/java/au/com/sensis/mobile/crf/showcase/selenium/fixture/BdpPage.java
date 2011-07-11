package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import junit.framework.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * Assertions and actions for the BDP Page.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public abstract class BdpPage extends AbstractPageFixture {

    /**
     * Default constructor.
     *
     * @param selenium Selenium instance to use.
     */
    public BdpPage(final Selenium selenium) {
        super(selenium);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assertPageStructure() {
        assertTrue("logo.jsp not found", getBrowser().isTextPresent("[default] logo.jsp"));

        assertInlineScriptPresent();

        assertAbsolutelyReferencedScript("external, absolutely referenced script not found",
                "http://localhost:8080/something.js");

        assertAppProperty1LoadedFromMainPropertiesFile();

        assertResourcePrefixes();

        assertBundleScriptsTagOutputPresent();

        doAssertPageStructure();
    }

    private void assertResourcePrefixes() {
        final String expectedImgSrcPrefix =
                "'/uidev/crfshowcase/uiresources/" + getProjectVersion() + "/images/'";
        assertTrue("imgSrcPrefix via direct render not found", getBrowser().isTextPresent(
                "imgSrcPrefix (direct render): " + expectedImgSrcPrefix));
        assertTrue("imgSrcPrefix via var not found", getBrowser().isTextPresent(
                "imgSrcPrefix (via var): " + expectedImgSrcPrefix));

        final String expectedLinkHrefPrefix =
                "'/uidev/crfshowcase/uiresources/" + getProjectVersion() + "/css/'";
        assertTrue("linkHrefPrefix via direct render not found", getBrowser().isTextPresent(
                "linkHrefPrefix (direct render): " + expectedLinkHrefPrefix));
        assertTrue("linkHrefPrefix via var not found", getBrowser().isTextPresent(
                "linkHrefPrefix (via var): " + expectedLinkHrefPrefix));

        final String expectedScriptSrcPrefix =
                "'/uidev/crfshowcase/uiresources/" + getProjectVersion() + "/javascript/'";
        assertTrue("scriptSrcPrefix via direct render not found", getBrowser().isTextPresent(
                "scriptSrcPrefix (direct render): " + expectedScriptSrcPrefix));
        assertTrue("scriptSrcPrefix via var not found", getBrowser().isTextPresent(
                "scriptSrcPrefix (via var): " + expectedScriptSrcPrefix));

    }

    private void assertInlineScriptPresent() {
        assertBundleScriptsTagJavaScriptVariable("window.myScript",
                "I am here and you should see me only once");
    }

    /**
     * Assert expected out of the bundleScriptsTag.
     */
    protected void assertBundleScriptsTagOutputPresent() {
        assertBundleScriptsTagJavaScriptVariable("defaultShowcaseAppBundlePackage1File1", "true");
        assertBundleScriptsTagJavaScriptVariable("defaultShowcaseAppBundlePackage1File2", "true");
        assertBundleScriptsTagJavaScriptVariable("defaultShowcaseAppBundlePackage2File1", "true");
        assertBundleScriptsTagJavaScriptVariable("defaultShowcaseAppBundlePackage2File2", "true");

        assertBundleScriptsTagJavaScriptVariable("iphoneIpodShowcaseAppBundlePackage1File1",
                "null");
        assertBundleScriptsTagJavaScriptVariable("iphoneIpodShowcaseAppBundlePackage1File2",
                "null");
    }

    /**
     * Assert that the page contains a JavaScript variable set to the given value.
     *
     * @param scriptVariableName Name of the variable to assert.
     * @param expectedValue Expected value of the variable.
     */
    protected void assertBundleScriptsTagJavaScriptVariable(final String scriptVariableName,
            final String expectedValue) {
        final String scriptVariable = getBrowser().getEval("window." + scriptVariableName);
        assertEquals(scriptVariableName + " set by BundleScriptsTag has wrong value",
                expectedValue, scriptVariable);
    }

    /**
     * Assert that the action we hit has successfully retrieved a properties file via
     * the framework.
     */
    protected void assertAppProperty1LoadedFromMainPropertiesFile() {
        assertTrue("app.property1 not found on page",
                getBrowser().isTextPresent("'app.property1': appProperty1DefaultValue"));
    }

    /**
     * @return number of scripts expected by this abstract BdpPage.
     */
    protected final int getNumExpectedScripts() {
        return 3;
    }

    /**
     * Subclasses to provide further assertions.
     */
    protected abstract void doAssertPageStructure();

    /**
     * Helper method for asserting the number of CSS links present.
     *
     * @param expectedNumLinks Expected number of link elements present.
     */
    protected final void assertNumCssLinks(final int expectedNumLinks) {
        assertEquals("Incorrect number of CSS links", expectedNumLinks,
                getBrowser().getXpathCount("//head/link"));
    }

    /**
     * Helper method for asserting the number of CSS links present.
     *
     * @param expectedNumLinks Expected number of link elements present.
     */
    protected final void assertNumScripts(final int expectedNumLinks) {
        assertEquals("Incorrect number of script elements", expectedNumLinks,
                getBrowser().getXpathCount("//head/script"));
    }

    /**
     * Helper method for asserting the number of img elements present.
     *
     * @param expectedNumLinks Expected number of img elements present.
     */
    protected final void assertNumImgElements(final int expectedNumLinks) {
        assertEquals("Incorrect number of img elements", expectedNumLinks,
                getBrowser().getXpathCount("//body//img"));
    }

    /**
     * Helper method for asserting the presence of a CSS link element.
     *
     * @param message Message to use if the test fails.
     * @param expectedHref Expected href value of the link, relative to the root
     *      resources/css dir.
     */
    protected final void assertCssLink(final String message, final String expectedHref) {
        assertTrue(message, getBrowser().isElementPresent(
                "//head/link["
                + "@type=\"text/css\" "
                + "and @rel=\"stylesheet\" "
                + "and @href=\"/uidev/crfshowcase/uiresources/"
                + getProjectVersion() + "/css/"
                + expectedHref + "\""
                + "]"));

    }

    /**
     * Helper method for asserting that a CSS link element is not present with an href
     * matching a given regex.
     *
     * @param message Message to use if the test fails.
     * @param expectedHrefRegex Regex to test against the link's href.
     */
    // TODO: this just doesn't seem to work
    protected final void assertCssLinkNotPresent(final String message,
        final String expectedHrefRegex) {
        getBrowser().allowNativeXpath("false");
        Assert.assertFalse(message, getBrowser().isElementPresent(
                "//head/link["
                + "matches(@href, \"" + expectedHrefRegex + "\") "
                + "]"));

    }

    /**
     * Helper method for asserting the presence of a script element.
     *
     * @param message Message to use if the test fails.
     * @param expectedSrc Expected src value of the script, relative to the root
     *      resources/javascript dir.
     */
    protected final void assertScript(final String message, final String expectedSrc) {
        assertTrue(message, getBrowser().isElementPresent(
                "//head/script["
                + "@type=\"text/javascript\" "
                + "and @charset=\"utf-8\" "
                + "and @src=\"/uidev/crfshowcase/uiresources/"
                + getProjectVersion() + "/javascript/"
                + expectedSrc + "\""
                + "]"));

    }

    /**
     * Helper method for asserting the presence of a script element with a src that is
     * an absolute URL.
     *
     * @param message Message to use if the test fails.
     * @param expectedSrc Expected src value of the script.
     */
    protected final void assertAbsolutelyReferencedScript(final String message,
            final String expectedSrc) {
        assertTrue(message, getBrowser().isElementPresent(
                "//head/script["
                + "@type=\"text/javascript\" "
                + "and @src=\""
                + expectedSrc + "\""
                + "]"));

    }

    /**
     * Helper method for asserting the presence of an img element.
     *
     * @param message Message to use if the test fails.
     * @param expectedId Expected id attribute.
     * @param expectedTitle Expected title attribute.
     * @param expectedAlt Expected alt attribute.
     * @param expectedSrc Expected src value of the link, relative to the root
     *      resources/images dir.
     * @param expectedWidth Expected width attribute of the img.
     * @param expectedHeight Expected height attribute of the img.
     */
    protected final void assertImg(final String message, final String expectedId,
            final String expectedTitle, final String expectedAlt,
            final String expectedSrc, final int expectedWidth, final int expectedHeight) {
        assertTrue(message, getBrowser().isElementPresent(
                "//body//img["
                + "@id=\"" + expectedId + "\" "
                + "and @title=\"" + expectedTitle + "\" "
                + "and @alt=\"" + expectedAlt + "\" "
                + "and @src=\"/uidev/crfshowcase/uiresources/"
                + getProjectVersion() + "/images/"
                + expectedSrc + "\" "
                + "and @width=\"" + expectedWidth + "\" "
                + "and @height=\"" + expectedHeight + "\" "
                + "]"));

    }

    /**
     * Helper method for asserting the presence of a (broken) img. ie. an img which
     * could not be resolved to a real resource
     *
     * @param message Message to use if the test fails.
     * @param expectedId Expected id attribute.
     * @param expectedTitle Expected title attribute.
     * @param expectedAlt Expected alt attribute.
     * @param expectedSrc Expected src value of the link, relative to the root
     *      resources/images dir.
     */
    protected final void assertBrokenImg(final String message, final String expectedId,
            final String expectedTitle, final String expectedAlt,
            final String expectedSrc) {
        assertTrue(message, getBrowser().isElementPresent(
                "//body//img["
                + "@id=\"" + expectedId + "\" "
                + "and @title=\"" + expectedTitle + "\" "
                + "and @alt=\"" + expectedAlt + "\" "
                + "and @src=\"/uidev/crfshowcase/uiresources/"
                + getProjectVersion() + "/images/"
                + expectedSrc + "\""
                + "]"));

    }

    /**
     * Assert presence of scaled yellow pages image.
     *
     * @param width
     *            Width of image.
     * @param height
     *            Height of image.
     * @param extension Extension of the image.
     */
    protected void assertScaledYellowPagesImage(final int width, final int height,
            final String extension) {
        assertImg("Yellow Pages img not found", "yellowPagesLogoImg", "Yellow Pages",
                "Yellow Pages", "default/selenium/common/w" + width + "/h" + height
                        + "/yellow-pages." + extension, width, height);
    }

    /**
     * Assert presence of scaled search image.
     *
     * @param width
     *            Width of image.
     * @param height
     *            Height of image.
     * @param extension
     *            Extension that the scaled image should have.
     * @param group Group that the image was found in.
     */
    protected void assertScaledSearchImage(final int width, final int height,
            final String extension, final String group) {
        assertImg("Search img not found", "searchImg", "Search", "Search", group
                + "/selenium/common/w" + width + "/h" + height + "/search." + extension, width,
                height);
    }


    /**
     * Helper method for asserting the presence of an img element.
     *
     * @param message
     *            Message to use if the test fails.
     * @param expectedSrc
     *            Expected src value of the link, relative to the root
     *            resources/images dir.
     */
    protected final void assertImgPath(final String message, final String expectedSrc) {
        assertTrue(message, getBrowser().isTextPresent(
                        "/uidev/crfshowcase/uiresources/" + getProjectVersion()
                        + "/images/" + expectedSrc));

    }

    /**
     * Assert presence of scaled yellow pages image path.
     *
     * @param width
     *            Width of image.
     * @param height
     *            Height of image.
     * @param extension Extension of the image.
     */
    protected void assertScaledYellowPagesImagePath(final int width, final int height,
            final String extension) {
        assertTrue("Yellow Pages img not found", getBrowser().isTextPresent(
                "/uidev/crfshowcase/uiresources/" + getProjectVersion()
                        + "/images/default/selenium/common/w" + width + "/h" + height
                        + "/yellow-pages." + extension));
    }
}
