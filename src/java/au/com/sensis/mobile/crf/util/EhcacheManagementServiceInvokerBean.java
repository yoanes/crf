package au.com.sensis.mobile.crf.util;

import javax.management.MBeanServer;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import org.springframework.beans.factory.InitializingBean;

/**
 * Invokes
 * {@link ManagementService#registerMBeans(net.sf.ehcache.CacheManager,
 * javax.management.MBeanServer, boolean, boolean, boolean, boolean)}
 * after construction. This bean is intended to hook into Spring, hence the
 * implementation of {@link InitializingBean}. If you're not using Spring,
 * you're likely better off to simply invoke the {@link ManagementService}
 * directly.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class EhcacheManagementServiceInvokerBean implements InitializingBean {

    private final MBeanServer mbeanServer;
    private final CacheManager cacheManager;

    private boolean registerCacheManager;
    private boolean registerCaches;
    private boolean registerCacheConfigurations;
    private boolean registerCacheStatistics;

    /**
     * Constructor.
     *
     * @param mbeanServer {@link MBeanServer} to register the ehcache management beans with.
     * @param cacheManager {@link CacheManager} to register MBeans for.
     */
    public EhcacheManagementServiceInvokerBean(final MBeanServer mbeanServer,
            final CacheManager cacheManager) {
        this.mbeanServer = mbeanServer;
        this.cacheManager = cacheManager;
    }

    /**
     * @param registerCacheManager
     *            the registerCacheManager to set
     */
    public void setRegisterCacheManager(final boolean registerCacheManager) {
        this.registerCacheManager = registerCacheManager;
    }

    /**
     * @param registerCaches
     *            the registerCaches to set
     */
    public void setRegisterCaches(final boolean registerCaches) {
        this.registerCaches = registerCaches;
    }

    /**
     * @param registerCacheConfigurations
     *            the registerCacheConfigurations to set
     */
    public void setRegisterCacheConfigurations(final boolean registerCacheConfigurations) {
        this.registerCacheConfigurations = registerCacheConfigurations;
    }

    /**
     * @param registerCacheStatistics
     *            the registerCacheStatistics to set
     */
    public void setRegisterCacheStatistics(final boolean registerCacheStatistics) {
        this.registerCacheStatistics = registerCacheStatistics;
    }

    /**
     * Registers the ehcache MBeans.
     *
     * @throws Exception Thrown if any errors occur.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ManagementService.registerMBeans(getCacheManager(), getMbeanServer(),
                isRegisterCacheManager(), isRegisterCaches(), isRegisterCacheConfigurations(),
                isRegisterCacheStatistics());
    }

    /**
     * @return the mbeanServer
     */
    private MBeanServer getMbeanServer() {
        return mbeanServer;
    }

    /**
     * @return the cacheManager
     */
    private CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * @return the registerCacheManager
     */
    private boolean isRegisterCacheManager() {
        return registerCacheManager;
    }

    /**
     * @return the registerCaches
     */
    private boolean isRegisterCaches() {
        return registerCaches;
    }

    /**
     * @return the registerCacheConfigurations
     */
    private boolean isRegisterCacheConfigurations() {
        return registerCacheConfigurations;
    }

    /**
     * @return the registerCacheStatistics
     */
    private boolean isRegisterCacheStatistics() {
        return registerCacheStatistics;
    }
}
