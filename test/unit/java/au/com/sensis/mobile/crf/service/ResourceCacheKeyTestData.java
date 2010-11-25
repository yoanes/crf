package au.com.sensis.mobile.crf.service;

import au.com.sensis.mobile.crf.config.GroupTestData;

/**
 * Test data for {@link ResourceCacheKey}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ResourceCacheKeyTestData {

    private final GroupTestData groupTestData = new GroupTestData();
    private final ResourcePathTestData resourcePathTestData = new ResourcePathTestData();

    public ResourceCacheKey createResourceCacheKey() {
        return new ResourceCacheKeyBean(getResourcePathTestData().getRequestedJspResourcePath(),
                getGroupTestData().createIPhoneGroup());
    }

    /**
     * @return the groupTestData
     */
    private GroupTestData getGroupTestData() {
        return groupTestData;
    }

    /**
     * @return the resourcePathTestData
     */
    private ResourcePathTestData getResourcePathTestData() {
        return resourcePathTestData;
    }
}
