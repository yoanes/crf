package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.ConfigurationFactory;
import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * Standard base class for {@link ResourceResolver}s.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractResourceResolver implements ResourceResolver {

    private static final String EXTENSION_LEADING_DOT = ".";

    /**
     * Separator character for resource paths.
     */
    protected static final String RESOURCE_SEPARATOR = "/";

    private final String abstractResourceExtension;
    private final File rootResourcesDir;
    private final ResourceResolutionWarnLogger resourceResolutionWarnLogger;
    private final DeploymentMetadata deploymentMetadata;
    private final ResourceAccumulatorFactory resourceAccumulatorFactory;
    private final ConfigurationFactory configurationFactory;

    /**
     * Constructor.
     *
     * @param commonParams
     *            Holds the common parameters used in constructing all {@link ResourceResolver}s.
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.

     */
    public AbstractResourceResolver(final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension,
            final File rootResourcesDir) {

        validateAbstractResourceExtension(abstractResourceExtension);
        validateRootResourcesDir(rootResourcesDir);

        resourceResolutionWarnLogger = commonParams.getResourceResolutionWarnLogger();
        deploymentMetadata = commonParams.getDeploymentMetadata();
        resourceAccumulatorFactory = commonParams.getResourceAccumulatorFactory();
        configurationFactory = commonParams.getConfigurationFactory();

        this.abstractResourceExtension =
            prefixWithLeadingDotIfRequired(abstractResourceExtension);
        this.rootResourcesDir = rootResourcesDir;

    }

    /**
     * Template method for resolving requested resource paths to {@link Resource}s.
     *
     * {@inheritDoc}
     */
    @Override
    public List<Resource> resolve(final String requestedResourcePath, final Device device)
    throws ResourceResolutionRuntimeException {

        if (isRecognisedAbstractResourceRequest(requestedResourcePath)) {

            final ResourceAccumulator accumulator = createResourceAccumulator();

            // TODO in future will check cache
            final Iterator<Group> matchingGroupIterator =
                getMatchingGroupIterator(device, requestedResourcePath);

            while (matchingGroupIterator.hasNext()) {

                final Group currGroup = matchingGroupIterator.next();

                getLogger().debug("Group: " + currGroup.getName());

                debugLogCheckingGroup(requestedResourcePath, currGroup);

                accumulator.accumulate(
                        resolveForGroup(requestedResourcePath, currGroup));

            }

            return accumulator.getResources();

        } else {

            debugLogRequestIgnored(requestedResourcePath);

            return new ArrayList<Resource>();
        }
    }

    /**
     * Returns the {@link Resource}s that are resolved for the given {@link Group}.
     *
     * @param requestedResourcePath for the resource to be resolved
     * @param group in which to look for the given requestedResourcePath
     * @return a list of {@link Resource}s resolved for the given {@link Group}
     */
    protected List<Resource> resolveForGroup(final String requestedResourcePath,
            final Group group) {

        debugLogAttemptingResolution(requestedResourcePath);

        final List<Resource> resolvedResources =
            doResolveForGroup(requestedResourcePath, group);

        debugLogResolutionResults(requestedResourcePath, resolvedResources);

        return resolvedResources;
    }

    /**
     * @return Returns a new {@link ResourceAccumulator} for this {@link ResourceResolver}.
     */
    protected abstract ResourceAccumulator createResourceAccumulator();

    /**
     * Workhorse method that is only called if
     * {@link #isRecognisedAbstractResourceRequest(String)} is true. Resolves
     * the requested resource path to one or more {@link Resource}s if they
     * exist. Returns an empty list if none are found. The default
     * implementation simply creates a new {@link Resource} from the given
     * parameters if the path created by
     * {@link #createNewResourcePath(String, Group)} exists.
     *
     * @param requestedResourcePath
     *            The original resource that was requested.
     * @param group
     *            {@link Group} that the
     *            {@link au.com.sensis.wireless.common.volantis.devicerepository.api.Device}
     *            for the current request belongs to.
     *
     * @return {@link List} of {@link Resource}s that exist.
     * @throws ResourceResolutionRuntimeException
     *             Thrown if any error occurs.
     */
    protected List<Resource> doResolveForGroup(final String requestedResourcePath,
            final Group group)
            throws ResourceResolutionRuntimeException {

        final String newResourcePath =
            createNewResourcePath(requestedResourcePath, group);

        debugLogCheckingIfPathExists(newResourcePath);

        if (exists(newResourcePath)) {
            return Arrays.asList(createResource(requestedResourcePath,
                    newResourcePath));
        } else {
            return new ArrayList<Resource>();
        }
    }

    /**
     * @param newResourcePath Path to test.
     * @return true if the given new path exists in
     *         {@link #getRootResourceDir()}.
     */
    protected final boolean exists(final String newResourcePath) {
        // TODO: possibly cache the result since we are accessing the file
        // system?
        return FileIoFacadeFactory.getFileIoFacadeSingleton().fileExists(
                getRootResourcesDir(), newResourcePath);
    }

    /**
     * Create a {@link Resource} from the requested path and the new path that
     * it maps to.
     *
     * @param requestedResourcePath
     *            Requested path.
     * @param newPath
     *            New path that the requested path maps to.
     * @return new {@link Resource} created from the requested path and the new
     *         path that it maps to.
     */
    protected final Resource createResource(final String requestedResourcePath,
            final String newPath) {
        return new ResourceBean(requestedResourcePath, newPath,
                getRootResourcesDir());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final String requestedResourcePath) {
        return isRecognisedAbstractResourceRequest(requestedResourcePath);
    }

    /**
     * Returns true if the requested resource path is
     * for a recognised abstract resource that this {@link ResourceResolver}
     * can handle. The default implementation returns true if the requested
     * resource path ends with {@link #getAbstractResourceExtension()}.
     *
     * @param requestedResourcePath
     *            The path of the resource requested.
     * @return true if the requested resource path is for a recognised abstract
     *         resource that this {@link ResourceResolver} can handle.
     */
    protected boolean isRecognisedAbstractResourceRequest(
            final String requestedResourcePath) {
        return requestedResourcePath.endsWith(getAbstractResourceExtension());
    }

    /**
     * The actual algorithm for mapping the requested resource path to the
     * candidate real resource path. Will only ever be called if
     * {@link #isRecognisedAbstractResourceRequest(String)} returns true. The
     * default implementation invokes
     * {@link #insertGroupNameAndDeploymentVersionIntoPath(String, Group)} then replaces the
     * {@link #getAbstractResourceExtension()} with
     * {@link #getRealResourcePathExtension()}.
     *
     * @param requestedResourcePath
     *            The path of the resource requested.
     * @param group
     *            {@link Group} that the
     *            {@link au.com.sensis.wireless.common.volantis.devicerepository.api.Device}
     *            for the current request belongs to.
     * @return The candidate real resource path that the requested resource path
     *         maps to. May not be null.
     */
    protected String createNewResourcePath(final String requestedResourcePath,
            final Group group) {

        return replaceAbstractResourceExtensionWithReal(insertGroupNameAndDeploymentVersionIntoPath(
                requestedResourcePath, group));
    }

    private String replaceAbstractResourceExtensionWithReal(
            final String resourcePath) {
        return StringUtils.removeEnd(resourcePath,
                getAbstractResourceExtension())
                + getRealResourcePathExtension();
    }

    /**
     * @return the extension of resources (eg. "css" or "crf") that this class
     *         knows how to resolve.
     */
    protected final String getAbstractResourceExtension() {
        return abstractResourceExtension;
    }

    /**
     * Returns the file extension to use for the generated,
     * real resource paths.
     *
     * @return the file extension to use for the generated, real resource paths.
     */
    protected abstract String getRealResourcePathExtension();

    /**
     * Insert the name of the given {@link Group}, plus the
     * {@link #getDeploymentMetadata()} version into the requested resource path
     * and return the result. The default implementation simply inserts the
     * version and group name at the start of the path, each component separated
     * from the rest of the path by {@link #RESOURCE_SEPARATOR}.
     *
     * @param requestedResourcePath
     *            The path of the resource requested.
     * @param group
     *            {@link Group} that the
     *            {@link au.com.sensis.wireless.common.volantis.devicerepository.api.Device}
     *            for the current request belongs to.
     * @return the result of inserting the the name of the given {@link Group},
     *         plus the {@link #getDeploymentMetadata()} version into the
     *         requested resource path.
     */
    protected String insertGroupNameAndDeploymentVersionIntoPath(
            final String requestedResourcePath, final Group group) {
        return getDeploymentMetadata().getVersion() + RESOURCE_SEPARATOR + group.getName()
        + RESOURCE_SEPARATOR + requestedResourcePath;
    }

    /**
     * Debug friendly name of the type of resource that this
     * {@link ResourceResolver} handles.
     *
     * @return Debug friendly name of the type of resource that this
     *         {@link ResourceResolver} handles.
     */
    protected abstract String getDebugResourceTypeName();

    /**
     * The {@link Logger} to use for this
     * {@link ResourceResolver}. Allows the
     * {@link #resolve(String, Group)} to log messages that clearly
     * indicate what the actual {@link ResourceResolver} implementation being
     * executed is.
     *
     * @return The {@link Logger} to use for this {@link ResourceResolver}.
     *         Allows the {@link #resolve(String, Group)} to log
     *         messages that clearly indicate what the actual
     *         {@link ResourceResolver} implementation being executed is.
     */
    protected abstract Logger getLogger();

    /**
     * @return the rootResourcesDir Root directory where the real resources that
     *         this resolver handles are stored.
     */
    protected File getRootResourcesDir() {
        return rootResourcesDir;
    }

    /**
     * @return the {@link ResourceAccumulatorFactory} from which to obtain a
     *         {@link ResourceAccumulator} implementation.
     */
    protected ResourceAccumulatorFactory getResourceAccumulatorFactory() {
        return resourceAccumulatorFactory;
    }

    /**
     * @return the {@link ConfigurationFactory} from which to obtain the
     * {@link au.com.sensis.mobile.crf.config.UiConfiguration}.
     */
    protected ConfigurationFactory getConfigurationFactory() {
        return configurationFactory;
    }

    /**
     * @return the {@link ResourceResolutionWarnLogger} to use to log warnings.
     */
    protected final ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
    }

    private void validateAbstractResourceExtension(
            final String abstractResourceExtension) {
        if (StringUtils.isBlank(abstractResourceExtension)) {
            throw new IllegalArgumentException(
                    "abstractResourceExtension must not be blank: '"
                    + abstractResourceExtension + "'");
        }
    }

    private void validateRootResourcesDir(final File resourcesRootDir) {
        if (!resourcesRootDir.exists() || !resourcesRootDir.isDirectory()) {
            throw new IllegalArgumentException(
                    "rootResourcesDir must be a directory: '"
                    + resourcesRootDir + "'");
        }
    }

    private String prefixWithLeadingDotIfRequired(final String path) {
        if (path.startsWith(EXTENSION_LEADING_DOT)) {
            return path;
        } else {
            return EXTENSION_LEADING_DOT + path;
        }
    }

    private Iterator<Group> getMatchingGroupIterator(final Device device,
            final String requestedResourcePath) {

        return getConfigurationFactory().getUiConfiguration(requestedResourcePath)
        .matchingGroupIterator(device);
    }

    private void debugLogAttemptingResolution(final String requestedResourcePath) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    "Attempting to resolve requested resource '"
                    + requestedResourcePath + "'");
        }
    }

    private void debugLogCheckingIfPathExists(final String newResourcePath) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Checking if resource exists: '" + newResourcePath + "'");
        }
    }

    private void debugLogResolutionResults(final String requestedResourcePath,
            final List<Resource> resolvedResources) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    "Resolved requested resource '" + requestedResourcePath
                    + "' to '" + resolvedResources + "'");
        }
    }

    private void debugLogRequestIgnored(final String requestedResourcePath) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    "Requested resource '" + requestedResourcePath
                    + "' is not for a "
                    + getDebugResourceTypeName()
                    + " file. Ignoring the request.");
        }
    }

    private void debugLogCheckingGroup(final String requestedResourcePath,
            final Group currGroup) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Looking for '" + requestedResourcePath
                    + "' in matching group: " + currGroup);
        }
    }

    private DeploymentMetadata getDeploymentMetadata() {
        return deploymentMetadata;
    }


}
