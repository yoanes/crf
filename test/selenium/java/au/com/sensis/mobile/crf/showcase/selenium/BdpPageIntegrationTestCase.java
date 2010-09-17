package au.com.sensis.mobile.crf.showcase.selenium;

import au.com.sensis.mobile.crf.showcase.selenium.fixture.HD800BdpPage;
import au.com.sensis.mobile.crf.showcase.selenium.fixture.IphoneOS2xBdpPage;
import au.com.sensis.mobile.crf.showcase.selenium.fixture.IphoneOS3xBdpPage;
import au.com.sensis.mobile.crf.showcase.selenium.fixture.Nokia7600BdpPage;

/**
 * Tests the home page.
 *
 * TODO
 *
 * In order to run this test start tomcat, then start the selenium server, run this as a JUnit test.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class BdpPageIntegrationTestCase extends AbstractSeleniumIntegrationTestCase {

    protected final void openBdp() {
        openUrl("http://localhost:8080/bdp.action");
    }

    /**
     * Test the page for a specific device.
     */
    public void testHD800Device() throws Exception {
        // TODO: setup user agent for HD800 device.

        openBdp();

        getPageFixtureFactory().createPageFixture(HD800BdpPage.class);
    }

    /**
     * Test the page for a specific device.
     */
    public void testNokia7600Device() throws Exception {
        // TODO: setup user agent for Nokia 7600 device.

        openBdp();

        getPageFixtureFactory().createPageFixture(Nokia7600BdpPage.class);
    }

    /**
     * Test the page for a specific device.
     */
    public void testIphoneOS3xDevice() throws Exception {
        // TODO: setup user agent for iphone OS 3.x device.

        openBdp();

        getPageFixtureFactory().createPageFixture(IphoneOS3xBdpPage.class);
    }

    /**
     * Test the page for a specific device.
     */
    public void testIphoneOS2xDevice() throws Exception {
        // TODO: setup user agent for iphone OS 2.x device.

        openBdp();

        getPageFixtureFactory().createPageFixture(IphoneOS2xBdpPage.class);
    }
}
