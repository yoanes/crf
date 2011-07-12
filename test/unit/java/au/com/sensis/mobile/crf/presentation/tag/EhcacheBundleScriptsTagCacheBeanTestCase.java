package au.com.sensis.mobile.crf.presentation.tag;

import net.sf.ehcache.Ehcache;

import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link EhcacheBundleScriptsTagCacheBean}.
 *
 * @author w12495
 */
public class EhcacheBundleScriptsTagCacheBeanTestCase extends AbstractJUnit4TestCase {

    private Ehcache mockEhcache;

    /**
     * Very trivial "test" just to get code coverage.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Test
    public void testConstructor() throws Exception {
        new EhcacheBundleScriptsTagCacheBean(getMockEhcache(), true);
    }

    /**
     * @return the mockEhcache
     */
    public Ehcache getMockEhcache() {
        return mockEhcache;
    }

    /**
     * @param mockEhcache the mockEhcache to set
     */
    public void setMockEhcache(final Ehcache mockEhcache) {
        this.mockEhcache = mockEhcache;
    }
}
