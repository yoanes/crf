package au.com.sensis.mobile.crf.showcase.selenium;

import au.com.sensis.mobile.crf.showcase.selenium.fixture.HD800BdpPage;
import au.com.sensis.mobile.crf.showcase.selenium.fixture.HD800BundledBdpPage;
import au.com.sensis.mobile.crf.showcase.selenium.fixture.IphoneOS2xBdpPage;
import au.com.sensis.mobile.crf.showcase.selenium.fixture.IphoneOS3xBdpPage;
import au.com.sensis.mobile.crf.showcase.selenium.fixture.IphoneOS3xClientAppBdpPage;
import au.com.sensis.mobile.crf.showcase.selenium.fixture.IphoneOS4xBdpPage;
import au.com.sensis.mobile.crf.showcase.selenium.fixture.Nokia7600BdpPage;
import au.com.sensis.wireless.test.selenium.UserAgent;

/**
 * Tests the BDP page.
 *
 * In order to run this test, do either:
 * <ol>
 * <li>start tomcat, then run ant execute-system-tests</li>
 * <li>run ant run-system-tests</li>
 * </ol>
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class BdpPageIntegrationTestCase extends AbstractSeleniumIntegrationTestCase {

    protected final void openBdp() {
        openUrl("http://" + getServerHostAndPort() + "/uidev/crfshowcase/crf/bdp.action");
    }

    protected final void openBdpWithBundlingOn() {
        openUrl("http://" + getServerHostAndPort()
                + "/uidev/crfshowcase/crf/bdp.action?config.change=env.bundle.resources,true");
    }

    protected final void openBdpWithUserAgent(final String userAgent) {
        openUrlWithUserAgent("http://" + getServerHostAndPort()
                + "/uidev/crfshowcase/crf/bdp.action", userAgent);
    }

    protected final void openIphoneClientAppBdpWithUserAgent(final String userAgent) {
        openUrlWithUserAgent("http://" + getServerHostAndPort()
                + "/uidev/crfshowcase/crf/bdp.action?client=iphone", userAgent);
    }

    /**
     * Test the page for a specific device.
     */
    public void testHD800Device() throws Exception {

        openBdp();

        getPageFixtureFactory().createPageFixture(HD800BdpPage.class);
    }

    /**
     * Test the page for a specific device.
     */
    public void testNokia7600Device() throws Exception {
        openBdpWithUserAgent(UserAgent.NOKIA_7600.getUserAgentString());

        getPageFixtureFactory().createPageFixture(Nokia7600BdpPage.class);
    }

    /**
     * Test the page for a specific device.
     */
    public void testIphoneOS3xDevice() throws Exception {

        /**
         * Assert expected out of the bundleScriptsTag.
         */
        openBdpWithUserAgent(UserAgent.IPHONE_OS3_1.getUserAgentString());

        getPageFixtureFactory().createPageFixture(IphoneOS3xBdpPage.class);

    }

    /**
     * Test the page for a specific device.
     */
    public void testIphoneOS3xClientAppDevice() throws Exception {
        openIphoneClientAppBdpWithUserAgent(UserAgent.IPHONE_OS3_1.getUserAgentString());

        getPageFixtureFactory().createPageFixture(IphoneOS3xClientAppBdpPage.class);

    }

    /**
     * Test the page for a specific device.
     */
    public void testIphoneOS4xDevice() throws Exception {
        openBdpWithUserAgent("Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_2_1 like Mac OS X; en-us) "
                + "AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 "
                + "Safari/6531.22.7");

        getPageFixtureFactory().createPageFixture(IphoneOS4xBdpPage.class);

    }

    /**
     * Test the page for a specific device.config.change=env.bundle.resources,true
     */
    public void testIphoneOS2xDevice() throws Exception {
        openBdpWithUserAgent(UserAgent.IPHONE_OS2_1.getUserAgentString());

        getPageFixtureFactory().createPageFixture(IphoneOS2xBdpPage.class);
    }

    /**
     * Assert expected out of the bundleScriptsTag.
     */

    /**
     * Test the page for a specific device - with bundling switched on.
     *
     * Please note - after this test bundling will be enabled for all subsequent tests.
     */
    public void testHD800DeviceWithBundlingOn() throws Exception {

        openBdpWithBundlingOn();

        getPageFixtureFactory().createPageFixture(HD800BundledBdpPage.class);
    }

}
