package au.com.sensis.mobile.crf.debug;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.config.GroupsCache;
import au.com.sensis.mobile.crf.config.GroupsCacheKeyBean;

/**
 * MBean for filling a {@link GroupsCache} with an arbitrary number of elements.
 * Useful during development to analyse memory usage.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
@ManagedResource(value = "contentRenderingFramework.debug:name=crf.groupsCacheFiller")
public class GroupsCacheFillerBean {

    private static final Logger LOGGER = Logger.getLogger(GroupsCacheFillerBean.class);

    /**
     * Base String to use for generating user agents.
     */
    public static final String BASE_USER_AGENT =
        "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_1_3 like Mac OS X; en-us) "
        + "AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7E18 Safari/528.16 "
        + "some extra stuff at the end to make this longer than we will probably see "
        + "some extra stuff at the end to make this longer than we will probably see "
        + "some extra stuff at the end to make this longer than we will probably see "
        + "some extra stuff at the end to make this longer than we will probably see";

    /**
     * Base String to use for generating UI cnofiguration paths.
     */
    public  static final String UI_CONFIGURATION_PATH =
        "length of this path doesn't really matter";

    /**
     * Base String to use for generating group names.
     */
    public static final String BASE_GROUP_NAME = "someGroup";

    /**
     * Base String to use for generating group expressions.
     */
    public static final String BASE_EXPR = "this is the expr for my dummy group. "
        + "The length doesn't really matter. ";

    private final GroupsCache groupsCache;

    private boolean enabled;

    /**
     * Constructor.
     *
     * @param groupsCache
     *            {@link GroupsCache} to fill.
     */
    public GroupsCacheFillerBean(final GroupsCache groupsCache) {
        this.groupsCache = groupsCache;
    }

    /**
     * @param numUserAgents
     *            Number of elements to fill the cache with.
     * @param numUiConfigurations Number of UiConfigurations to simiulate.
     * @param groupsPerUserAgent
     *            Number fo groups that each user agent should map to.
     */
    @ManagedOperation(description = "Fill cache")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "numUserAgents", description = "numUserAgents"),
            @ManagedOperationParameter(name = "numUiConfigurations",
                    description = "numUiConfigurations"),
            @ManagedOperationParameter(name = "groupsPerUserAgent",
                    description = "groupsPerUserAgent") })
    public void fillCache(final int numUserAgents, final int numUiConfigurations,
            final int groupsPerUserAgent) {
        if (isEnabled()) {
            doFillCache(numUserAgents, numUiConfigurations, groupsPerUserAgent);
        } else {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Bean is disabled. Will not fill cache.");
            }
        }
    }

    private void doFillCache(final int numUserAgents, final int numUiConfigurations,
            final int groupsPerUserAgent) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("fillCache start. numElementsToAddToCache=" + numUserAgents
                    + "; groupsPerUserAgent=" + groupsPerUserAgent);
        }

        final Group[] groups = createGroups(groupsPerUserAgent);
        final String[] uiConfigurationPaths = creatUiConfigurationPaths(numUiConfigurations);
        doFillCache(numUserAgents, uiConfigurationPaths, groups);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("fillCache end");
        }
    }

    private String[] creatUiConfigurationPaths(final int numUiConfigurations) {
        final List<String> uiConfigPaths = new ArrayList<String>();
        for (int i = 0; i < numUiConfigurations; i++) {
            uiConfigPaths.add(UI_CONFIGURATION_PATH + i);
        }
        return uiConfigPaths.toArray(new String [] {});
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

    private void doFillCache(final int numUserAgents, final String[] uiConfigurationPaths,
            final Group[] sourceGroups) {

        for (int i = 0; i < uiConfigurationPaths.length; i++) {
            for (int j = 0; j < numUserAgents; j++) {
                final String userAgent = BASE_USER_AGENT + j;
                final GroupsCacheKeyBean groupsCacheKeyBean =
                        new GroupsCacheKeyBean(userAgent, uiConfigurationPaths[i]);

                // Must create a new array each time.
                final List<Group> groupsToCache = createNewArrayOf(sourceGroups);

                infoLogProgress(uiConfigurationPaths, i, j);

                getGroupsCache().put(groupsCacheKeyBean, groupsToCache.toArray(new Group[] {}));
            }
        }
    }

    private void infoLogProgress(final String[] uiConfigurationPaths, final int uiConfigPathIndex,
            final int userAgentIndex) {
        final int progressLoggingMarkerIndex = 100;
        if ((userAgentIndex % progressLoggingMarkerIndex == 0) && LOGGER.isInfoEnabled()) {
            LOGGER.info("fillCache ... uiConfigPath=" + uiConfigurationPaths[uiConfigPathIndex]
                    + "userAgent " + userAgentIndex);
        }
    }

    private List<Group> createNewArrayOf(final Group[] sourceGroups) {
        final List<Group> groupsToCache = new ArrayList<Group>();
        for (int k = 0; k < sourceGroups.length; k++) {
            groupsToCache.add(sourceGroups[k]);
        }
        return groupsToCache;
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

        getGroupsCache().removeAll();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("clearCache end");
        }
    }

    /**
     * @return the groupsCache
     */
    private GroupsCache getGroupsCache() {
        return groupsCache;
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
