package au.com.sensis.mobile.web.showcase;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Provides the logic for applying debug configuration(s) to a single request.
 *
 * When the "config.change" request parameter is supplied it will provide information
 * that will drive special setup
 *
 * ie. config.change=env.bundle.resources,true
 *
 * This is primarily to help with debugging and automated testing.
 *
 * @author Boyd Sharrock.
 *
 */
public class DebugConfigurationManager {

    private static final Logger LOGGER = Logger.getLogger(DebugConfigurationManager.class);

    private static final String DEBUG_FLAG = "config.change";

    private static final Map<String, DebugConfigurer> DEBUG_CONFIGURERS =
        new HashMap<String, DebugConfigurer>();
    static {
        DEBUG_CONFIGURERS.put("env.bundle.resources", new BundleResourcesConfigurer());
    }

    /**
     * If the debug flag is set in the session - then apply any debug configuration
     * that is specified.
     *
     * @param request The {@link HttpServletRequest}.
     * @param servletContext The {@link ServletContext}.
     */
    public void applyDebugConfigurationIfRequired(final HttpServletRequest request,
            final ServletContext servletContext) {

        final String debugConfiguration = request.getParameter(DEBUG_FLAG);

        LOGGER.debug("debugConfiguration - " + debugConfiguration);

        if (StringUtils.isNotBlank(debugConfiguration)) {

            final String[] debugParameterConfigs = debugConfiguration.split(":");

            for (final String debugParameterConfig : debugParameterConfigs) {

                LOGGER.debug("debugParameterConfig - " + debugParameterConfig);

                final String[] debugParameterNameAndInfo = debugParameterConfig.split(",");

                applyDebugConfiguration(debugParameterNameAndInfo[0], debugParameterNameAndInfo,
                        servletContext);
            }
        }
    }

    // Utility //

    private void applyDebugConfiguration(final String flag,
            final String[] debugParameterNameAndInfo, final ServletContext servletContext) {

        final DebugConfigurer configurer = DEBUG_CONFIGURERS.get(flag);

        if (configurer != null) {

            configurer.applyDebugSetup(debugParameterNameAndInfo, servletContext);
        }
    }

}
