package au.com.sensis.mobile.crf.showcase.selenium;

import org.apache.commons.lang.StringUtils;


/**
 * Super class for all selenium test cases.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public abstract class AbstractSeleniumIntegrationTestCase
    extends au.com.sensis.wireless.test.selenium.AbstractSeleniumIntegrationTestCase {

    private static final String SERVER_HOSTNAME_PROPERTY_NAME = "server.hostname";
    private static final String SERVER_PORT_PROPERTY_NAME = "server.port";
    private String serverHostname;
    private String serverPort;

    public AbstractSeleniumIntegrationTestCase() {
        super();

        initServerHostname();
        initServerPort();

    }

    private void initServerHostname() {
        final String serverHostnameProperty = System.getProperty(SERVER_HOSTNAME_PROPERTY_NAME);
        if (StringUtils.isBlank(serverHostnameProperty)) {
            throw new IllegalStateException("System property " + SERVER_HOSTNAME_PROPERTY_NAME
                    + " must be set to the non-blank version of your deployed showcase. Was: '"
                    + serverHostnameProperty + "'");
        }
        setServerHostname(serverHostnameProperty);
    }

    private void initServerPort() {
        final String serverPortProperty = System.getProperty(SERVER_PORT_PROPERTY_NAME);
        if (StringUtils.isBlank(serverPortProperty)) {
            throw new IllegalStateException("System property " + SERVER_PORT_PROPERTY_NAME
                    + " must be set to the non-blank version of your deployed showcase. Was: '"
                    + serverPortProperty + "'");
        }
        setServerPort(serverPortProperty);
    }

    @Override
    protected void openHome() {
        openUrl("http://" + getServerHostAndPort() + "/bdp.action");
    }

    protected final String getServerHostAndPort() {
        return getServerHostname() + ":" + getServerPort();
    }

    /**
     * @return the serverHostname
     */
    protected final String getServerHostname() {
        return serverHostname;
    }

    /**
     * @param serverHostname the serverHostname to set
     */
    private void setServerHostname(final String serverHostname) {
        this.serverHostname = serverHostname;
    }

    /**
     * @return the serverPort
     */
    protected final String getServerPort() {
        return serverPort;
    }

    /**
     * @param serverPort the serverPort to set
     */
    private void setServerPort(final String serverPort) {
        this.serverPort = serverPort;
    }
}
