package au.com.sensis.mobile.crf.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.Validate;

import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;


/**
 * 
 * 
 * @author Tony Filipe
 */
public class CachedResourceResolverEngineBean
implements ResourceResolverEngine {


    private ConcurrentHashMap<Integer, List<Resource>> resourceMapCache;
    private final ResourceResolverEngine resourceResolverEngine;
    private final boolean cachingEnabled;


    public CachedResourceResolverEngineBean(final ResourceResolverEngine resourceResolverEngine,
            final boolean cachingEnabled) {

        Validate.notNull(resourceResolverEngine, "resourceResolverEngine must not be null");

        this.resourceResolverEngine = resourceResolverEngine;
        this.cachingEnabled = cachingEnabled;

        resourceMapCache = new ConcurrentHashMap<Integer, List<Resource>>();
    }


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

        return resource;
    }

    protected int generateHashKey(final Device device, final String requestedResourcePath) {
        return (device.getName() + requestedResourcePath).hashCode();
    }

    protected List<Resource> fetchAllResources(final Device device,
            final String requestedResourcePath) {

        return getResourceResolverEngine().getAllResources(device, requestedResourcePath);
    }

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
