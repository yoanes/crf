package au.com.sensis.mobile.crf.util;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.log4j.Logger;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link EhcacheStatisticsLogger}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheStatisticsLoggerTestCase extends AbstractJUnit4TestCase {

    private static final String CACHE_NAME = "myCacheName";

    private EhcacheStatisticsLogger objectUnderTest;

    private Ehcache ehcache;
    private CacheManager cacheManager;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setCacheManager(new CacheManager());
        getCacheManager().addCache(CACHE_NAME);

        setEhcache(getCacheManager().getEhcache(CACHE_NAME));
        getEhcache().setStatisticsEnabled(true);

        setObjectUnderTest(new EhcacheStatisticsLogger(getEhcache()));

        swapOutRealLoggerForMock(EhcacheStatisticsLogger.class);
    }

    @Test
    public void testLogStats() throws Throwable {

        EasyMock.expect(getMockLogger().isInfoEnabled()).andReturn(Boolean.TRUE);

        getMockLogger().info("Statistics: " + getEhcache().getStatistics());

        replay();

        getObjectUnderTest().logStats();
    }

    private Logger getMockLogger() {
        return getMockLogger(EhcacheStatisticsLogger.class);
    }

    /**
     * @return the objectUnderTest
     */
    private EhcacheStatisticsLogger getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final EhcacheStatisticsLogger objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the ehcache
     */
    public Ehcache getEhcache() {
        return ehcache;
    }

    /**
     * @param ehcache the ehcache to set
     */
    public void setEhcache(final Ehcache ehcache) {
        this.ehcache = ehcache;
    }

    /**
     * @return the cacheManager
     */
    private CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * @param cacheManager the cacheManager to set
     */
    private void setCacheManager(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

}
