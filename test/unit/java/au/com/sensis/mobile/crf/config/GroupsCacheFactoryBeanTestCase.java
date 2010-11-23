package au.com.sensis.mobile.crf.config;

import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link SimpleGroupsCacheFactoryBean}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GroupsCacheFactoryBeanTestCase extends AbstractJUnit4TestCase {

    @Test
    public void testCreateGroupsCacheWhenCacheEnabled() throws Throwable {

        final GroupsCache groupsCache =
                createSimpleGroupsCacheFactoryBeanWhenCacheEnabled().createGroupsCache();

        Assert.assertEquals("groupsCache should be a SimpleGroupsCacheBean",
                SimpleGroupsCacheBean.class, groupsCache.getClass());

        Assert.assertTrue("groupsCache should be enabled", groupsCache.isEnabled());

    }

    @Test
    public void testCreateGroupsCacheWhenCacheDisabled() throws Throwable {

        final GroupsCache groupsCache =
            createSimpleGroupsCacheFactoryBeanWhenCacheDisabled().createGroupsCache();

        Assert.assertEquals("groupsCache should be a SimpleGroupsCacheBean",
                SimpleGroupsCacheBean.class, groupsCache.getClass());

        Assert.assertFalse("groupsCache should be disabled", groupsCache.isEnabled());

    }

    private SimpleGroupsCacheFactoryBean createSimpleGroupsCacheFactoryBeanWhenCacheEnabled() {
        return new SimpleGroupsCacheFactoryBean(true);
    }

    private SimpleGroupsCacheFactoryBean createSimpleGroupsCacheFactoryBeanWhenCacheDisabled() {
        return new SimpleGroupsCacheFactoryBean(false);
    }
}
