package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import com.thoughtworks.selenium.Selenium;

/**
 * Like the HD800 - but this one is when we switch on bundling.
 *
 * ie. env.bundle.resources=true
 *
 * @author Boyd Sharrock
 *
 */
public class HD800BundledBdpPage extends HD800BdpPage {

    /**
     * @param selenium Selenium instance to use.
     */
    public HD800BundledBdpPage(final Selenium selenium) {
        super(selenium);
    }

    @Override
    protected void assertBundleLinksTagOutputPresent() {

        assertBundleLinksTagOutputPresentWhenBundlingEnabled();
    }

    @Override
    protected void assertBundleScriptsTagOutputPresent() {
        assertBundleScriptsTagOutputPresentWhenBundlingEnabled();
    }

    @Override
    protected void assertBundleLinksWithinBundleScriptsWithDeferredRenderingTagOutputPresent() {

        // Default is bundling disabled...
        assertBundleLinksWithinBundleScriptsWithDeferredRenderingTagOutputPresentBundlingEnabled();
    }

    @Override
    protected void assertHD800Css() {
        assertNumCssLinks(super.getNumExpectedLinks() + 2);
    }

    @Override
    protected void assertHD800Scripts() {

        // Because of bundling being enabled (and some deferred rendering)
        // there are much fewer scripts in the head...
        assertNumHeadScripts(11);

        // Because of the deferred rendering - there will now be 2 in the body...
        final int expectedNumHD800BodyScripts = 2;
        assertNumBodyScripts(expectedNumHD800BodyScripts + super.getNumExpectedBodyScripts());
    }

}
