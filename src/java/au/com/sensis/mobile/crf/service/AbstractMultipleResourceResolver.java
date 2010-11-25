package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * {@link ResourceResolver} that can resolve to multiple resources.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractMultipleResourceResolver extends AbstractResourceResolver {

    private final ResourceAccumulatorFactory resourceAccumulatorFactory;

    /**
     * Constructor.
     *
     * @param commonParams
     *            Holds the common parameters used in constructing all {@link ResourceResolver}s.
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param resourceCache {@link ResourceCache} for caching {@link Resource}s.
     */
    public AbstractMultipleResourceResolver(final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension, final File rootResourcesDir,
            final ResourceCache resourceCache) {
        super(commonParams, abstractResourceExtension, rootResourcesDir, resourceCache);

        resourceAccumulatorFactory = commonParams.getResourceAccumulatorFactory();
    }

    /**
     * @return the {@link ResourceAccumulatorFactory} from which to obtain a
     *         {@link ResourceAccumulator} implementation.
     */
    protected ResourceAccumulatorFactory getResourceAccumulatorFactory() {
        return resourceAccumulatorFactory;
    }

    /**
     * Invoked by {@link #resolve(String, Device)} if
     * {@link #isRecognisedAbstractResourceRequest(String)} resturns true.
     *
     * @param requestedResourcePath
     *            Requested path. eg. /WEB-INF/view/jsp/detal/bdp.crf.
     * @param device
     *            {@link Device} to perform the path mapping for.
     * @return List of {@link Resource}s containing the results. If no resources
     *         can be resolved, an empty list is returned. May not be null.
     */
    @Override
    protected List<Resource> doResolve(final String requestedResourcePath, final Device device) {

        final ResourceAccumulator accumulator = createResourceAccumulator();

        final Iterator<Group> matchingGroupIterator =
                getMatchingGroupIterator(device, requestedResourcePath);

        while (matchingGroupIterator.hasNext()) {

            final Group currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(requestedResourcePath, currGroup);

            accumulator.accumulate(
                    resolveForGroupPossiblyFromCache(requestedResourcePath, currGroup));

        }

        return accumulator.getResources();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Resource> resolveForGroupPossiblyFromCache(
            final String requestedResourcePath, final Group currGroup) {
        // TODO: if bundling is enabled, this method should check the cache for a bundle.
        // If found, the bundle should be returned from _doResolve_ immediately. Otherwise
        // the resolve should occur but the results not added to the cache. Only when
        // the bundle is created can the bundle be added to the cache.
        return super.resolveForGroupPossiblyFromCache(requestedResourcePath, currGroup);
    }

    /**
     * @return Returns a new {@link ResourceAccumulator} for this {@link ResourceResolver}.
     */
    protected abstract ResourceAccumulator createResourceAccumulator();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addResourcesToResourceResolutionTreeIfEnabled(final List<Resource> resources) {
        // This is done in the accumulator.
        // TODO: refactor to eliminate this really ugly override.
    }
}
