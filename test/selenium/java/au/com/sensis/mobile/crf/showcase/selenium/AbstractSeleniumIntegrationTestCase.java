package au.com.sensis.mobile.crf.showcase.selenium;


/**
 * Super class for all selenium test cases.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public abstract class AbstractSeleniumIntegrationTestCase
    extends au.com.sensis.wireless.test.selenium.AbstractSeleniumIntegrationTestCase {

    @Override
    protected void openHome() {
        openUrl("http://localhost:8080/bdp.action");
    }
}
