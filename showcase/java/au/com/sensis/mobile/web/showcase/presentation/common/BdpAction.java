package au.com.sensis.mobile.web.showcase.presentation.common;

import java.util.Properties;

import au.com.sensis.mobile.crf.service.PropertiesLoader;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;

/**
 * Entry action into the webapp for selenium tests to hit.
 */
public class BdpAction extends BusinessAction {

    private final PropertiesLoader propertiesLoader;

    /**
     * Constructor.
     *
     * @param propertiesLoader {@link PropertiesLoader} to use to load a properties file.
     */
    public BdpAction(final PropertiesLoader propertiesLoader) {
        this.propertiesLoader = propertiesLoader;
    }

    /**
     * I do nothing...
     *
     * @return success result.
     */
    public String execute() {

        return ResultName.SUCCESS;
    }

    /**
     * @return As per CRF-67 acceptance criteria, app.property1 retrieved from
     *         main.properties, where the properties file is looked up via the
     *         {@link #getPropertiesLoader()}.
     */
    public String getAppProperty1() {
        final Properties properties =
                getPropertiesLoader().loadProperties(getContext().getDevice(), "main.properties");
        return properties.getProperty("app.property1");
    }

    /**
     * @return the propertiesLoader
     */
    private PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }

}
