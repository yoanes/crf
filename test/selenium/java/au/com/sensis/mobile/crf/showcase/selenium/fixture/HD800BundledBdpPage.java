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

    /**
     * {@inheritDoc}
     */
    @Override
    public void assertPageStructure() {

        assertBundleLinksTagOutputPresentWhenBundlingEnabled();
        assertBundleScriptsTagOutputPresentWhenBundlingEnabled();
    }

}
