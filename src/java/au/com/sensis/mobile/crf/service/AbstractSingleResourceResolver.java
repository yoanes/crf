package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * {@link ResourceResolver} that will only ever resolve to, at most, a single resource.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractSingleResourceResolver extends AbstractResourceResolver {

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
     */
    public AbstractSingleResourceResolver(final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension, final File rootResourcesDir) {
        super(commonParams, abstractResourceExtension, rootResourcesDir);
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

        final Iterator<Group> matchingGroupIterator =
                getMatchingGroupIterator(device, requestedResourcePath);

        while (matchingGroupIterator.hasNext()) {

            final Group currGroup = matchingGroupIterator.next();

            debugLogCheckingGroup(requestedResourcePath, currGroup);

            final List<Resource> resolvedResources =
                    resolveForGroup(requestedResourcePath, currGroup);
            if (!resolvedResources.isEmpty()) {
                // Return immediately once a resource is found.
                return resolvedResources;
            }

        }

        // No resources found.
        return new ArrayList<Resource>();
    }
}
