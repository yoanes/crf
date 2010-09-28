package au.com.sensis.mobile.crf.showcase.selenium.fixture;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.selenium.Selenium;

/**
 * Super class for pages.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public abstract class AbstractPageFixture
    extends au.com.sensis.wireless.test.selenium.fixture.AbstractPageFixture {

    private static final String PROJECT_VERSION_PROPERTY_NAME = "project.version.complete";
    private String projectVersion;

    /**
     * Default constructor.
     *
     * @param selenium {@link Selenium} instance to communicate with the browser.
     */
    public AbstractPageFixture(final Selenium selenium) {
        super(selenium);
        initProjectVersion();
    }

    private void initProjectVersion() {
        final String projectVersionProperty = System.getProperty(PROJECT_VERSION_PROPERTY_NAME);
        if (StringUtils.isBlank(projectVersionProperty)) {
            throw new IllegalStateException("System property " + PROJECT_VERSION_PROPERTY_NAME
                    + " must be set to the non-blank version of your deployed showcase. Was: '"
                    + projectVersionProperty + "'");
        }
        setProjectVersion(projectVersionProperty);
    }

    /**
     * @return Version of the deployed app.
     */
    protected final String getProjectVersion() {
        return projectVersion;
    }

    private void setProjectVersion(final String projectVersion) {
        this.projectVersion = projectVersion;
    }

}
