package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.mobile.crf.util.FileIoFacadeFactory;

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

    /**
     * Constructor.
     *
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param resourceResolutionWarnLogger
     *            {@link ResourceResolutionWarnLogger} to use to log warnings.
     * @param deploymentMetadata {@link DeploymentMetadata} of the deployed app.
     */
    public AbstractResourceResolver(final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final DeploymentMetadata deploymentMetadata) {
        validateAbstractResourceExtension(abstractResourceExtension);
        validateRootResourcesDir(rootResourcesDir);
        validateResourceResolutionWarnLogger(resourceResolutionWarnLogger);
        validateDeploymentMetadata(deploymentMetadata);

        this.abstractResourceExtension =
            prefixWithLeadingDotIfRequired(abstractResourceExtension);
        this.rootResourcesDir = rootResourcesDir;
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
        this.deploymentMetadata = deploymentMetadata;
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

    private void validateResourceResolutionWarnLogger(
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        Validate.notNull(resourceResolutionWarnLogger,
        "resourceResolutionWarnLogger must not be null");
    }

    private void validateDeploymentMetadata(final DeploymentMetadata deploymentMetadata) {
        Validate.notNull(deploymentMetadata, "deploymentMetadata must not be null");
    }

    private String prefixWithLeadingDotIfRequired(final String path) {
        if (path.startsWith(EXTENSION_LEADING_DOT)) {
            return path;
        } else {
            return EXTENSION_LEADING_DOT + path;
        }
    }

    /**
     * Template method for resolving requested resource paths to {@link Resource}s.
     *
     * {@inheritDoc}
     */
    @Override
    public List<Resource> resolve(final String requestedResourcePath,
            final Group group, final ResourceAccumulator results) throws ResourceResolutionRuntimeException {

        if (isRecognisedAbstractResourceRequest(requestedResourcePath)) {

            debugLogAttemptingResolution(requestedResourcePath);

            // question: what's the key in the hashmap?
            // - concat'd string of path and group?
            // - hashcode of path n group?


            // check ConcurrentHashMap to see if it already contains resolved resources
            // for the given requestedResourcePath and group

            // if so, return it

            // if not,
            final List<Resource> resolvedResources =
                doResolve(requestedResourcePath, group);

            accumulateGroupResources(resolvedResources, results);

            debugLogResolutionResults(requestedResourcePath, resolvedResources);

            return resolvedResources;
        } else {

            debugLogRequestIgnored(requestedResourcePath);

            return new ArrayList<Resource>();
        }
    }

    protected void accumulateGroupResources(
            final List<Resource> resolvedPaths,
            final ResourceAccumulator allResourcePaths) {

        if (!resolvedPaths.isEmpty() &&
                ((allResourcePaths != null) &&
                        (allResourcePaths.getAllResourcePaths() != null))) {

            Collections.reverse(resolvedPaths);

            for (final Resource currPath : resolvedPaths) {
                allResourcePaths.getAllResourcePaths().push(currPath);
            }
        }
    }

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
    protected List<Resource> doResolve(final String requestedResourcePath,
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
     * @return the {@link ResourceResolutionWarnLogger} to use to log warnings.
     */
    protected final ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
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

    private DeploymentMetadata getDeploymentMetadata() {
        return deploymentMetadata;
    }
}
