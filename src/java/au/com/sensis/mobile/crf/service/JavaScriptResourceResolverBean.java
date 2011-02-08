package au.com.sensis.mobile.crf.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import au.com.sensis.mobile.crf.config.Group;
import au.com.sensis.mobile.crf.exception.ResourceResolutionRuntimeException;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;

/**
 * {@link ResourceResolver} that maps abstract Script paths to real Script paths.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class JavaScriptResourceResolverBean extends AbstractMultipleResourceResolver {

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
     * @param commonParams
     *            Holds the common parameters used in constructing all {@link ResourceResolver}s.
     * @param abstractResourceExtension
     *            Extension of resources (eg. "css" or "crf") that this class
     *            knows how to resolve.
     * @param rootResourcesDir
     *            Root directory where the real resources that this resolver
     *            handles are stored.
     * @param resourceAccumulatorFactory
     *            Provides a {@link ResourceAccumulator} for this
     *            {@link ResourceResolver}.
     * @param abstractPathPackageKeyword
     *            Keyword recognised at the end of abstract paths that signifies
     *            a "package" of JavaScript is being requested.
     * @param javaScriptFileFinder
     *            {@link JavaScriptFileFinder} delegate to use to find a list of
     *            JavaScript files in a directory.
     */
    public JavaScriptResourceResolverBean(final ResourceResolverCommonParamHolder commonParams,
            final String abstractResourceExtension,
            final File rootResourcesDir,
            final ResourceAccumulatorFactory resourceAccumulatorFactory,
            final String abstractPathPackageKeyword,
            final JavaScriptFileFinder javaScriptFileFinder) {

        super(commonParams, abstractResourceExtension, rootResourcesDir,
                resourceAccumulatorFactory);

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
    protected List<Resource> doResolveForGroup(final String requestedResourcePath,
            final Device device, final Group group)
            throws ResourceResolutionRuntimeException {

        if (isPackageRequested(requestedResourcePath)) {
            return findPacakgeResources(requestedResourcePath, group);
        } else {
            return super.doResolveForGroup(requestedResourcePath, device, group);
        }
    }

    private File getPackageDir(final String requestedResourcePath,
            final Group group) throws IllegalStateException {

        final String requestedGroupResourcePath =
            insertGroupNameAndDeploymentVersionIntoPath(requestedResourcePath, group);
        return new File(getRootResourcesDir(), FilenameUtils
                .getPath(requestedGroupResourcePath));
    }

    private List<Resource> findPacakgeResources(
            final String requestedResourcePath, final Group group) {

        try {
            final File packageDir = getPackageDir(requestedResourcePath, group);
            return doFindPackageResources(requestedResourcePath, group, packageDir);
        } catch (final IOException e) {
            throw new ResourceResolutionRuntimeException(
                    "Unexpected error when resolving requested resource '"
                    + requestedResourcePath + "' for group " + group, e);
        }
    }

    private List<Resource> doFindPackageResources(
            final String requestedResourcePath, final Group group,
            final File javascriptFilesBaseDir) throws IOException {

        debugFindingPackageResourcesIn(javascriptFilesBaseDir);

        final List<Resource> result = new ArrayList<Resource>();

        final List<File> foundFiles =
            getJavaScriptFileFinder().findFiles(javascriptFilesBaseDir);
        if (foundFiles != null) {
            for (final File file : foundFiles) {
                final Resource currResource =
                    createResource(requestedResourcePath,
                            getRootResourceDirRelativePath(file), group);
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
    protected ResourceAccumulator createResourceAccumulator() {

        return getResourceAccumulatorFactory().getJavaScriptResourceAccumulator(
                getAbstractPathPackageKeyword());
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

    private void debugFindingPackageResourcesIn(final File javascriptFilesBaseDir) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Finding package resources in: '" + javascriptFilesBaseDir + "'");
        }
    }
}
