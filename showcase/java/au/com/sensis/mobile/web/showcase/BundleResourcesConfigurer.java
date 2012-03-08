package au.com.sensis.mobile.web.showcase;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import au.com.sensis.mobile.crf.presentation.tag.BundleTagDependencies;
import au.com.sensis.mobile.crf.service.ResourceAccumulatorFactory;

/**
 * Can modify the configuration for the property env.bundle.resources.
 *
 * We have to find all the spring manager beans that use it and change their value.
 *
 * @author Boyd Sharrock.
 *
 */
public class BundleResourcesConfigurer implements DebugConfigurer {

    private static final Logger LOGGER = Logger.getLogger(BundleResourcesConfigurer.class);

    @Override
    public void applyDebugSetup(final String[] debugInfo, final ServletContext servletContext) {

        LOGGER.debug(">>applyDebugSetup...");

        if (debugInfo.length <= 1) { return; }

        final boolean bundlingEnabledNewValue =
            "true".equals(debugInfo[1]) ? true : false;

        LOGGER.debug(">>applyDebugSetup env.bundle.resources=" + bundlingEnabledNewValue);

        final WebApplicationContext applicationContext =
            WebApplicationContextUtils.getWebApplicationContext(servletContext);

        final BundleTagDependencies scriptsDependancies = (BundleTagDependencies)
        applicationContext.getBean("crf.bundleScriptsTagDependencies");
        final BundleTagDependencies linksDependancies = (BundleTagDependencies)
        applicationContext.getBean("crf.bundleLinksTagDependencies");
        final ResourceAccumulatorFactory factory = (ResourceAccumulatorFactory)
        applicationContext.getBean("crf.resourceAccumulatorFactory");

        scriptsDependancies.setBundlingEnabled(bundlingEnabledNewValue);
        linksDependancies.setBundlingEnabled(bundlingEnabledNewValue);
        factory.setBundlingEnabled(bundlingEnabledNewValue);
    }

}
