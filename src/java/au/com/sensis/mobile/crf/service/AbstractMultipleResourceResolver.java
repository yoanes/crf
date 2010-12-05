package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Arrays;
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
     *            Holds the common parameters used in constructing all
     *            {@link ResourceResolver}s.
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param resourceAccumulatorFactory
     *            Provides a {@link ResourceAccumulator} for this
     *            {@link ResourceResolver}.
     */
    public AbstractMultipleResourceResolver(final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension, final File rootResourcesDir,
            final ResourceAccumulatorFactory resourceAccumulatorFactory) {
        super(commonParams, abstractResourceExtension, rootResourcesDir);

        validateResourceAccumulatorFactory(resourceAccumulatorFactory);

        this.resourceAccumulatorFactory = resourceAccumulatorFactory;
    }

    private void validateResourceAccumulatorFactory(
            final ResourceAccumulatorFactory resourceAccumulatorFactory) {

        if (resourceAccumulatorFactory == null) {
            throw new IllegalArgumentException("resourceAccumulatorFactory must not be null");
        }
    }

    /**
     * @return the {@link ResourceAccumulatorFactory} from which to obtain a
     *         {@link ResourceAccumulator} implementation.
     */
    protected final ResourceAccumulatorFactory getResourceAccumulatorFactory() {
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

        if (accumulator.isBundlingEnabled()) {
            final ResourceCacheKey resourceCacheKey =
                    createBundleResourceCacheKey(requestedResourcePath, device);
            final List<Resource> cachedBundle = getCachedBundle(resourceCacheKey);
            if (cachedBundle != null) {
                addResourcesToResourceResolutionTreeIfEnabled(cachedBundle);
                return cachedBundle;
            }

            accumulateResources(requestedResourcePath, device, accumulator);

            final List<Resource> accumulatedResources = accumulator.getResources();
            if (resourceCacheKey != null) {
                getResourceCache().put(resourceCacheKey,
                        accumulatedResources.toArray(new Resource[] {}));

            }
            addResourcesToResourceResolutionTreeIfEnabled(accumulatedResources);
            return accumulatedResources;
        } else {
            accumulateResources(requestedResourcePath, device, accumulator);

            final List<Resource> accumulatedResources = accumulator.getResources();
            addResourcesToResourceResolutionTreeIfEnabled(accumulatedResources);
            return accumulatedResources;
        }

    }

    private List<Resource> getCachedBundle(final ResourceCacheKey resourceCacheKey) {
        if ((resourceCacheKey != null) && getResourceCache().contains(resourceCacheKey)) {
            debugLogResourcesFoundInCache();
            return Arrays.asList(getResourceCache().get(resourceCacheKey));
        } else {
            debugLogResourcesNotFoundInCache();
            return null;
        }

    }

    private void accumulateResources(final String requestedResourcePath, final Device device,
            final ResourceAccumulator accumulator) {
        final Iterator<Group> matchingGroupIterator =
                getMatchingGroupIterator(device, requestedResourcePath);

        while (matchingGroupIterator.hasNext()) {

            final Group currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(requestedResourcePath, currGroup);

            if (accumulator.isBundlingEnabled()) {
                accumulator.accumulate(resolveForGroup(requestedResourcePath, currGroup));
            } else {
                accumulator.accumulate(resolveForGroupPossiblyFromCache(requestedResourcePath,
                        currGroup));
            }

        }
    }

    private ResourceCacheKey createBundleResourceCacheKey(final String requestedResourcePath,
            final Device device) {
        final Iterator<Group> matchingGroupIterator =
                getMatchingGroupIterator(device, requestedResourcePath);
        if (matchingGroupIterator.hasNext()) {
            return new ResourceCacheKeyBean(requestedResourcePath, matchingGroupIterator.next());
        } else {
            return null;
        }
    }

    /**
     * @return Returns a new {@link ResourceAccumulator} for this {@link ResourceResolver}.
     */
    protected abstract ResourceAccumulator createResourceAccumulator();
}
