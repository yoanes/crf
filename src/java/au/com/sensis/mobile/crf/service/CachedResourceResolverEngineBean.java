package au.com.sensis.mobile.crf.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;


/**
 * Provides optional caching of Resource requests, if enabled.
 * Otherwise it passes the resource resolution through to the underlying
 * {@link ResourceResolverEngine}.
 *
 * @author Tony Filipe
 * @deprecated Deprecated in favour of caching at a more fine grained level. Resurrect this class
 * if requirements change.
 */
@Deprecated
public class CachedResourceResolverEngineBean
implements ResourceResolverEngine {

    private static final Logger LOGGER = Logger.getLogger(CachedResourceResolverEngineBean.class);
    private ConcurrentHashMap<Integer, List<Resource>> resourceMapCache;
    private final ResourceResolverEngine resourceResolverEngine;
    private final boolean cachingEnabled;


    /**
     * Constructs an initialised CachedResourceResolverEngineBean.
     * @param resourceResolverEngine the underlying {@link ResourceResolverEngine} used when
     *  caching is disabled or the requested resource can't be found in the cache.
     * @param cachingEnabled determines whether the request is serveed from cache or simply passes
     *  through to the underlying {@link ResourceResolverEngine} for resolution.
     */
    public CachedResourceResolverEngineBean(final ResourceResolverEngine resourceResolverEngine,
            final boolean cachingEnabled) {

        Validate.notNull(resourceResolverEngine, "resourceResolverEngine must not be null");

        this.resourceResolverEngine = resourceResolverEngine;
        this.cachingEnabled = cachingEnabled;

        resourceMapCache = new ConcurrentHashMap<Integer, List<Resource>>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getAllResources(final Device device, final String requestedResourcePath)
    throws ResourceResolutionRuntimeException {

        List<Resource> resources;

        if (isCachingEnabled()) {

            final int key = generateHashKey(device, requestedResourcePath);

            // Get resources from Cache if available
            if (getResourceMapCache().containsKey(key)) {

                resources = getResourceMapCache().get(key);

            } else {
                // Resolve the resources for this request and add them to Cache
                resources = fetchAllResources(device, requestedResourcePath);
                getResourceMapCache().put(key, resources);
            }

        } else {
            resources = fetchAllResources(device, requestedResourcePath);
        }

        return resources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getResource(final Device device, final String requestedResourcePath)
    throws ResourceResolutionRuntimeException {

        Resource resource;

        if (isCachingEnabled()) {

            final int key = generateHashKey(device, requestedResourcePath);

            // Get resources from Cache if available
            if (getResourceMapCache().containsKey(key)) {

                // We return the FIRST value in the list.
                // There should only ever be ONE value in the Cache list.
                resource = getResourceMapCache().get(key).get(0);

            } else {
                // Resolve the resource for this request and add it to the Cache
                resource = fetchResource(device, requestedResourcePath);
                getResourceMapCache().put(key, Arrays.asList(resource));
            }

        } else {
            resource = fetchResource(device, requestedResourcePath);
        }

        if (requestedResourcePath.indexOf(".crf") > -1) {
            // can add it to list, put into req to be accessed & output in JSP
            LOGGER.info("Including JSP: " + resource.getNewPath());
        }
        return resource;
    }

    /**
     * Generates a unique hash key for the given {@link Device} and requested resource.
     *
     * @param device for which the given requested resource is for
     * @param requestedResourcePath to be resolved for the given {@link Device}
     * @return a unique hash key for the given {@link Device} and requested resource
     */
    protected int generateHashKey(final Device device, final String requestedResourcePath) {
        return (device.getUserAgent() + requestedResourcePath).hashCode();
    }

    /**
     * Delegates to the underlying {@link ResourceResolverEngine} to get all matching resources
     * for the given {@link Device}.
     * @param device to get all resources for
     * @param requestedResourcePath to resolve all resources for the given {@link Device}
     * @return a list of matching {@link Resource}s
     */
    protected List<Resource> fetchAllResources(final Device device,
            final String requestedResourcePath) {

        return getResourceResolverEngine().getAllResources(device, requestedResourcePath);
    }

    /**
     * Delegates to the underlying {@link ResourceResolverEngine} to get the matching resource
     * for the given {@link Device}.
     * @param device to get the resource for
     * @param requestedResourcePath for which to find a matching {@link Resource} for the
     * given {@link Device}
     * @return the {@link Resource} matching the given {@link Device}
     */
    protected Resource fetchResource(final Device device, final String requestedResourcePath) {

        return getResourceResolverEngine().getResource(device, requestedResourcePath);
    }

    /**
     * @return the resourceResolverEngine
     */
    protected ResourceResolverEngine getResourceResolverEngine() {

        return resourceResolverEngine;
    }

    /**
     * @return the cachingEnabled
     */
    protected boolean isCachingEnabled() {

        return cachingEnabled;
    }

    /**
     * @return the resourceMapCache
     */
    protected ConcurrentHashMap<Integer, List<Resource>> getResourceMapCache() {

        return resourceMapCache;
    }

    /**
     * @param resourceMapCache  the resourceMapCache to set
     */
    protected void setResourceMapCache(
            final ConcurrentHashMap<Integer, List<Resource>> resourceMapCache) {

        this.resourceMapCache = resourceMapCache;
    }

}
