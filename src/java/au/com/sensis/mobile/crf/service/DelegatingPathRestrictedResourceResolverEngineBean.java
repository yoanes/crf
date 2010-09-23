package au.com.sensis.mobile.crf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * {@link ResourceResolverEngine} that delegates each request
 * to a {@link List} of {@link PathRestrictedResourceResolverEngine} instances.
 * Only the engine for which
 * {@link PathRestrictedResourceResolverEngine#isInterestedIn(String)}
 * returns true will handle the request. If no such engine is
 * found, {@link #getDefaultResourceResolverEngine()} is used instead.
 * <p>
 * Each {@link PathRestrictedResourceResolverEngine} is added to this
 * {@link DelegatingPathRestrictedResourceResolverEngineBean} automatically as long as it
 * is defined in the same Spring context. This is facilitated by partaking
 * in the Spring {@link BeanPostProcessor} lifecycle.
 * </p>
 * <p>
 * The order in which {@link PathRestrictedResourceResolverEngine}s are checked is undefined.
 * ie. we implicitly expect the path prefix of each engine to be unique.
 * </p>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DelegatingPathRestrictedResourceResolverEngineBean
        implements ResourceResolverEngine, BeanPostProcessor {

    /**
     * Not final to allow injection of a mock for unit testing.
     */
    private static Logger logger = Logger.getLogger(
            DelegatingPathRestrictedResourceResolverEngineBean.class);

    private final List<PathRestrictedResourceResolverEngine>
        pathRestrictedResourceResolverEngines =
            new ArrayList<PathRestrictedResourceResolverEngine>();

    private final ResourceResolverEngine defaultResourceResolverEngine;

    /**
     * Constructor.
     *
     * @param defaultResourceResolverEngine
     *            {@link ResourceResolverEngine} to use by
     *            default if no
     *            {@link PathRestrictedResourceResolverEngine}
     *            is found for which
     *            {@link PathRestrictedResourceResolverEngine#isInterestedIn(String)}
     *            returns true.
     */
    public DelegatingPathRestrictedResourceResolverEngineBean(
            final ResourceResolverEngine defaultResourceResolverEngine) {
        Validate.notNull(defaultResourceResolverEngine,
                "defaultResourceResolverEngine must not be null");
        this.defaultResourceResolverEngine = defaultResourceResolverEngine;

    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public List<MappedResourcePath> getAllResourcePaths(final Device device,
            final String requestedResourcePath) throws IOException {

        for (final PathRestrictedResourceResolverEngine resourceSelector
                : getPathRestrictedResourceResolverEngines()) {

            if (resourceSelector.isInterestedIn(requestedResourcePath)) {
                debugLogFoundInterestedSelector(requestedResourcePath, resourceSelector);

                return resourceSelector.getAllResourcePaths(device, requestedResourcePath);
            }
        }

        debugLogNoInterestedSelectorFound(requestedResourcePath);

        return getDefaultResourceResolverEngine().getAllResourcePaths(device,
                requestedResourcePath);
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public MappedResourcePath getResourcePath(final Device device,
            final String requestedResourcePath) throws IOException {

        for (final PathRestrictedResourceResolverEngine resourceSelector
                : getPathRestrictedResourceResolverEngines()) {

            if (resourceSelector.isInterestedIn(requestedResourcePath)) {
                debugLogFoundInterestedSelector(requestedResourcePath, resourceSelector);

                return resourceSelector.getResourcePath(device, requestedResourcePath);
            }
        }

        debugLogNoInterestedSelectorFound(requestedResourcePath);

        return getDefaultResourceResolverEngine().getResourcePath(device,
                requestedResourcePath);
    }

    /**
     * All {@link PathRestrictedResourceResolverEngine}
     * instances are added to the internal delegation chain. The order
     * in which they are added is undefined.
     *
     * {@inheritDoc}
     */
    @Override
    public Object postProcessAfterInitialization(final Object bean,
            final String beanName) throws BeansException {

        if (bean instanceof PathRestrictedResourceResolverEngine) {
            final PathRestrictedResourceResolverEngine selector =
                    (PathRestrictedResourceResolverEngine) bean;

            if (logger.isInfoEnabled()) {
                logger.info("Adding bean to list. Bean name: '" + beanName
                        + "'. Bean: " + bean + ".");
            }

            getPathRestrictedResourceResolverEngines().add(selector);
        }
        return bean;
    }

    /**
     * Simply returns the original bean.
     *
     * {@inheritDoc}
     */
    @Override
    public Object postProcessBeforeInitialization(final Object bean,
            final String beanName) throws BeansException {
        return bean;
    }

    private ResourceResolverEngine getDefaultResourceResolverEngine() {
        return defaultResourceResolverEngine;
    }

    private List<PathRestrictedResourceResolverEngine>
        getPathRestrictedResourceResolverEngines() {
        return pathRestrictedResourceResolverEngines;
    }

    private void debugLogFoundInterestedSelector(final String requestedResourcePath,
            final PathRestrictedResourceResolverEngine selector) {
        if (logger.isDebugEnabled()) {
            logger
                    .debug("Found a resourceSelector interested in requested path: '"
                            + requestedResourcePath
                            + "'. Selector: "
                            + selector);
        }
    }

    private void debugLogNoInterestedSelectorFound(
            final String requestedResourcePath) {
        if (logger.isDebugEnabled()) {
            logger
                    .debug("No interested resourceSelector was found for requested path: '"
                            + requestedResourcePath
                            + "'. Delegating to defaultResourceResolverEngine: "
                            + getDefaultResourceResolverEngine());
        }
    }

}
