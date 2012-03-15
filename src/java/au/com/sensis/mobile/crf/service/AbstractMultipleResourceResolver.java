package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.mobile.crf.config.Group;

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

        accumulateResources(requestedResourcePath, device, accumulator);

        return accumulator.getResources();

    }

    private void accumulateResources(final String requestedResourcePath, final Device device,
            final ResourceAccumulator accumulator) {
        final Iterator<Group> matchingGroupIterator =
                getMatchingGroupIterator(device, requestedResourcePath);

        while (matchingGroupIterator.hasNext()) {

            final Group currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(requestedResourcePath, currGroup);

            accumulator.accumulate(resolveForGroup(requestedResourcePath, device, currGroup));

        }
    }

    /**
     * @return Returns a new {@link ResourceAccumulator} for this {@link ResourceResolver}.
     */
    protected abstract ResourceAccumulator createResourceAccumulator();
}
