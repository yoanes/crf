package au.com.sensis.mobile.crf.debug;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.service.Resource;
import au.com.sensis.mobile.crf.service.ResourceBean;
import au.com.sensis.mobile.crf.service.ResourceCache;
import au.com.sensis.mobile.crf.service.ResourceCacheEntryBean;
import au.com.sensis.mobile.crf.service.ResourceCacheKeyBean;

/**
 * MBean for filling a {@link ResourceCache} with an arbitrary number of elements.
 * Useful during development to analyse memory usage.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
@ManagedResource(value = "contentRenderingFramework.debug:name=crf.resourceCacheFiller")
public class ResourceCacheFillerBean {

    private static final Logger LOGGER = Logger.getLogger(ResourceCacheFillerBean.class);

    /**
     * Base String to use for generating abstract requested paths.
     */
    public static final String BASE_ABSTRACT_REQUESTED_PATH =
        "abstract path which is 100 characters long abstract path which is 100 "
        + "characters long abstract path ";

    /**
     * Base String to use for generating group names.
     */
    public static final String BASE_GROUP_NAME = "someGroup";

    /**
     * Base String to use for generating group expressions.
     */
    public static final String BASE_EXPR = "this is the expr for my dummy group. "
        + "The length doesn't really matter. ";

    private final ResourceCache groupsCache;

    private File rootResourcesDir;
    private boolean enabled;

    /**
     * Constructor.
     *
     * @param resoucreCache
     *            {@link ResourceCache} to fill.
     */
    public ResourceCacheFillerBean(final ResourceCache resoucreCache) {
        groupsCache = resoucreCache;

        try {
            setRootResourcesDir(new File(getClass().getResource("/").toURI()));
        } catch (final URISyntaxException e) {
            throw new RuntimeException("Ooooops !!! This ResourceCacheFillerBean is buggy !!!");
        }
    }

    /**
     * @param numAbstractPaths
     *            Number of abstract paths to fill the cache with.
     * @param groupsPerPath
     *            Groups matched per path.
     * @param numResourcesPerPath
     *            Number of concrete {@link Resource}s to create per abstract
     *            path.
     */
    @ManagedOperation(description = "Fill cache")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "numAbstractPaths", description = "numAbstractPaths"),
            @ManagedOperationParameter(name = "groupsPerPath", description = "groupsPerPath"),
            @ManagedOperationParameter(name = "numResourcesPerPath",
                    description = "numResourcesPerPath") })
    public void fillCache(final int numAbstractPaths, final int groupsPerPath,
            final int numResourcesPerPath) {
        if (isEnabled()) {
            doFillCache(numAbstractPaths, groupsPerPath, numResourcesPerPath);
        } else {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Bean is disabled. Will not fill cache.");
            }

        }

    }

    private void doFillCache(final int numAbstractPaths, final int groupsPerPath,
            final int numResourcesPerPath) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("fillCache start. numAbstractPaths=" + numAbstractPaths
                    + "; numResourcesPerPath=" + numResourcesPerPath);
        }

        final Group[] groups = createGroups(groupsPerPath);

        doFillCache(numAbstractPaths, numResourcesPerPath, groups);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("fillCache end");
        }
    }

    private Group[] createGroups(final int groupsPerUserAgent) {
        final List<Group> groups = new ArrayList<Group>();

        for (int i = 0; i < groupsPerUserAgent; i++) {
            final Group group = new Group();
            group.setName(BASE_GROUP_NAME + i);
            group.setExpr(BASE_EXPR + i);
            groups.add(group);
        }

        return groups.toArray(new Group [] {});
    }

    private void doFillCache(final int numAbstractPaths, final int numResourcesPerPath,
            final Group [] groups) {
        for (int i = 0; i < numAbstractPaths; i++) {
            final String abstractRequestedPath = BASE_ABSTRACT_REQUESTED_PATH + i;

            for (int j = 0; j < groups.length; j++) {
                doFillCache(numResourcesPerPath, abstractRequestedPath, groups[j]);
            }

            infoLogProgress(i);

        }
    }

    private void doFillCache(final int numResourcesPerPath, final String abstractRequestedPath,
            final Group currGroup) {
        final ResourceCacheKeyBean groupsCacheKeyBean =
                new ResourceCacheKeyBean(abstractRequestedPath,
                        new Group [] { currGroup });
        final Resource[] resources =
                createResources(abstractRequestedPath, currGroup, numResourcesPerPath);
        getResourceCache().put(groupsCacheKeyBean,
            new ResourceCacheEntryBean(resources,
                ResourceCache.DEFAULT_RESOUCRES_NOT_FOUND_MAX_REFRESH_COUNT,
                ResourceCache.DEFAULT_RESOURCES_NOT_FOUND_REFRESH_COUNT_UPDATE_MILLISECONDS));
    }

    private void infoLogProgress(final int abstractPathIndex) {
        final int progressLoggingMarkerIndex = 100;
        if ((abstractPathIndex % progressLoggingMarkerIndex == 0) && LOGGER.isInfoEnabled()) {
            LOGGER.info("fillCache ... abstract path index=" + abstractPathIndex);
        }
    }

    private Resource[] createResources(final String abstractRequestedPath, final Group currGroup,
            final int numResourcesPerPath) {
        final List<Resource> resources = new ArrayList<Resource>();
        for (int i = 0; i < numResourcesPerPath; i++) {
            final Resource resource =
                    new ResourceBean(abstractRequestedPath, abstractRequestedPath + "/"
                            + currGroup.getName() + "/" + i, getRootResourcesDir(), currGroup);
            resources.add(resource);
        }
        return resources.toArray(new Resource[] {});
    }

    /**
     * Clear the cache.
     */
    @ManagedOperation(description = "Clear cache")
    public void clearCache() {
        if (isEnabled()) {
            doClearCache();
        } else {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Bean is disabled. Will not clear cache.");
            }
        }
    }

    private void doClearCache() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("clearCache start");
        }

        getResourceCache().removeAll();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("clearCache ends");
        }
    }

    /**
     * @return the groupsCache
     */
    private ResourceCache getResourceCache() {
        return groupsCache;
    }

    /**
     * @return the rootResourcesDir
     */
    private File getRootResourcesDir() {
        return rootResourcesDir;
    }

    /**
     * @param rootResourcesDir the rootResourcesDir to set
     */
    private void setRootResourcesDir(final File rootResourcesDir) {
        this.rootResourcesDir = rootResourcesDir;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

}
