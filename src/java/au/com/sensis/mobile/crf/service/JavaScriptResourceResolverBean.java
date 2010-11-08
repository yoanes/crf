package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.DeploymentMetadata;
import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;

/**
 * {@link ResourceResolver} that maps abstract Script paths to real Script paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptResourceResolverBean extends AbstractResourceResolver {

    private static final Logger LOGGER = Logger.getLogger(JavaScriptResourceResolverBean.class);

    /**
     * Separator character expected to be used in {@link #getOriginalPath()}
     * and {@link #getNewFile()}.
     */
    protected static final String SEPARATOR = "/";

    private final String abstractPathPackageKeyword;
    private final JavaScriptFileFinder javaScriptFileFinder;

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
     * @param abstractPathPackageKeyword
     *            Keyword recognised at the end of abstract paths that signifies
     *            a "package" of JavaScript is being requested.
     * @param javaScriptFileFinder
     *            {@link JavaScriptFileFinder} delegate to use to find a list of
     *            JavaScript files in a directory.
     */
    public JavaScriptResourceResolverBean(
            final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceResolutionWarnLogger resourceResolutionWarnLogger,
            final DeploymentMetadata deploymentMetadata,
            final String abstractPathPackageKeyword,
            final JavaScriptFileFinder javaScriptFileFinder) {
        super(abstractResourceExtension, rootResourcesDir,
                resourceResolutionWarnLogger, deploymentMetadata);

        validateAbstractPathPackageKeyword(abstractPathPackageKeyword);
        validateJavaScriptFileFinder(javaScriptFileFinder);

        this.abstractPathPackageKeyword = abstractPathPackageKeyword;
        this.javaScriptFileFinder = javaScriptFileFinder;
    }


    private void validateAbstractPathPackageKeyword(
            final String abstractPathPackageKeyword) {
        if (StringUtils.isBlank(abstractPathPackageKeyword)) {
            throw new IllegalArgumentException(
                    "abstractPathPackageKeyword must not be blank: '"
                    + abstractPathPackageKeyword + "'");
        }
    }

    private void validateJavaScriptFileFinder(
            final JavaScriptFileFinder javaScriptFileFinder) {
        Validate.notNull(javaScriptFileFinder,
        "javaScriptFileFinder must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isRecognisedAbstractResourceRequest(
            final String requestedResourcePath) {
        return super.isRecognisedAbstractResourceRequest(requestedResourcePath)
        || isPackageRequested(requestedResourcePath);
    }

    /**
     * Performs resolution in both cases where
     * {@link #isPackageRequested(String)} is true and when it is not.
     *
     * {@inheritDoc}
     */
    @Override
    protected List<Resource> doResolve(final String requestedResourcePath,
            final Group group)
            throws ResourceResolutionRuntimeException {

        if (isPackageRequested(requestedResourcePath)) {
            return findBundleResources(requestedResourcePath, group);
        } else {
            return super.doResolve(requestedResourcePath, group);
        }
    }

    /**
     * Accumulates unique resources, overriding duplicate matches with the most specific version,
     * whilst preserving specifically set file order for packages
     * (set using the package.properties).
     *
     * {@inheritDoc}
     */
    @Override
    protected void accumulateGroupResources(
            final List<Resource> resolvedPaths,
            final ResourceAccumulator allResourcePaths) {

        if (!resolvedPaths.isEmpty()) {

            // We reverse the files so that when they're output in the HTML page
            // they're still in the original order.
            Collections.reverse(resolvedPaths);

            // for each found resource decide if we want to add it to the final list
            for (final Resource newResource : resolvedPaths) {

                LOGGER.debug("looking at: " + newResource.getNewPath());

                if (!hasAVariantAlreadyBeenStored(newResource,
                        allResourcePaths.getAllResourcePaths())) {

                    LOGGER.debug("Adding " + newResource.getNewPath() + " to the list.");
                    allResourcePaths.getAllResourcePaths().push(newResource);
                }
            }
        }
    }

    private boolean isPartOfAPackage(final Resource requestedResource) {
        return requestedResource.getOriginalPath().endsWith(getAbstractPathPackageKeyword());
    }

    private boolean hasAVariantAlreadyBeenStored(final Resource newResource,
            final Deque<Resource> accumulatedResources) {

        boolean alreadyStoredAVariant = false;

        // loop through the final list to see if our new ones are already there
        for (final Resource existingResource : accumulatedResources) {

            LOGGER.debug("Comparing with: " + existingResource.getNewPath());

            if (hasPackageResourceBeenStored(newResource, existingResource)
                    || hasSingleResourceBeenStored(newResource, existingResource)) {

                alreadyStoredAVariant = true;
                break;
            }
        }

        return alreadyStoredAVariant;
    }

    private boolean hasSingleResourceBeenStored(final Resource newResource,
            final Resource existingResource) {

        if (isPartOfAPackage(newResource)) {
            return false;
        }

        // It's not a Javascript package request
        return existingResource.getOriginalPath().equals(newResource.getOriginalPath());
    }

    private boolean hasPackageResourceBeenStored(final Resource newResource,
            final Resource existingResource) {

        if (!isPartOfAPackage(newResource)) {
            return false;
        }

        // compare on filename
        final int lastSlashPos = existingResource.getNewPath().lastIndexOf("/");
        final String existingFilename = existingResource.getNewPath().substring(
                lastSlashPos);

        // new request is for same filename as already in the list
        return newResource.getNewPath().endsWith(existingFilename);
    }

    private File getPackageDir(final String requestedResourcePath,
            final Group group) throws IllegalStateException {

        final String requestedGroupResourcePath =
            insertGroupNameAndDeploymentVersionIntoPath(requestedResourcePath, group);
        return new File(getRootResourcesDir(), FilenameUtils
                .getPath(requestedGroupResourcePath));
    }

    private List<Resource> findBundleResources(
            final String requestedResourcePath, final Group group) {

        try {
            final File packageDir = getPackageDir(requestedResourcePath, group);
            return doFindBundleResources(requestedResourcePath, packageDir);
        } catch (final IOException e) {
            throw new ResourceResolutionRuntimeException(
                    "Unexpected error when resolving requested resource '"
                    + requestedResourcePath + "' for group " + group, e);
        }
    }

    private List<Resource> doFindBundleResources(
            final String requestedResourcePath,
            final File javascriptFilesBaseDir) throws IOException {

        debugFindingBundleResourcesIn(javascriptFilesBaseDir);

        final List<Resource> result = new ArrayList<Resource>();

        final List<File> foundFiles =
            getJavaScriptFileFinder().findFiles(javascriptFilesBaseDir);
        if (foundFiles != null) {
            for (final File file : foundFiles) {
                final Resource currResource =
                    createResource(requestedResourcePath,
                            getRootResourceDirRelativePath(file));
                result.add(currResource);
            }
        }
        return result;
    }

    private String getRootResourceDirRelativePath(final File file) {
        String rootResourceDirRelativePath =
            StringUtils.substringAfter(file.getPath(),
                    getRootResourcesDir().getPath());
        rootResourceDirRelativePath =
            rootResourceDirRelativePath.replace(File.separator, SEPARATOR);

        if (rootResourceDirRelativePath.startsWith(SEPARATOR)) {
            return StringUtils.substringAfter(rootResourceDirRelativePath,
                    SEPARATOR);
        } else {
            return rootResourceDirRelativePath;
        }
    }

    private boolean isPackageRequested(final String requestedResourcePath) {
        return requestedResourcePath.endsWith(getAbstractPathPackageKeyword());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRealResourcePathExtension() {
        return ".js";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDebugResourceTypeName() {
        return "JavaScript";
    }

    private JavaScriptFileFinder getJavaScriptFileFinder() {
        return javaScriptFileFinder;
    }

    private String getAbstractPathPackageKeyword() {
        return abstractPathPackageKeyword;
    }

    private void debugFindingBundleResourcesIn(final File javascriptFilesBaseDir) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Finding bundle resources in: '" + javascriptFilesBaseDir + "'");
        }
    }
}
