package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import com.thoughtworks.selenium.Selenium;

/**
 * Super class for pages.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public abstract class AbstractPageFixture
    extends au.com.sensis.wireless.test.selenium.fixture.AbstractPageFixture {

    /**
     * Default constructor.
     *
     * @param selenium {@link Selenium} instance to communicate with the browser.
     */
    public AbstractPageFixture(final Selenium selenium) {
        super(selenium);
    }

}
