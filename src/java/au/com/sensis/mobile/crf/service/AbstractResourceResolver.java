package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;

/**
 * Standard base class for {@link ResourceResolver}s implementing the template
 * method pattern.
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

    /**
     * Constructor.
     *
     * @param abstractResourceExtension
     *            Extension of resourcs (eg. "css" or "crf") that this class
     *            knows how to map.
     * @param rootResourcesDir
     *            Root directory where the real resources that this mapper
     *            handles are stored.
     * @param resourceResolutionWarnLogger
     *            {@link ResourceResolutionWarnLogger}.
     */
    public AbstractResourceResolver(final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger) {
        validateAbstractResourceExtension(abstractResourceExtension);
        validateRootResourcesDir(rootResourcesDir);
        validateResourceResolutionWarnLogger(resourceResolutionWarnLogger);

        this.abstractResourceExtension =
                prefixWithLeadingDotIfRequired(abstractResourceExtension);
        this.rootResourcesDir = rootResourcesDir;
        this.resourceResolutionWarnLogger = resourceResolutionWarnLogger;
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

    private String prefixWithLeadingDotIfRequired(final String path) {
        if (path.startsWith(EXTENSION_LEADING_DOT)) {
            return path;
        } else {
            return EXTENSION_LEADING_DOT + path;
        }
    }

    /**
     * Template method for mapping requested resource paths to real resource
     * paths.
     *
     * {@inheritDoc}
     *
     * @throws IOException
     *             Thrown if an IO error occurs.
     */
    @Override
    public List<Resource> resolve(final String requestedResourcePath,
            final Group group) throws IOException {
        if (isRecognisedAbstractResourceRequest(requestedResourcePath)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "Attempting to resolve requested resource '"
                                + requestedResourcePath + "'");
            }

            final List<Resource> resolvedResources =
                    doResolve(requestedResourcePath, group);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "Resolved requested resource '" + requestedResourcePath
                                + "' to '" + resolvedResources + "'");
            }
            return resolvedResources;
        } else {

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                        "Requested resource '" + requestedResourcePath
                                + "' is not for a "
                                + getDebugResourceTypeName()
                                + " file. Ignoring the request.");
            }

            return new ArrayList<Resource>();
        }
    }

    /**
     * Workhorse method that is only called if
     * {@link #isRecognisedAbstractResourceRequest(String)} is true. Resolves
     * the requested resource path to one or more {@link Resource}s if they
     * exist. Returns an empty list if none are found. The default
     * implementation simply creates a new {@link Resource} from the given
     * parameters if newResourcePath exists.
     *
     * @param requestedResourcePath
     *            The original resource that was requested.
     * @param group {@link Group} for the request.
     * @return {@link List} of {@link Resource}s that exist.
     * @throws IOException
     *             Thrown if any IO error occurs.
     */
    protected List<Resource> doResolve(final String requestedResourcePath,
            final Group group) throws IOException {
        final String newResourcePath =
                createNewResourcePath(requestedResourcePath, group);

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

    private  Resource createResource(
            final String requestedResourcePath, final String newPath) {
        return new ResourceBean(requestedResourcePath,
                newPath, getRootResourcesDir());
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
     * {@link #insertGroupNameIntoPath(String, Group)} the replaces the
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

        return replaceAbstractResourceExtensionWithReal(insertGroupNameIntoPath(
                requestedResourcePath, group));
    }

    private String replaceAbstractResourceExtensionWithReal(
            final String resourcePath) {
        return StringUtils.removeEnd(resourcePath,
                getAbstractResourceExtension())
                + getRealResourcePathExtension();
    }

    /**
     * @return the abstractResourceExtension
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
     * Insert the name of the given {@link Group} into
     * the requested resource path and return the result. The default
     * implementation simply inserts the group name at the start of the path,
     * separated from the rest of the path by {@link #RESOURCE_SEPARATOR}.
     *
     * @param requestedResourcePath
     *            The path of the resource requested.
     * @param group
     *            {@link Group} that the
     *            {@link au.com.sensis.wireless.common.volantis.devicerepository.api.Device}
     *            for the current request belongs to.
     * @return the result of inserting the the name of the given {@link Group}
     *         into the requested resource path.
     */
    protected String insertGroupNameIntoPath(
            final String requestedResourcePath, final Group group) {
        return group.getName() + RESOURCE_SEPARATOR + requestedResourcePath;
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
     * @return the rootResourcesDir
     */
    protected File getRootResourcesDir() {
        return rootResourcesDir;
    }

    /**
     * @return the {@link ResourceResolutionWarnLogger}.
     */
    protected final ResourceResolutionWarnLogger getResourceResolutionWarnLogger() {
        return resourceResolutionWarnLogger;
    }

}
