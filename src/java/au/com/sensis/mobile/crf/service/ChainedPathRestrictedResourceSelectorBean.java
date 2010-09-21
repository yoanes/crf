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
 * {@link ResourceSelector} that delegates each request
 * to a chain of {@link PathRestrictedResourceSelector} instances.
 * Only the selector for which
 * {@link PathRestrictedResourceSelector#isInterestedIn(String)}
 * returns true will handle the request. If no such selector is
 * found, the {@link #getDefaultSelector()} is used instead.
 * <p>
 * Each {@link PathRestrictedResourceSelector} is added to this
 * {@link ChainedPathRestrictedResourceSelectorBean} automatically as long as it
 * is defined in the same Spring context. This is facilitated by partaking
 * in the {@link BeanPostProcessor} lifecycle.
 * </p>
 * <p>
 * The order in which {@link PathRestrictedResourceSelector}s are checked is undefined.
 * </p>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ChainedPathRestrictedResourceSelectorBean
        implements ResourceSelector, BeanPostProcessor {

    /**
     * Not final to allow injection of a mock for unit testing.
     */
    private static Logger logger = Logger.getLogger(
            ChainedPathRestrictedResourceSelectorBean.class);

    private final List<PathRestrictedResourceSelector>
        pathRestrictedResourceSelectors =
            new ArrayList<PathRestrictedResourceSelector>();

    private final ResourceSelector defaultSelector;

    /**
     * Constructor.
     *
     * @param defaultSelector
     *            {@link ResourceSelector} to use by
     *            default if no
     *            {@link PathRestrictedResourceSelector}
     *            is found for which
     *            {@link PathRestrictedResourceSelector#isInterestedIn(String)}
     *            returns true.
     */
    public ChainedPathRestrictedResourceSelectorBean(
            final ResourceSelector defaultSelector) {
        Validate.notNull(defaultSelector, "defaultSelector must not be null");
        this.defaultSelector = defaultSelector;

    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public List<MappedResourcePath> getAllResourcePaths(final Device device,
            final String requestedResourcePath) throws IOException {

        for (final PathRestrictedResourceSelector resourceSelector
                : getPathRestrictedResourceSelectors()) {

            if (resourceSelector.isInterestedIn(requestedResourcePath)) {
                debugLogFoundInterestedSelector(requestedResourcePath, resourceSelector);

                return resourceSelector.getAllResourcePaths(device, requestedResourcePath);
            }
        }

        debugLogNoInterestedSelectorFound(requestedResourcePath);

        return getDefaultSelector().getAllResourcePaths(device,
                requestedResourcePath);
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public MappedResourcePath getResourcePath(final Device device,
            final String requestedResourcePath) throws IOException {

        for (final PathRestrictedResourceSelector resourceSelector
                : getPathRestrictedResourceSelectors()) {

            if (resourceSelector.isInterestedIn(requestedResourcePath)) {
                debugLogFoundInterestedSelector(requestedResourcePath, resourceSelector);

                return resourceSelector.getResourcePath(device, requestedResourcePath);
            }
        }

        debugLogNoInterestedSelectorFound(requestedResourcePath);

        return getDefaultSelector().getResourcePath(device,
                requestedResourcePath);
    }

    /**
     * All {@link PathRestrictedResourceSelector}
     * instances are added to the internal delegation chain. The order
     * in which they are added is undefined.
     *
     * {@inheritDoc}
     */
    @Override
    public Object postProcessAfterInitialization(final Object bean,
            final String beanName) throws BeansException {

        if (bean instanceof PathRestrictedResourceSelector) {
            final PathRestrictedResourceSelector selector =
                    (PathRestrictedResourceSelector) bean;

            if (logger.isInfoEnabled()) {
                logger.info("Adding bean to list. Bean name: '" + beanName
                        + "'. Bean: " + bean + ".");
            }

            getPathRestrictedResourceSelectors().add(selector);
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

    private ResourceSelector getDefaultSelector() {
        return defaultSelector;
    }

    private List<PathRestrictedResourceSelector>
        getPathRestrictedResourceSelectors() {
        return pathRestrictedResourceSelectors;
    }

    private void debugLogFoundInterestedSelector(final String requestedResourcePath,
            final PathRestrictedResourceSelector selector) {
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
                            + "'. Delegating to defaultSelector: "
                            + getDefaultSelector());
        }
    }

}
