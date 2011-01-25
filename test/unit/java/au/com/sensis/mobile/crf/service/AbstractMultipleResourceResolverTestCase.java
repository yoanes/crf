package au.com.sensis.mobile.crf.service;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;

/**
 * Base test class for {@link AbstractMultipleResourceResolver}s.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractMultipleResourceResolverTestCase extends
        AbstractResourceResolverTestCase {

    @Deprecated
    protected void recordIsBundlingEnabled(final Boolean bundlingEnabled) {
        EasyMock.expect(getMockResourceAccumulator().isBundlingEnabled())
                .andReturn(bundlingEnabled).atLeastOnce();
    }

    protected void recordGetFromResourceCache(final ResourceCacheKey resourceCacheKey,
            final List<Resource> accumulatedResources) {

        final ResourceCacheEntryBean resourceCacheEntryBean =
                new ResourceCacheEntryBean(accumulatedResources.toArray(new Resource[] {}),
                    ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                    ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS);
        EasyMock.expect(getMockResourceCache().get(resourceCacheKey)).andReturn(
                resourceCacheEntryBean);
    }

    protected void recordGetResourcesFromAccumulator(final List<Resource> expectedResources) {
        EasyMock.expect(getMockResourceAccumulator().getResources()).andReturn(expectedResources);
    }

    protected void recordGetResourcesFromAccumulator(final Resource expectedResource) {
        EasyMock.expect(getMockResourceAccumulator().getResources()).andReturn(
                Arrays.asList(expectedResource));
    }

}
