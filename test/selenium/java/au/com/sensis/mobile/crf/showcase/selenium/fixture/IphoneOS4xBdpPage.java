package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import static junit.framework.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;

/**
 * Assertions and actions for the BDP Page.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class IphoneOS4xBdpPage extends IphoneOS3xBdpPage {

    /**
     * Default constructor.
     *
     * @param selenium Selenium instance to use.
     */
    public IphoneOS4xBdpPage(final Selenium selenium) {
        super(selenium);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAssertPageStructure() {
        super.doAssertPageStructure();
        assertInAllGroupsJspSelection();
    }

    private void assertInAllGroupsJspSelection() {
        assertTrue(getBrowser().isTextPresent("[clickToCallSupported-hd640] clickToCall.jsp"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getImageDimensionsDivisor() {
        // Image dimensions should be divided by 2 for iPhone 4.
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assertImageCategoryText() {
        assertTrue("imageCategory is wrong",
                getBrowser().isTextPresent("'custom.imageCategory': HD640"));
    }
}
